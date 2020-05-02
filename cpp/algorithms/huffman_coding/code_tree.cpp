#include "code_tree.hpp"

#include <queue>
#include <algorithm>

#include <prez/print_stuff.hpp>

using namespace std;


class IBitStream {
public:
  IBitStream(ifstream& in) : curByte(0), mask(0), in(in) {}
  bool readBit() {
    // Finished with this bit, need to read in another and reset the mask
    if (mask == 0) {
      in.read(reinterpret_cast<char*>(&curByte), sizeof(curByte));
      mask = 1 << (sizeof(mask)*8 - 1);
    }

    bool b = curByte & mask;
    mask >>= 1;
    return b;
  }

  uint8_t readByte() {
    uint8_t theByte = 0;
    for (size_t i = 0; i < 8; ++i) {
      theByte <<= 1;
      if (readBit()) {
        theByte |= 1;
      }
    }
    return theByte;
  }

private:
  uint8_t curByte;
  uint8_t mask;
  ifstream& in;
};


struct CodeTreePtrGtCmp {
  bool operator()(const CodeTree* t1, const CodeTree* t2) const noexcept {
    return t1->root_->freq_ > t2->root_->freq_;
  }
};


CodeTree CodeTree::build(const vector<size_t>& freqs)
{
  priority_queue<CodeTree*, vector<CodeTree*>, CodeTreePtrGtCmp> minHeap;
  for (size_t i = 0; i < BYTE_RANGE; ++i) {
    size_t freq = freqs[i];
    if (freq != 0) {
      minHeap.push(new CodeTree(freq, i));
    }
  }

  if (minHeap.size() <= 1) {
    throw invalid_argument("Code tree requires at least two different types of bytes");
  }


  while (minHeap.size() > 1) {
    // NOTE: Can't use unique ptrs because top() returns const reference
    CodeTree* tree1 = minHeap.top();
    minHeap.pop();
    CodeTree* tree2 = minHeap.top();
    minHeap.pop();
    minHeap.push(new CodeTree(move(*tree1), move(*tree2)));
    delete tree1;
    delete tree2;
  }

  CodeTree ret = move(*minHeap.top());
  delete minHeap.top();

  return ret;
}

CodeTree::CodeTree(const Node* root) : root_(root) {}

CodeTree::CodeTree(CodeTree&& left, CodeTree&& right)
  : root_(new Node{left.root_->freq_ + right.root_->freq_, left.root_, right.root_})
{
  left.root_ = nullptr;
  right.root_ = nullptr;
}

CodeTree::CodeTree(size_t freq, uint8_t byte)
 : root_(new LeafNode{freq, nullptr, nullptr, byte})
{}

CodeTree::LeafNode::LeafNode(size_t freq, const Node* left, const Node* right, uint8_t byte)
  : Node{freq, left, right}, byte_(byte)
{}

CodeTree::~CodeTree()
{
  if (root_) {
    delete root_;
  }
}

CodeTree::Node::~Node()
{
  if (left_) {
    delete left_;
  }
  if (right_) {
    delete right_;
  }
}

CodeTree::CodeTree(CodeTree&& other)
  : root_(other.root_)
{
  other.root_ = nullptr;
}

CodeTree& CodeTree::operator=(CodeTree&& other)
{
  root_ = other.root_;
  other.root_ = nullptr;
  return *this;
}

vector<vector<bool>> CodeTree::getByteMapping() const
{
  vector<vector<bool>> mappings(BYTE_RANGE);
  vector<bool> initPath;
  addPaths(root_, initPath, mappings);
  return mappings;
}

void CodeTree::addPaths(const Node* node, vector<bool>& currentPath, vector<vector<bool>>& mappings) const
{
  // Leaf node, add the byte -> path mapping
  if (node->left_ == nullptr) {
    mappings[static_cast<const LeafNode*>(node)->byte_] = move(currentPath);
    return;
  }

  // Otherwise, add a 0 (false) for left and a 1 (true) for right
  vector<bool> leftPath = currentPath;
  leftPath.push_back(false);
  vector<bool> rightPath = move(currentPath);
  rightPath.push_back(true);

  addPaths(node->left_, leftPath, mappings);
  addPaths(node->right_, rightPath, mappings);
}


void CodeTree::toBits(ofstream& out) const {
  vector<bool> bits;
  root_->toBits(bits);
  size_t len = bits.size();

  for (size_t i = 0; i < len; i += 8) {
    uint8_t theByte = bitsToByte(bits, i);
    out.write(reinterpret_cast<char*>(&theByte), sizeof(theByte));
  }
}


void CodeTree::Node::toBits(vector<bool>& bits) const {
  // Leaf
  if (!left_) {
    bits.push_back(true);
    byteToBits(static_cast<const LeafNode*>(this)->byte_, bits);
  } else {
    bits.push_back(false);
    left_->toBits(bits);
    right_->toBits(bits);
  }
}


CodeTree CodeTree::fromBits(ifstream& in) {
  IBitStream inBits(in);
  // return CodeTree(nullptr);
  return CodeTree(Node::fromBits(inBits));
}

CodeTree::Node* CodeTree::Node::fromBits(IBitStream& inBits) {
  // Leaf
  if (inBits.readBit()) {
    return new LeafNode{0, nullptr, nullptr, inBits.readByte()};
  }

  Node* left = fromBits(inBits);
  Node* right = fromBits(inBits);
  return new Node{0, left, right};
}


void CodeTree::decode(size_t nbytes, ifstream& in, ofstream& out) const {
  IBitStream inBits(in);
  const Node* currentNode = root_;

  size_t count = 0;
  while (count != nbytes) {
    // We required more than one type of byte in build(), so the root will not be a leaf
    currentNode = inBits.readBit() ? currentNode->right_ : currentNode->left_;
    // Reached a leaf
    if (!currentNode->left_) {
      uint8_t theByte = static_cast<const LeafNode*>(currentNode)->byte_;
      out.write(reinterpret_cast<char*>(&theByte), sizeof(theByte));
      currentNode = root_;
      ++count;
    }
  }
}


void byteToBits(uint8_t theByte, vector<bool>& bits) {
  uint8_t mask = 1 << 7;
  while (mask > 0) {
    if (theByte & mask) {
      bits.push_back(true);
    } else {
      bits.push_back(false);
    }
    mask >>= 1;
  }
}


uint8_t bitsToByte(const vector<bool>& bits, const size_t pos) {
  uint8_t theByte = 0;
  const size_t len = min(bits.size() - pos, (size_t) 8);
  for (size_t i = pos; i < pos + len; ++i) {
    theByte <<= 1;
    if (bits[i]) {
      theByte |= 1;
    }
  }

  // Pad the last byte with 0s
  for (size_t i = 0; i < 8 - len; ++i) {
    theByte <<= 1;
  }

  return theByte;
}

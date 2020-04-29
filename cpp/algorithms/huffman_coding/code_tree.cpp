#include "code_tree.hpp"

#include <queue>

using namespace std;

struct CodeTreePtrGtCmp {
  bool operator()(const CodeTree* t1, const CodeTree* t2) const noexcept {
    return t1->root_->freq_ > t2->root_->freq_;
  }
};

CodeTree CodeTree::build(const std::vector<size_t>& freqs)
{
  priority_queue<CodeTree*, vector<CodeTree*>, CodeTreePtrGtCmp> minHeap;
  for (uint8_t i = 0; i < BYTE_RANGE; ++i) {
    size_t freq = freqs[i];
    if (freq != 0) {
      minHeap.push(new CodeTree(freq, i));
    }
  }


  while (minHeap.size() > 1) {
    // NOTE: Can't use unique ptrs because top() return const reference
    CodeTree* tree1 = minHeap.top();
    // cout << *tree1 << endl;
    minHeap.pop();
    CodeTree* tree2 = minHeap.top();
    // cout << *tree2 << endl;
    minHeap.pop();
    minHeap.push(new CodeTree(move(*tree1), move(*tree2)));
    delete tree1;
    delete tree2;
  }

  CodeTree ret = move(*minHeap.top());
  delete minHeap.top();

  return ret;
}

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

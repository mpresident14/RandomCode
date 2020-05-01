#ifndef CODE_TREE_HPP
#define CODE_TREE_HPP

#include <cstddef>
#include <vector>
#include <stdint.h>
#include <iostream>

uint8_t bitsToByte(const std::vector<bool>& bits, const size_t pos);
void byteToBits(uint8_t theByte, std::vector<bool>& bits);

struct CodeTreePtrGtCmp;

class CodeTree {
public:
  static CodeTree build(const std::vector<size_t>& freqs);
  static CodeTree fromBits(const std::vector<bool>& bits, size_t& pos);

  ~CodeTree();
  CodeTree(const CodeTree& other) = delete;
  CodeTree& operator=(const CodeTree& other) = delete;
  CodeTree(CodeTree&& other);
  CodeTree& operator=(CodeTree&& other);
  std::vector<std::vector<bool>> getByteMapping() const;
  std::vector<bool> toBits() const;
  std::vector<uint8_t> decode(const std::vector<bool>& bits, size_t pos) const;
  // friend std::ostream& operator<<(std::ostream& out, const CodeTree& tree) {
  //   tree.root_->toStream(out);
  //   return out;
  // }
  friend struct CodeTreePtrGtCmp;

  static constexpr size_t BYTE_RANGE = 1 << (sizeof(uint8_t) * 7); // 128

private:
  struct Node {
    static Node* fromBits(const std::vector<bool>& bits, size_t& pos);

    ~Node();
    void toBits(std::vector<bool>&) const;
    // virtual void toStream(std::ostream& out) const {
    //   out << freq_ << '\n';
    //   if (left_) {
    //     out << '\t';
    //     left_->toStream(out);
    //   }
    //   if (right_) {
    //     right_->toStream(out);
    //   }
    // }

    size_t freq_;
    const Node* left_;
    const Node* right_;
  };

  struct LeafNode : Node {
    LeafNode(size_t freq, const Node* left, const Node* right, uint8_t byte);
    // virtual void toStream(std::ostream& out) const override {
    //   out << freq_ << " : " << (char) byte_ << '\n';
    //   if (left_) {
    //     out << '\t';
    //     left_->toStream(out);
    //   }
    //   if (right_) {
    //     right_->toStream(out);
    //   }
    // }
    uint8_t byte_;
  };

  CodeTree(const Node* root);
  CodeTree(size_t freq, uint8_t byte);
  CodeTree(CodeTree&& left, CodeTree&& right);
  void addPaths(const Node* node, std::vector<bool>& currentPath, std::vector<std::vector<bool>>& mappings) const;

  const Node* root_;


};

#endif

#include "huffman.hpp"
#include "code_tree.hpp"

#include <fstream>
#include <sstream>

#include <prez/print_stuff.hpp>

using namespace std;

vector<size_t> getFrequencies(istream& in) {
  return getFrequencies(string(istreambuf_iterator<char>{in}, istreambuf_iterator<char>{}));
}

vector<size_t> getFrequencies(const string& input) {
  vector<size_t> freqs(CodeTree::BYTE_RANGE, 0);
  for (uint8_t c : input) {
    ++freqs[c];
  }
  return freqs;
}

void writeBits(const vector<bool>& outBits, ostream& out) {
  size_t len = outBits.size();

  size_t i;
  for (i = 0; i + 8 < len; i += 8) {
    uint8_t curByte = 0;
    for (size_t j = 0; j < 8; ++j) {
      curByte <<= 1;
      if (outBits[i + j]) {
        curByte += 1;
      }
    }
    out << (size_t) curByte;
  }

  // Get leftover bits if the number of bits isn't divisible by 8
  // TODO: Need to encode number of leftover bits in file
  if (i != len) {
    uint8_t curByte = 0;
    for (; i < len; ++i) {
      curByte <<= 1;
      if (outBits[i]) {
        curByte += 1;
      }
    }
    // Pad the last byte with 0s
    curByte <<= len + 8 - i;
    out << (size_t) curByte;
  }
}

void compress(std::istream& in) {
  compress(string(istreambuf_iterator<char>{in}, istreambuf_iterator<char>{}));
}

void compress(const std::string& input) {
  const vector<size_t> freqs = getFrequencies(input);
  const CodeTree codeTree = CodeTree::build(freqs);
  const vector<vector<bool>> byteMapping = codeTree.getByteMapping();
  cout << byteMapping << endl;
  vector<bool> outBits;
  for (uint8_t c : input) {
    const vector<bool>& bits = byteMapping[c];
    outBits.insert(outBits.end(), bits.cbegin(), bits.cend());
  }
  cout << outBits << endl;

  cout << hex;
  writeBits(outBits, cout);
  cout << endl;
}

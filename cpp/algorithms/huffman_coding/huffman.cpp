#include "huffman.hpp"
#include "code_tree.hpp"

#include <fstream>
#include <sstream>

#include <prez/print_stuff.hpp>

using namespace std;

// TODO: Files are likely large, so don't read all bytes into memory first

// vector<uint8_t> streamToBytes(const string& fileName) {
//   ifstream in;
//   in.open(fileName, in.binary);
//   if (!in.is_open()) {
//     throw invalid_argument("Could not open file " + fileName);
//   }

//   vector<uint8_t> bytes;
//   for_each(
//       istreambuf_iterator<char>{in},
//       istreambuf_iterator<char>{},
//       [&bytes](char b){ bytes.push_back((uint8_t) b); });
//   return bytes;
// }


vector<size_t> getFrequencies(ifstream& in) {
  vector<size_t> freqs(CodeTree::BYTE_RANGE, 0);
  for_each(
      istreambuf_iterator<char>{in},
      istreambuf_iterator<char>{},
      [&freqs](char c){ ++freqs[static_cast<uint8_t>(c)]; });
  return freqs;
}


void compress(ifstream& in, ofstream& out) {
  const vector<size_t> freqs = getFrequencies(in);
  const CodeTree codeTree = CodeTree::build(freqs);
  const vector<vector<bool>> byteMapping = codeTree.getByteMapping();
  vector<bool> outBits = codeTree.toBits();

  // Encode each byte of input
  for_each(
      istreambuf_iterator<char>{in},
      istreambuf_iterator<char>{},
      [&outBits, &byteMapping](char c){
        const vector<bool>& bits = byteMapping[static_cast<uint8_t>(c)];
        outBits.insert(outBits.end(), bits.cbegin(), bits.cend()); // TODO: Shouldn't be in memory
      });

  // Store number of bits that will be left over at the end
  uint8_t numBitsLeftOver = outBits.size() % 8;
  out.write(reinterpret_cast<char*>(&numBitsLeftOver), sizeof(numBitsLeftOver));

  size_t len = outBits.size();
  for (size_t i = 0; i < len; i += 8) {
    uint8_t theByte = bitsToByte(outBits, i);
    out.write(reinterpret_cast<char*>(&theByte), sizeof(theByte));
  }
}


void compress(const string& fileName) {
  ifstream in;
  in.open(fileName, in.binary);
  if (!in.is_open()) {
    throw invalid_argument("Could not open file " + fileName);
  }

  ofstream out(fileName + ".huff");
  compress(in, out);
}


void decompress(ifstream& in, ofstream& out) {
  // Read the number of bits that will be leftover at the end
  uint8_t numBitsLeftOver;
  in.read(reinterpret_cast<char*>(&numBitsLeftOver), sizeof(numBitsLeftOver));

  // Convert to bits
  vector<bool> bits;
  size_t len = input.size();
  for (size_t i = 1; i < len - 1; ++i) {
    byteToBits(input[i], bits);
  }

  // Grab valid bits of last byte
  uint8_t lastByte = input[len - 1];
  uint8_t mask = 1 << 7;
  for (size_t i = 0; i < numBitsLeftOver; ++i) {
    if (lastByte & mask) {
      bits.push_back(true);
    } else {
      bits.push_back(false);
    }
    mask >>= 1;
  }

  size_t pos = 0;
  const CodeTree codeTree = CodeTree::fromBits(bits, pos);
  cout << pos << endl;
  cout << codeTree.getByteMapping() << endl;
  vector<uint8_t> output = codeTree.decode(bits, pos);
  out.write((char*) output.data(), output.size() * sizeof(uint8_t));
}


void decompress(const string& fileName) {
  const char ext[] = ".huff";
  if (!fileName.ends_with(ext)) {
    throw invalid_argument("Unknown file extension");
  }

  ifstream in;
  in.open(fileName, in.binary);
  if (!in.is_open()) {
    throw invalid_argument("Could not open file " + fileName);
  }

  ofstream out(fileName.substr(0, fileName.size() - sizeof(ext) + 1));
  decompress(in, out);
}

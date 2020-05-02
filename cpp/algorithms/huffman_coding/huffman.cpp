#include "huffman.hpp"
#include "code_tree.hpp"

#include <fstream>
#include <sstream>
#include <numeric>

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


void extendAndMaybeFlush(vector<bool>& bits, const vector<bool>& newBits, ofstream& out) {
  bits.insert(bits.end(), newBits.cbegin(), newBits.cend());

  // constexpr size_t capacity = 1 << 23; // 2^20 = bytes/MB * 2^3 bits/byte = 2^23 bits/MB
  // size_t len = bits.size();
  // // In-memory size exceeded, flush all full bytes to file
  // if (len > capacity) {
  //   size_t i;
  //   for (i = 0; i + 8 < len; i += 8) {
  //     uint8_t theByte = bitsToByte(bits, i);
  //     out.write(reinterpret_cast<char*>(&theByte), sizeof(theByte));
  //   }

  //   // Copy the leftover bits to the start of the vector
  //   copy(bits.begin() + i, bits.end(), bits.begin());
  // }
}


void compress(ifstream& in, ofstream& out) {
  const vector<size_t> freqs = getFrequencies(in);
  size_t nbytes = accumulate(freqs.cbegin(), freqs.cend(), 0);
  // Store original file size
  out.write(reinterpret_cast<char*>(&nbytes), sizeof(nbytes));

  // Write code tree into file
  const CodeTree codeTree = CodeTree::build(freqs);
  codeTree.toBits(out);

  // Encode each byte of input
  vector<bool> outBits;
  const vector<vector<bool>> byteMapping = codeTree.getByteMapping();
  // Reset stream from getFrequencies
  in.seekg(0);
  for_each(
      istreambuf_iterator<char>(in),
      istreambuf_iterator<char>(),
      [&outBits, &byteMapping, &out](char c){
        const vector<bool>& newBits = byteMapping[static_cast<uint8_t>(c)];
        extendAndMaybeFlush(outBits, newBits, out);
      });

  // Flush the rest of the encodings
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
  // Read the size of the original file
  size_t nbytes;
  in.read(reinterpret_cast<char*>(&nbytes), sizeof(nbytes));

  // Read in the code tree
  const CodeTree codeTree = CodeTree::fromBits(in);

  // Use the code tree to decode and write the compressed bytes
  codeTree.decode(nbytes, in, out);
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

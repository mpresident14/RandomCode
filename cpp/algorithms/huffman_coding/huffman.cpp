#include "huffman.hpp"
#include "code_tree.hpp"

#include <fstream>
#include <sstream>
#include <numeric>

#include <prez/print_stuff.hpp>

using namespace std;


class OEncodingsStream {
public:
  static constexpr size_t maxEncodings = 1 << 20; // 1 million 8-byte ptrs = 8MB

  OEncodingsStream(ofstream& out) : out(out) {
    encodings.reserve(maxEncodings);
  }

  void flushEncodings() {
    if (encodings.empty()) {
      return;
    }

    size_t vecNum = 0;
    size_t i = 0;
    size_t len = encodings.size();
    const vector<bool>* curVec = encodings[0];

    // Write a single bit per iteration
    while (true) {
      // Finished with this encoding, move to the next one
      if (i == curVec->size()) {
        // Last vector, nothing more to write
        if (vecNum == len - 1) {
          break;
        }
        curVec = encodings[++vecNum];
        i = 0;
      }
      // Finished with this byte, need to write it
      if (shifts == 8) {
        out.write(reinterpret_cast<char*>(&curByte), sizeof(curByte));
        curByte = 0;
        shifts = 0;
      }

      curByte <<= 1;
      if ((*curVec)[i++]) {
        curByte |= 1;
      }
      ++shifts;
    }

    // We have last partial byte saved in curByte, so we can clear this vector
    encodings.clear();
    encodings.reserve(maxEncodings);
  }


  void completeFlush() {
    flushEncodings();
    if (shifts != 0) {
      // Pad final byte with 0s
      curByte <<= 8 - shifts;
      out.write(reinterpret_cast<char*>(&curByte), sizeof(curByte));
      curByte = 0;
      shifts = 0;
    }
  }


  void addEncoding(const vector<bool>* encoding) {
    encodings.push_back(encoding);
    if (encodings.size() > maxEncodings) {
      flushEncodings();
    }
  }

private:
  vector<const vector<bool>*> encodings;
  ofstream& out;
  uint8_t curByte = 0;
  size_t shifts = 0;
};


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
  size_t nbytes = accumulate(freqs.cbegin(), freqs.cend(), 0);
  // Store original file size
  out.write(reinterpret_cast<char*>(&nbytes), sizeof(nbytes));

  // Write code tree into file
  const CodeTree codeTree = CodeTree::build(freqs);
  codeTree.toBits(out);

  // Encode each byte of input
  const vector<vector<bool>> byteMapping = codeTree.getByteMapping();

  OEncodingsStream estream(out);
  // Reset stream from getFrequencies
  in.seekg(0);
  char c = in.get();
  while (!in.eof()) {
    estream.addEncoding(&byteMapping[static_cast<uint8_t>(c)]);
    c = in.get();
  }
  estream.completeFlush();
}


void compress(const string& fileName) {
  ifstream in(fileName, ios::binary);
  if (!in.is_open()) {
    throw invalid_argument("Could not open file " + fileName);
  }

  ofstream out(fileName + ".huff", ios::binary);
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

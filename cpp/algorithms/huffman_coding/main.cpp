#include "huffman.hpp"

#include <iostream>
#include <sstream>
#include <unistd.h>

#include <prez/print_stuff.hpp>

using namespace std;

int main(int argc, char** argv)
{
  int opt;
  bool shouldDecompress = false;
  char* fileName;

  while ((opt = getopt(argc, argv, "df:")) != -1) {
    switch (opt) {
      case 'd':
        shouldDecompress = true;
        break;
      case 'f':
        fileName = optarg;
        break;
      default:
        cerr << "Usage: " << argv[0] << " [-f fileName] [-d]" << endl;
        exit(EXIT_FAILURE);
    }
  }

  if (shouldDecompress) {
    decompress(fileName);
  } else {
    compress(fileName);
  }
}

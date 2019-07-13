#include <iostream>
#include <stdlib.h>

using namespace std;

int main(int argc, char** argv)
{
  if (argc != 2) {
    cout << "Needs 1 integer argument." << endl;
    exit(EXIT_FAILURE);
  }

  cout << "Your input: " << atoi(argv[1]) << endl;
  
  return 0;
}
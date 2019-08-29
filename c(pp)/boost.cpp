// https://www.boost.org/doc/libs/1_55_0/more/getting_started/unix-variants.html
// Does not include any binaries that must be built

#include <iostream>
#include <fstream>
#include <cstddef>
#include <boost/algorithm/is_palindrome.hpp>

// Windows: g++ -I/cygdrive/c/boost_1_67_0/ -g -Wall -Wextra -std=c++11 -pedantic -o boost boost.cpp
// Ubuntu: clang++ -std=c++1z -I/usr/local/include/boost_1_71_0 boost.cpp -o boost

int main()
{
  std::cout << boost::algorithm::is_palindrome("rotator") << std::endl;
  return 0;
}

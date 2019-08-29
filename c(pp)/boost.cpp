#include <iostream>
#include <fstream>
#include <cstddef>
// #include <boost/algorithm/is_palindrome.hpp>
#include <boost/archive/tmpdir.hpp>

#include <boost/archive/text_iarchive.hpp>
#include <boost/archive/text_oarchive.hpp>
#include <boost/archive/detail/common_iarchive.hpp>

#include <boost/serialization/base_object.hpp>
#include <boost/serialization/utility.hpp>
#include <boost/serialization/list.hpp>
#include <boost/serialization/assume_abstract.hpp>

// g++ -I/cygdrive/c/boost_1_67_0/ -g -Wall -Wextra -std=c++11 -pedantic -o boost boost.cpp

class Test {
  public:
    friend class boost::serialization::access;

    Test(int x, int y)
      : x_{x}, y_{y} {}
    
    template<typename Archive>
    void serialize(Archive& ar, const unsigned int version)
    {
      ar & x_;
      ar & y_;
    }

  
  private:
    int x_;
    int y_;

};

int main()
{
  std::ofstream ofs("boost_serialization.txt");
  Test test{4,3};

  boost::archive::text_oarchive oa(ofs);
  oa << test;

  // std::cout << boost::algorithm::is_palindrome("rotator") << std::endl;

  return 0;
}
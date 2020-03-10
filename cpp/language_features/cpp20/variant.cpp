#include <iostream>
#include <string>
#include <cstddef>

#include <boost/type_index.hpp>


#include <variant>

using namespace std;
using namespace boost::typeindex;

class Widget {
public:
  Widget() {}
  string doStuff() { return "I'm a Widget!";}
};

class Test {
public:
  Test() {}
  string doStuff() { return "I'm a Test";}
};


template <typename... Args>
void runDoStuff(Args&&... args) {
  variant<Args...> arr[] = { (variant<Args...>(std::forward<Args>(args)))... };
  for (size_t i = 0; i < sizeof...(args); ++i) {
    // Return type of lambda must be the same for all alternatives in the variant
    cout << visit([](auto&& arg) { return arg.doStuff(); }, arr[i]) << endl;
  }
}

int main()
{
  runDoStuff(Widget(), Test());
  return 0;
}

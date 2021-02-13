#include <iostream>
#include <string>
#include <vector>

// https://abseil.io/tips/49
// https://en.cppreference.com/w/cpp/language/namespace
// https://en.cppreference.com/w/cpp/language/unqualified_lookup

/********************
 * USING DIRECTIVES *
 ********************/

// Suppose this declaration was included from some other file. Now the reference
// below may be ambiguous, or, even worse, resolve to the unintended overload.
// "For the purpose of unqualified name lookup, all
// declarations from a namespace nominated by a using directive appear as if
// declared in the nearest enclosing namespace which contains, directly or
// indirectly, both the using-directive and the nominated namespace."
// - SOME INCLUDED FILE -
std::string show(int) { return "global"; }

// - MY CODE -
namespace other {
std::string show(size_t) { return "other"; }
} // namespace other

namespace outer {
namespace inner {
using namespace other;

std::string callShow() { return show(3); }

} // namespace inner
} // namespace outer


/*******
 * ADL *
 *******/
template <typename T>
std::ostream& operator<<(std::ostream& out, const std::vector<T>&) {
  // My implementation ...
  return out;
}
namespace mylib {

class MyClass {
  void f() {
    std::vector<int> v;
    // This line will not compile if we uncomment the operator<< below because
    // it is called in the mylib namespace, so name lookup stops when it sees
    // MyClass operator<<. And ADL doesn't find it since ostream and vector are
    // in the std namespace.
    std::cout << v << std::endl;
  }
};

// std::ostream &operator<<(std::ostream &out, const MyClass &c) {
//   // My implementation ...
//   return out;
// }

} // namespace mylib

void f() {
  std::vector<int> v;
  // This lines will compile regardless because it is declared in the
  // global namespace, so lookup finds the vector operator<<.
  std::cout << v << std::endl;
}

int main() {
  std::cout << outer::inner::callShow() << std::endl;
  return 0;
}

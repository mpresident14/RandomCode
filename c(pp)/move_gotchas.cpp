#include "widget.hpp"

#include <iostream>
#include <string>

using namespace std;

// Since std::move(w) is not the same as w, the compiler cannot use RVO.
// Results in an extra call to the move constructor.
// Clang even gives a nice warning.
Widget create_widget_bad(int n) {
  Widget w{string{(char) n}};  
  return std::move(w);
}

class Thing {
 public:
  // w is const, so move(w) creates a const Widget rvalue. The constness
  // prevents it from being passed to the move constructor. However, the copy
  // constructor is happy to accept it.
  Thing(const Widget w) : w_{move(w)} {};

 private:
  Widget w_;
};

int main() {
  Widget w;
  cout << endl;

  cout << "Construct w/ lvalue" << endl;
  Thing t1{w};
  cout << endl;

  cout << "Good return local (RVO occurs)" << endl;
  w = Widget::create_widget(4);
  cout << endl;

  cout << "Bad return local (RVO inhibited)" << endl;
  w = create_widget_bad(4);
  cout << endl;
}
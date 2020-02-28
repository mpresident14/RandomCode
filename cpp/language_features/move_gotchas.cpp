#include "../widget.hpp"

#include <iostream>
#include <string>

using namespace std;

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
}
#include "widget.hpp"

using namespace std;

void f1(Widget w) { w.work(5); }

void f2(const Widget& w) { w.work(5); }

void f3(const Widget w) { w.work(5); }

template <typename T>
void f4(T&& item) {
  // TODO: print the type of the reference
  item.work(5);
}

int main() {
  cout << "(Widget) lvalue" << endl;
  Widget w1a;
  f1(w1a);
  cout << endl;

  cout << "(Widget) rvalue" << endl;
  Widget w1b;
  f1(move(w1b));
  cout << endl;

  cout << "(const Widget&) lvalue" << endl;
  Widget w2a;
  f2(w2a);
  cout << endl;

  cout << "(const Widget&) rvalue" << endl;
  Widget w2b;
  f2(move(w2b));
  cout << endl;

  cout << "(const Widget) lvalue" << endl;
  Widget w3a;
  f3(w3a);
  cout << endl;

  cout << "(const Widget) rvalue" << endl;
  Widget w3b;
  f3(move(w3b));
  cout << endl;

  cout << "(T&&) lvalue" << endl;
  Widget w4a;
  f4(w4a);
  cout << endl;

  cout << "(T&&) rvalue" << endl;
  Widget w4b;
  f4(move(w4b));
  cout << endl;
}
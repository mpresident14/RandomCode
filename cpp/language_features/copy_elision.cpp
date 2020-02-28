#include "../widget.hpp"

#include <iostream>

using namespace std;


// The local variable variable prac is created directly in the parent stack
// frame, rather than in its own return frame. This "elides" the temporary
// copy. Also known as (named) return value optimization.
Widget makeWidgetReturnLocal()
{
    Widget w;
    return w;
}

// In a situation with multiple local vars that can possibly be returned, copy
// elision is difficult for the compiler. In this case, the compiler is
// required to treat returning a local var as if it were written "return
// std::move(local_var)". Thus, it calls the move constructor.
Widget makeWidgetReturnLocalBranched(int n) {
    Widget local1;
    Widget local2;
    
    if (n % 2 == 0) {
      return local1;
    } else {
      return local2;
    }
}

// Returned rvalue created in parent stack frame.
Widget makeWidgetReturnRvalueBranched(int n) {
    if (n % 2 == 0) {
      return Widget{};
    } else {
      return Widget{};
    }
}

// Since move(w) is not the same as w, the compiler cannot use RVO.
// Results in an extra call to the move constructor.
// Clang even gives a nice warning let you know you done messed up.
Widget makeWidgetBad(int n) {
  Widget w;
  return move(w);
}

// RVO works for by-value parameters as well
void acceptWidget(Widget w) {
  w.work(4);
}

Widget acceptAndReturnWidget(Widget w) {
  w.work(4);
  return w;
}

struct Thing {
  Widget w_;
};


// No RVO when returning member variables.
Widget makeWidgetStructMember() {
  return Thing().w_;
}


int main()
{
    cout << "makeWidgetReturnLocal()" << endl;
    Widget w1 = makeWidgetReturnLocal();
    cout << endl;

    cout << "makeWidgetReturnLocalBranched()" << endl;
    Widget w2 = makeWidgetReturnLocalBranched(4);
    cout << endl;

    cout << "makeWidgetReturnRvalueBranched()" << endl;
    Widget w3 = makeWidgetReturnRvalueBranched(4);
    cout << endl;

    cout << "makeWidgetBad()" << endl;
    Widget w4 = makeWidgetBad(4);
    cout << endl;

    cout << "makeWidgetStructMember()" << endl;
    Widget w5 = makeWidgetStructMember();
    cout << endl;

    cout << "acceptWidget()" << endl;
    acceptWidget(Widget{});
    cout << endl;

    cout << "acceptAndReturnWidget() rvalue" << endl;
    acceptWidget(Widget{});
    cout << endl;

    cout << "acceptAndReturnWidget() lvalue" << endl;
    acceptWidget(w1);
    cout << endl;
}


/* OUTPUT */
// makeWidgetReturnLocal()
// Default Constructor: 0x7fffd70e5ef8

// makeWidgetReturnLocalBranch()
// Default Constructor: 0x7fffd70e5e30
// Default Constructor: 0x7fffd70e5e18
// Move Constructor: 0x7fffd70e5ed0
// Destructor: 0x7fffd70e5e18
// Destructor: 0x7fffd70e5e30

// makeWidgetReturnRvalue()
// Default Constructor: 0x7fffd70e5eb8

// Destructor: 0x7fffd70e5eb8
// Destructor: 0x7fffd70e5ed0
// Destructor: 0x7fffd70e5ef8
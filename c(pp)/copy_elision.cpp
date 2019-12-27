#include "widget.hpp"

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
Widget makeWidgetReturnLocalBranch(int n) {
    Widget local1;
    Widget local2;
    
    if (n % 2 == 0) {
      return local1;
    } else {
      return local2;
    }
}

// Returned rvalue created in parent stack frame.
Widget makeWidgetReturnRvalue(int n) {
    if (n % 2 == 0) {
      return Widget{};
    } else {
      return Widget{};
    }
}

int main()
{
    cout << "makeWidgetReturnLocal()" << endl;
    Widget w1 = makeWidgetReturnLocal();
    cout << endl;

    cout << "makeWidgetReturnLocalBranch()" << endl;
    Widget w2 = makeWidgetReturnLocalBranch(4);
    cout << endl;

    cout << "makeWidgetReturnRvalue()" << endl;
    Widget w3 = makeWidgetReturnRvalue(4);
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
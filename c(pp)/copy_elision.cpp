#include "widget.hpp"

#include <iostream>

using namespace std;

Widget makeWidgetReturnLocal()
{
    Widget w;
    return w;
}

Widget makeWidgetReturnLocalBranch(int n) {
    Widget local1;
    Widget local2;
    
    if (n % 2 == 0) {
      return local1;
    } else {
      return local2;
    }
}

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
    Widget w2 = makeWidgetReturnRvalue(4);
    cout << endl;
}

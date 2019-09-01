#include "widget.hpp"

#include <iostream>
#include <string>

using namespace std;

int main() {

	Widget w0 = "literal"s; 	// OK, Widget can be constructed from a std::string
	// Widget w1 = "literal"; // DOES NOT COMPILE, no implicit conversions

	Widget w1{"literal"};
	cout << endl;

	Widget w2 = w1;
	cout << endl;

	Widget w3{w1};
	cout << endl;

	Widget w4{Widget::create_widget(7)};
	cout << endl;

	Widget w5 = Widget::create_widget(7);
	cout << endl;

	Widget w6 = {Widget::create_widget(7)};
	cout << endl;

	Widget w7 = move(w1);
	cout << endl;
}

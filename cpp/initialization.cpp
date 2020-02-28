#include "widget.hpp"

#include <iostream>
#include <string>

using namespace std;

int main() {

	cout << "Widget w0 = \"literal\"s;" << endl;
	Widget w0 = "literal"s; 	// OK, Widget can be constructed from a std::string
	// Widget w1 = "literal"; // DOES NOT COMPILE, no implicit conversions
	cout << endl;

	cout << "Widget w1{\"literal\"};" << endl;
	Widget w1{"literal"};
	cout << endl;

	cout << "Widget w2 = w1;" << endl;
	Widget w2 = w1;
	cout << endl;

	cout << "Widget w3{w1};" << endl;
	Widget w3{w1};
	cout << endl;

	cout << "Widget w4{Widget::create_widget(7)};" << endl;
	Widget w4{Widget::create_widget(7)};
	cout << endl;

	cout << "Widget w5 = Widget::create_widget(7);" << endl;
	Widget w5 = Widget::create_widget(7);
	cout << endl;

	cout << "Widget w6 = {Widget::create_widget(7)};" << endl;
	Widget w6 = {Widget::create_widget(7)};
	cout << endl;

	cout << "Widget w7 = move(w1);" << endl;
	Widget w7 = move(w1);
	cout << endl;
}

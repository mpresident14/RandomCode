#include <iostream>
#include <cstddef>
#include <string>

#include <boost/type_index.hpp>

using namespace std;
using namespace boost::typeindex;

/**************************************************************************************
 * 1) decltype works as expected for names.
 * 2) decltype(auto) uses decltype rules for deduction rather than template deduction.
 * 3) decltype returns a T& for non-name lvalue expressions of a type T.
 **************************************************************************************/

int main()
{
    int n = 5;
    const int& crn = n;

    auto autoint = crn;
    decltype(auto) declautoint = crn;

    cout << "auto autoint = crn;" << endl;
    cout << "autoint = " << type_id_with_cvr<decltype(autoint)>().pretty_name() << endl;
    cout << endl;

    cout << "decltype(auto) declautoint = crn;" << endl;
    cout << "declautoint = " << type_id_with_cvr<decltype(declautoint)>().pretty_name() << endl;
    cout << endl;

    // See https://en.cppreference.com/w/cpp/language/value_category.
    cout << "*** Non-name lvalue expressions examples***" << endl;
    cout << "decltype(\"hello\") = " << type_id_with_cvr<decltype("hello")>().pretty_name() << endl;
    cout << "decltype((1, n)) = " << type_id_with_cvr<decltype((1, n))>().pretty_name() << endl;
    cout << "decltype((n)) = " << type_id_with_cvr<decltype((n))>().pretty_name() << endl;

    return 0;
}
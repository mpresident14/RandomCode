#include <iostream>
#include <cstddef>
#include <string>

#include <boost/type_index.hpp>

using namespace std;
using namespace boost::typeindex;

/*********************************************************
 * template<typename T>
 * void f(ParamType param);

 * f(expr);
 ********************************************************/

void printStars(const char* str)
{
    size_t len = strlen(str) + 4;
    string stars = string(len, '*');
    cout << stars << '\n' << "* " << str << " *" << '\n' << stars << endl;
}

/****************************************************************************
 * Case 1: ParamType is a reference or a pointer, but not a universal reference.
 *   1) If expr is a reference, ignore the reference part.
 *   2) Then pattern match.
 ****************************************************************************/

template<typename T>
void f1(T& param)
{
    cout << "T     = " << type_id_with_cvr<T>().pretty_name() << endl;
    cout << "param = " << type_id_with_cvr<decltype(param)>().pretty_name() << endl;
}

template<typename T>
void f2(const T& param)
{
    cout << "T     = " << type_id_with_cvr<T>().pretty_name() << endl;
    cout << "param = " << type_id_with_cvr<decltype(param)>().pretty_name() << endl;
}

/****************************************************************************
 * Case 2: ParamType universal reference.
 *   1) If expr is an lvalue, T and ParamType are lvalue references 
 *     (note the T& && -> T& reference collapsing).
 *   2) If expr is an rvalue, Case 1 applies.
*****************************************************************************/

template<typename T>
void g1(T&& param)
{
    cout << "T     = " << type_id_with_cvr<T>().pretty_name() << endl;
    cout << "param = " << type_id_with_cvr<decltype(param)>().pretty_name() << endl;
}

/****************************************************************************
 * Case 3: ParamType is neither a reference nor a pointer.
 *   1) Ignore reference, const, and volatile.
 *****************************************************************************/

template<typename T>
void h1(T param)
{
    cout << "T     = " << type_id_with_cvr<T>().pretty_name() << endl;
    cout << "param = " << type_id_with_cvr<decltype(param)>().pretty_name() << endl;
}

/******************************************************************************
 * Auto deduction can be transformed into type deduction.
 * For example, in "const auto cx = 5", T -> auto and ParamType -> const auto.
 * The only difference is that auto assumes that a braced initializer represents
 * a initializer_list, but template type deduction does not.
 * 
 * Also note that when auto is used in a function return type or in a lambda
 * parameter, template type deduction is implied, so
 * "auto f() { return {1, 2, 3}; }" will not compile.
 *****************************************************************************/

int main()
{
    int x = 1;
    const int cx = x;
    const int& crx = cx;

    // **********************************************

    const char *f1Dec = "f(T& param)";
    printStars(f1Dec);

    cout << "int -> " << f1Dec << endl;
    f1(x);
    cout << endl;

    cout << "const int -> " << f1Dec << endl;
    f1(cx);
    cout << endl;

    // Reference-ness is ignored
    cout << "const int& -> " << f1Dec  << endl;
    f1(crx);
    cout << endl;

    // **********************************************

    const char *f2Dec = "f2(const T& param)";
    printStars(f2Dec);

    cout << "int -> " << f2Dec << endl;
    f2(x);
    cout << endl;

    // Const-ness is ignored since param is already a reference-to-const
    cout << "const int -> " << f2Dec << endl;
    f2(cx);
    cout << endl;

    cout << "const int& -> " << f2Dec << endl;
    f2(crx);
    cout << endl;

    cout << "rvalue -> " << f2Dec << endl;
    f2(1);
    cout << endl;

    // **********************************************

    const char *g1Dec = "g1(T&& param)";
    printStars(g1Dec);

    cout << "int -> " << g1Dec << endl;
    g1(x);
    cout << endl;

    cout << "const int -> " << g1Dec << endl;
    g1(cx);
    cout << endl;

    cout << "const int& -> " << g1Dec << endl;
    g1(crx);
    cout << endl;

    cout << "rvalue -> " << g1Dec << endl;
    g1(1);
    cout << endl;

    // **********************************************

    const char *h1Dec = "h1(T param)";
    printStars(h1Dec);

    cout << "int -> " << h1Dec << endl;
    h1(x);
    cout << endl;

    cout << "const int -> " << h1Dec << endl;
    h1(cx);
    cout << endl;

    cout << "const int& -> " << h1Dec << endl;
    h1(crx);
    cout << endl;

    cout << "rvalue -> " << h1Dec << endl;
    h1(1);
    cout << endl;

    // **********************************************

    printStars("auto deduction");

    // Note that some compilers may deduce x as an initializer_list<int>, depending
    // whether they have implemented proposal N3922.
    auto m{1};
    cout << "auto m{1}" << endl;
    cout << "m = " << type_id_with_cvr<decltype(m)>().pretty_name() << endl;
    cout << endl;

    cout << "auto n = {1}" << endl;
    auto n = {1};
    cout << "n = " << type_id_with_cvr<decltype(n)>().pretty_name() << endl;
    cout << endl;

    return 0;
}
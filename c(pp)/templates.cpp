#include <iostream>
#include <string>
#include <type_traits>

using namespace std;

template<typename T>
struct Thing {
    /****************************************************************************
     * NOTE: The comment code below does not compile because default template
     * arguments are not accounted for in function template equivalence. Thus,
     * we get a redeclaration error. However, enable_if_t<is_integral<U>::value, int>
     * and enable_if_t<!is_integral<U>::value, int> are different types, so
     * the templates are not equivalent. Therefore, the non-commented code below
     * is valid.
     ****************************************************************************/
    // template<typename U, typename = enable_if_t<is_integral_v<U>>>
    // string g(U);
    // template<typename U, typename = enable_if_t<!is_integral_v<U>>>
    // string g(U);

    template<typename U, enable_if_t<is_integral_v<U>, int> = 0>
    string f(U);
    template<typename U, enable_if_t<!is_integral_v<U>, int> = 0>
    string f(U);

    /****************************************************************************
     * NOTE: we cannot do just "template<enable_if_t<is_integral_v<U>, int> = 0>"
     * because T is not deduced in the context of this function (it is deduced in
     * the context of the class). Thus, we need to supply the additional deduced
     * type U.
     ****************************************************************************/
    template<typename U = T, enable_if_t<is_integral_v<U>, int> = 0>
    string g();
    template<typename U = T, enable_if_t<!is_integral_v<U>, int> = 0>
    string g();



};

/****************************************************************************
 * NOTE: Out-of-line function definitions cannot respecify default arguments
 ****************************************************************************/
template<typename T>
template<typename U, enable_if_t<is_integral_v<U>, int>>
string Thing<T>::f(U) { return "f(): INTEGRAL"; }

template<typename T>
template<typename U, enable_if_t<!is_integral_v<U>, int>>
string Thing<T>::f(U) { return "f(): NOT INTEGRAL"; }


template<typename T>
template<typename U, enable_if_t<is_integral_v<U>, int>>
string Thing<T>::g() { return "g() INTEGRAL"; }

template<typename T>
template<typename U, enable_if_t<!is_integral_v<U>, int>>
string Thing<T>::g() { return "g() NOT INTEGRAL"; }


int main()
{
    Thing<int> tInt;
    Thing<string> tString;

    cout << tInt.f(5) << endl;
    cout << tString.f("") << endl;

    cout << tInt.g() << endl;
    cout << tString.g() << endl;
}

// SFINAE = Substitution failure is not an error
// https://en.cppreference.com/w/cpp/language/sfinae

#include <iostream>
#include <type_traits>
#include <string>

using namespace std;

/* Enable via template parameter */
// https://en.cppreference.com/w/cpp/language/template_parameters#Non-type_template_parameter
// We need the ", int>" because the default "void" is not a non-type template parameter

// Use std::enable_if to remove candidate functions from overload resolution.
// https://en.cppreference.com/w/cpp/types/enable_if
template<
    typename I, 
    enable_if_t<is_integral<I>::value, int> = 0
    >
void f(I num) 
{
    cout << num << " is an int"  << endl;
}

template<
    typename I, 
    enable_if_t<is_floating_point<I>::value, int> = 0
    >
void f(I num) 
{
    cout << num << " is an float"  << endl;
}

/* Enable via return type */
// Expression to the left of the decltype comma is being evaluated
// Expression to the right is the actual return type
template <typename C, typename F>
auto g(C c, F f) -> decltype((void)(c.*f)(), void())
{
    cout << "C is an object with a member function named f" << endl;
}

template <typename C, typename F>
auto g(C c, F f) -> decltype((void)(c->*f)(), void())
{
    cout << "C is an pointer to an object with a member function named f" << endl;
}

// We can use auto to type deduce the result of a call to a function
template<
    typename F, 
    typename ... ArgTypes
>
auto call(F f, ArgTypes ... args)
{
    return f(args...);
}


int main()
{
    f<int>(3);
    f<float>(3.2);

    struct typeA { void aFn() {} };
    typeA aObj;
    g(aObj, &typeA::aFn);
    g(&aObj, &typeA::aFn);

    int (*lambda)(int, bool) = [](int n, bool b) {return b ? n : n + 1;};
    // string s = call(lambda, 5, true);    FAILS TO COMPILE
    // int s = call(lambda, aObj, true);    FAILS TO COMPILE
    // int s = call(lambda, 5, true, 2.3);  FAILS TO COMPILE
    cout << call(lambda, 3, true) << endl;
}
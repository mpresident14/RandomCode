#include <iostream>
#include <type_traits>
#include <string>
#include <vector>

using namespace std;

int f()
{
    cout << "f() called" << endl;
    return 2;
}

int g()
{
    cout << "g() called" << endl;
    return 3;
}

// We can pass parameters as value or reference like normal using 
// Args, Args&, Args&&, const Arg&, etc.

// https://en.cppreference.com/w/cpp/language/fold
template <typename ... Args>
void foldOperator(Args ... args)
{
    // (1 - (2 - (3 - 4))) = -2
    int a = (args - ...);
    cout << "a = " << a << endl;

    // (((1 - 2) - 3) - 4) = -8
    int b = (... - args);
    cout << "b = " << b << endl;

    // (1 - (2 - (3 - (4 - 10)))) = -7
    // Ops must be the same
    int c = (args - ... - 10);
    cout << "c = " << c << endl;

    // ((((10 - 1) - 2) - 3) - 4) = 0
    int d = (10 - ... - args);
    cout << "d = " << d << endl;
}

// (a, b) computes a and b, discards a, and results in b
template <typename ... Args>
void commaOperator(Args&& ... args)
{
    int x = (f(), g());
    cout << "x = " << x << endl;

    // (((1, 2), 3), 4) = 4
    int y = (std::forward<Args>(args) , ...);
    cout << "y = " << y << endl;
}

template <typename ... Args>
void printArgs(Args&& ... args)
{
    // This expands to
    // (cout << arg1 << ", "), (cout << arg2 << ", "), etc.
    (..., (cout << std::forward<Args>(args) << ", ")) << endl;
}

template<typename T, typename... Args>
vector<T> pushBackVec(Args&&... args)
{
    vector<T> v;
    static_assert((is_constructible_v<T, Args&&> && ...));
    // This expands to
    // v.push_back(arg1), v.push_back(arg2), etc.
    (v.push_back(forward<Args>(args)), ...);
    return v;
}

int main()
{
    cout << "commaOperator()" << endl;
    commaOperator(1,2,3,4);
    cout << endl;

    cout << "foldOperator()" << endl;
    foldOperator(1,2,3,4);
    cout << endl;

    cout << "printArgs()" << endl;
    printArgs(12, "hello", 3.14152);
    cout << endl;

    cout << "initVec()" << endl;
    cout << "Vector has " << pushBackVec<int>(1,2,3,4).size() << " arguments" << endl;
}

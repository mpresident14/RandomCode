#include <vector>
#include <iostream>

using namespace std;

int main()
{
    vector<int> v = {1};
    const int& nRef = v[0];
    const int *nPtr = &v[0];
    auto iter = v.begin();

    cout << nRef << endl;
    cout << *nPtr << endl;
    cout << *iter << endl;

    for (size_t i = 0; i < 1000; ++i) {
        v.push_back(i);
    }

    // Iterators and references/pointers to objects in a vector are invalidated after insertion
    // because the vector may resize!
    cout << nRef << endl;
    cout << *nPtr << endl;
    cout << *iter << endl;
}

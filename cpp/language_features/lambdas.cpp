
// https://en.cppreference.com/w/cpp/language/lambdaalias

// More on lambdas and std::function:
// https://shaharmike.com/cpp/lambdas-and-functions/

#include <iostream>
#include <vector>
#include <forward_list>
#include <algorithm>

using namespace std;

void removeIf(vector<int>& list, bool (*f)(int)) {
  auto iter = list.begin();
  while (iter != list.end()) {
    if (f(*iter)) {
      iter = list.erase(iter);
    } else {
      ++iter;
    }
  }
}

int main() {
  // Lambda
  bool (*isEven)(int) = [](int val) {return val % 2 == 0;};

  // Remove from vector using my function
  vector<int> vec{1,3,5,2,4,6};
  removeIf(vec, isEven);

  for (auto& i : vec) {
    cout << i << endl;
  }
  cout << endl;

  // Remove from forward list using built in method
  forward_list<int> fwlist{1,2,3,4,5,6};
  fwlist.remove_if(isEven);
  for (auto& i : fwlist) {
    cout << i << endl;
  }
  cout << endl;
}

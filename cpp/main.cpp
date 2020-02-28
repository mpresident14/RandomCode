#include "arraylist.hpp"

#include <iostream>

using namespace std;

int main()
{
  ArrayList<int> myList;

  for (int i = 0; i < 10; i++) {
    myList.add(i);
  }

  myList.remove(4);
  myList.add(10);

  cout << "\n" << "INDEXING" << endl;
  for (size_t i = 0; i < myList.size(); i++) {
    cout << myList[i] << endl;
  }

  cout << "\n" << "ITERATOR" << endl;
  for (auto iter = myList.begin(); iter != myList.end(); ++iter) {
    cout << *iter << endl;
  }
}
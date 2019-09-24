#include "arraylist.hpp"

#include <iostream>

using namespace std;

int main()
{
  ArrayList<int> myList;

  for (int i = 0; i < 10; i++) {
    myList.add(i);
  }

  for (int i = 0; i < 10; i++) {
    cout << myList[i] << endl;
  }
}
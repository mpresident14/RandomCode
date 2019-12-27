#include "widget.hpp"

#include <cstddef>
#include <iostream>
#include <utility>

/*
  Copy/swap vs normal assignment operator.
  Pros
    1. No need for a self-assignment test
    2. Contains only non-throwing operations (strong exception guarantee)
    3. No duplicated code from copy constructor.
    4. We don't have to write a move assignment operator.
  Cons
    1. Assignment with an rvalue by cast (via move) calls move constructor, whereas
      move assignment operator would take the rvalue by reference.  
 */

using namespace std;

class Practice {
 public:
  /** Constructor */
  Practice(size_t inputSize) : size_{inputSize}, data_{new int[inputSize]} {
    cout << "Constructor " << this << endl;
  };

  /** Destructor */
  ~Practice() {
    cout << "Destructor " << this << endl;
    if (data_ != nullptr) delete[] data_;
  };

  /** Copy constructor */
  Practice(const Practice& other)
      : size_{other.size_}, data_{new int[other.size_]} {
    cout << "Copy Constructor " << this << endl;
  };

  /** Move constructor */
  // Note that we have to initialize data_ to nullptr so that the destructor can 
  // check it. Otherwise the destructor will attempt to free memory that has not 
  // been allocated.
  Practice(Practice&& other)
    : data_{nullptr}
  {
    cout << "Move Constructor " << this << endl;
    // Move data from other to this (other is left with junk, but it doesn't
    // matter b/c it is an rvalue)
    myswap(*this, other);
  }

  /** Passing by value assignment operator */
  // If lvalue is passed, other is created using copy constructor
  // If rvalue is passed, other is created using move constructor, or
  // possibly created directly via copy elision.
  Practice& operator=(Practice other) {
    cout << "Assignment Operator " << this << endl;
    // Swaps data (other is destroyed)
    myswap(*this, other);
    return *this;
  };

  /** swap fcn */
  friend void myswap(Practice& first, Practice& second) {
    swap(first.size_, second.size_);
    swap(first.data_, second.data_);
  }

  size_t size_;
  int* data_;
};

int main() {
  Practice p1{3};
  Practice p2{4};
  Widget w1;
  Widget w2;
  cout << endl;

  cout << "Assign to lvalue" << endl;
  cout << "Practice" << endl;
  p1 = p2;
  cout << "Widget" << endl;
  w1 = w2;
  cout << endl;

  cout << "Assign to rvalue" << endl;
  cout << "Practice" << endl;
  p1 = Practice{5};
  cout << "Widget" << endl;
  w1 = Widget::create_widget(5);
  cout << endl;

  cout << "Assign to rvalue (cast)" << endl;
  cout << "Practice" << endl;
  p1 = move(p2);
  cout << "Widget" << endl;
  w1 = move(w2);
  cout << endl;
}

// OUTPUT
// Constructor 3
// Constructor 4
// Constructor 5

// Constructor 7
// &prac=0x7fff18ead490     * Note the copy elision
// &other=0x7fff18ead490
// Assignment Operator
// Destructor 5

// Constructor 15
// Constructor 15
// &local2=0x7fffc6b725f8
// Move constructor         * Compiler must treat as if "return
// move(local2)" Destructor 0             * Move Constructor: swap if
// uninitialized Practice Destructor 15            * Destroy local1
// &other=0x7fff18ead480
// Assignment Operator
// Destructor 7
// Destructor 15
// Destructor 4
// Destructor 3

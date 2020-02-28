#include "../widget.hpp"

#include <cstddef>
#include <iostream>
#include <utility>

/*
  Copy/swap vs normal assignment operator.
    1. No need for a self-assignment test.
    2. Contains only non-throwing operations (strong exception guarantee).
    3. No duplicated code from copy constructor.
    4. Move constructor and assignment can use swap function.
 */

using namespace std;

class Practice {
 public:
  
  /** "swap" fcn */
  friend void take(Practice& first, Practice& second) {
    first.size_ = second.size_;
    first.data_ = second.data_;
  }

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
    take(*this, other);
  }

  Practice& operator=(const Practice& other) {
    cout << "Copy Assignment Operator " << this << endl;
    // Temporary copy
    Practice tmp{other};
    // Steals data (tmp is destroyed)
    take(*this, tmp);
    return *this;
  };

  Practice& operator=(Practice&& other) {
    cout << "Move Assignment Operator " << this << endl;
    // Steals data (other is destroyed)
    take(*this, other);
    return *this;
  };

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
// Constructor 0x7fffc1bfd060
// Constructor 0x7fffc1bfd050
// Default Constructor: 0x7fffc1bfd028
// Default Constructor: 0x7fffc1bfd010

// Assign to lvalue
// Practice
// Copy Assignment Operator 0x7fffc1bfd060
// Copy Constructor 0x7fffc1bfced0
// Destructor 0x7fffc1bfced0
// Widget
// Copy Assignment: 0x7fffc1bfd028

// Assign to rvalue
// Practice
// Constructor 0x7fffc1bfd000
// Move Assignment Operator 0x7fffc1bfd060
// Destructor 0x7fffc1bfd000
// Widget
// Default Constructor: 0x7fffc1bfcfe8
// Move Assignment: 0x7fffc1bfd028
// Destructor: 0x7fffc1bfcfe8

// Assign to rvalue (cast)
// Practice
// Move Assignment Operator 0x7fffc1bfd060
// Widget
// Move Assignment: 0x7fffc1bfd028

// Destructor: 0x7fffc1bfd010
// Destructor: 0x7fffc1bfd028
// Destructor 0x7fffc1bfd050
// Destructor 0x7fffc1bfd060

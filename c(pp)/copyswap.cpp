#include <cstddef>
#include <iostream>
#include <utility>

/*
   The copy and swap idiom has 3 advantages over the normal assignment operator.
    1. No need for a self-assignment test
    2. Contains only non-throwing operations (strong exception guarantee)
    3. No duplicated code from copy constructor.
    4. Allows us to take advantage of copy elision by passing by value.
 */

class Practice {
 public:
  Practice() = delete;
  /** Constructor */
  Practice(size_t inputSize) : size_{inputSize}, data_{new int[inputSize]} {
    std::cout << "Constructor " << inputSize << std::endl;
  };

  /** Destructor */
  ~Practice() {
    std::cout << "Destructor " << size_ << std::endl;
    delete[] data_;
  };

  /** Copy constructor */
  Practice(const Practice& other)
      : size_{other.size_}, data_{new int[other.size_]} {
    std::cout << "Copy Constructor" << std::endl;
  };

  /** Move constructor */
  Practice(Practice&& other) {
    std::cout << "Move Constructor" << std::endl;
    // Move data from other to this (other is left with junk, but it doesn't
    // matter b/c it is an rvalue)
    swap(*this, other);
  }

  /** Passing by value assignment operator */
  // If lvalue is passed, other is created using copy constructor
  // If rvalue is passed, other is created using move constructor
  // (Other may also be generated via copy elision if possible)
  Practice& operator=(Practice other) {
    std::cout << "&other=" << &other << std::endl;
    std::cout << "Assignment Operator" << std::endl;
    // Swaps data (other is destroyed)
    swap(*this, other);
    return *this;
  };

  /** Passing by reference assignment operator */
  // Practice& operator=(const Practice& other)
  // {
  //     Passing by reference forces us to create a tmp object before
  //     swapping so that we do not invalidate other. This forces a call to the copy constructor.
  //     Passing by value in the function above allows the compiler to use
  //     copy elision when possible, whereas this version prevents this opportunity for
  //     optimization. 
  // };

  /** swap fcn */
  friend void swap(Practice& first, Practice& second) {
    std::swap(first.size_, second.size_);
    std::swap(first.data_, second.data_);
  }

  // The local variable variable prac is created directly in the parent stack
  // frame, rather than in its own return frame. This "elides" the temporary
  // copy. Also known as (named) return value optimization.
  Practice operator+(Practice& other) {
    Practice prac{size_ + other.size_};
    std::cout << "&prac=" << &prac << std::endl;
    return prac;
  }

  // In a situation with multiple local vars that can possibly be returned, copy
  // elision is difficult for the compiler. In this case, the compiler is
  // required to treat returning a local var as if it were written "return
  // std::move(local_var)". Thus, it calls the move constructor.
  static Practice getPractice(size_t inputSize) {
    Practice local1{inputSize}, local2{inputSize};
    std::cout << "&local2=" << &local2 << std::endl;
    if (inputSize % 2 == 0) {
      return local1;
    } else {
      return local2;
    }
  }

  size_t size_;
  int* data_;
};

int main() {
  Practice p1{3};
  Practice p2{4};
  Practice q{5};

  std::cout << std::endl;
  q = p1 + p2;

  std::cout << std::endl;
  q = Practice::getPractice(15);
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
// std::move(local2)" Destructor 0             * Move Constructor: swap if
// uninitialized Practice Destructor 15            * Destroy local1
// &other=0x7fff18ead480
// Assignment Operator
// Destructor 7
// Destructor 15
// Destructor 4
// Destructor 3

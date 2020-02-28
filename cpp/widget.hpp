#ifndef WIDGET_HPP
#define WIDGET_HPP 1

#include <iostream>
#include <vector>
#include <string>

class Widget {
 public:
  Widget() { info("Default Constructor"); };

  ~Widget() { info("Destructor"); };

  Widget(const Widget& other) : nums_{other.nums_} { info("Copy Constructor"); }

  Widget& operator=(const Widget& other) {
    if (this != &other) {
      nums_ = other.nums_;
    }

    info("Copy Assignment");
    return *this;
  };

  Widget(Widget&& other) : nums_{std::move(other.nums_)} {
    info("Move Constructor");
  }

  Widget(std::string str)
    :nums_{(int) str[0]}
  {
    info("String Constructor");
  }

  Widget& operator=(Widget&& other) {
    nums_ = std::move(other.nums_);

    info("Move Assignment");
    return *this;
  };

  int work(int n) const { return n + 3; };

  static Widget create_widget(int n) {
    Widget w;
    w.nums_.push_back(n);
    return w;
  }

 private:
  void info(const char* msg) { std::cout << msg << ": " << this << std::endl; }

  std::vector<int> nums_;
};

#endif


// OUTPUT

// Constructor 0x7fffc9a9c980
// Constructor 0x7fffc9a9c970
// Default Constructor: 0x7fffc9a9c948
// Default Constructor: 0x7fffc9a9c930

// Assign to lvalue
// Practice
// Copy Constructor 0x7fffc9a9c920
// Assignment Operator 0x7fffc9a9c980
// Destructor 0x7fffc9a9c920
// Widget
// Copy Assignment: 0x7fffc9a9c948

// Assign to rvalue
// Practice
// Constructor 0x7fffc9a9c910
// Assignment Operator 0x7fffc9a9c980
// Destructor 0x7fffc9a9c910
// Widget
// Default Constructor: 0x7fffc9a9c8f8
// Move Assignment: 0x7fffc9a9c948
// Destructor: 0x7fffc9a9c8f8

// Assign to rvalue (cast)
// Practice
// Move Constructor 0x7fffc9a9c8e8
// Assignment Operator 0x7fffc9a9c980
// Destructor 0x7fffc9a9c8e8
// Widget
// Move Assignment: 0x7fffc9a9c948

// Destructor: 0x7fffc9a9c930
// Destructor: 0x7fffc9a9c948
// Destructor 0x7fffc9a9c970
// Destructor 0x7fffc9a9c980

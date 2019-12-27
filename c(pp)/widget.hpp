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
    std::cout << "&w=" << &w << std::endl;
    w.nums_.push_back(n);
    return w;
  }

 private:
  void info(const char* msg) { std::cout << msg << ": " << this << std::endl; }

  std::vector<int> nums_;
};

#endif

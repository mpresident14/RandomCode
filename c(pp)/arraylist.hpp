#ifndef ARRAYLIST_HPP_INCLUDED
#define ARRAYLIST_HPP_INCLUDED 1

#include <cstddef>

template<typename T>
class ArrayList {
  public:
    ArrayList();
    ~ArrayList();

    T& operator[](size_t index);

    void add(const T& item);
    T remove(size_t index);
    size_t size() const;

  
  private:
    void resize();

    size_t capacity_;
    T* data_;
    size_t size_;
};

#endif
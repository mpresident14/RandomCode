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

    class Iterator {
      public:
        Iterator();
        Iterator(ArrayList<T>* arrList, size_t index);
        ~Iterator() = default;
        Iterator& operator++();
        bool operator==(const Iterator& other) const;
        bool operator!=(const Iterator& other) const;
        T& operator*();

      private:
        ArrayList<T>* arrList_;
        size_t index_;
    };

  public:
    Iterator begin();
    Iterator end();
};

#include "arraylist-private.hpp"

#endif
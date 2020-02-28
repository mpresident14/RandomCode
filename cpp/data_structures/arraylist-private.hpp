#include <iostream>
#include <cstring>

using namespace std;

template<typename T>
using ALIter = typename ArrayList<T>::Iterator;

template<typename T>
ArrayList<T>::ArrayList()
  : capacity_{5}, data_{new T[capacity_]}, size_{0}
{
  // Nothing to do
}

template<typename T>
ArrayList<T>::~ArrayList()
{
  delete[] data_;
}

template<typename T>
T& ArrayList<T>::operator[](size_t index)
{
  return data_[index];
}

template<typename T>
void ArrayList<T>::add(const T& item)
{
  if (size_ == capacity_) {
    resize();
  }

  data_[size_++] = item;
}

template<typename T>
T ArrayList<T>::remove(size_t index)
{
  T item =  data_[index];
  if (index != size_ - 1) {
    memmove(&data_[index], &data_[index + 1], sizeof(T) * (size_ - 1 - index));
  }

  --size_;
  return item;
}

template<typename T>
size_t ArrayList<T>::size() const
{
  return size_;
}

template<typename T>
void ArrayList<T>::resize()
{
  capacity_ = 2 * size_;
  T* newData = new T[capacity_];
  memmove(newData, data_, size_ * sizeof(T));
  delete[] data_;
  data_ = newData;
}

template<typename T>
ALIter<T> ArrayList<T>::begin()
{
  return ArrayList<T>::Iterator(this, 0);
}

template<typename T>
ALIter<T> ArrayList<T>::end()
{
  return ArrayList<T>::Iterator(this, size_);
}

template<typename T>
ArrayList<T>::Iterator::Iterator(ArrayList<T>* arrList, size_t index)
  : arrList_{arrList}, index_{index}
{
  // Nothing to do
}

template<typename T>
ALIter<T>& ArrayList<T>::Iterator::operator++()
{
  ++index_;
  return *this;
}

template<typename T>
bool ArrayList<T>::Iterator::operator==(const Iterator& other) const
{
  return arrList_ == other.arrList_ && index_ == other.index_;
}

template<typename T>
bool ArrayList<T>::Iterator::operator!=(const Iterator& other) const
{
  return !(*this == other);
}

template<typename T>
T& ArrayList<T>::Iterator::operator*()
{
  return (*arrList_)[index_];
}

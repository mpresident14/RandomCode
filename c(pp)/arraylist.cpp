#include "arraylist.hpp"

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
  return data_[index];
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
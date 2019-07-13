#include <cstddef>
#include <iostream>
#include <utility>

using namespace std;

/**
 * The copy and swap idiom allows us to only make a deep copy in the assignment operator if the 
 * param is an lvalue. (Normally, the assignment operator would have to do a deep copy regardless.)
 * 
 * If param is an rvalue (e.g. str1 + str2), uses move constructor b/c we don't have to worry about 
 * modifying the param. Passes a shallow copy.
 * 
 * If param is an lvalue (e.g. str), uses copy constructor b/c we cannot modify str the param. 
 * Passes a deep copy.
 * */

class Practice {

    public:
        Practice() = delete;
        /** Constructor */
        Practice(size_t inputSize) 
            : size_{inputSize}, data_{new int[inputSize]}
        {cout << "default" << endl;};

        /** Destructor */
        ~Practice() {
            cout << "destroy " << size_ << endl;
            delete[] data_;
        };

        /** Copy constructor */
        Practice(const Practice& other) 
            : size_{other.size_}, data_{new int[other.size_]}
        {
            cout << "copy" << endl;
        };

        /** swap fcn */
        friend void swap(Practice& first, Practice& second)
        {
            std::swap(first.size_, second.size_);
            std::swap(first.data_, second.data_);
        }

        /** Move constructor */
        Practice(Practice&& other)
            : data_{nullptr}
        {
            cout << "move" << endl;
            // Move data from other to this (other is left with junk, but it doesn't matter b/c it
            // is an rvalue)
            swap(*this, other);
        }

        /** Assignment operator */
        // If lvalue is passed, other is created using copy constructor
        // If rvalue is passed, other is created using move constructor
        Practice& operator=(Practice other)
        {
            cout << "assign" << endl;
            // Swaps data (other is destroyed) 
            swap(*this, other);
            return *this;
        };

        Practice operator+(Practice& other)
        {
            Practice prac{size_ + other.size_};
            cout << "&prac=" << &prac << endl;
            return prac;
            // Note: if we just say:
            // return Practice{size_ + other.size_}, compiler uses copy elision and move 
            // constructor is not called.
        }
      

        

    // private:
        size_t size_;
        int* data_;
};

int main()
{
    Practice p{3};
    Practice q{4};

    Practice r{p + q};
    cout << "&r=" << &r << endl;
    cout << r.size_ << endl;
}

// OUTPUT (with -Od (no) optimization):
// default
// default 
// default              ***construction in operator+***
// &prac=001BFAE0
// move
// destroy (random #)   ***destroy other in operator+*** 
// &r=001BFB20
// destroy 4            ***destroy other in move constructor***
// 7       
// destroy 7
// destroy 4
// destroy 3

// OUTPUT (with -O2 optimization): 
// default
// default 
// default              ***construction in operator+***
// &prac=00F3FD60
// &r=00F3FD60          ***copy elision (r is the same object as prac in operator+)***
// 7       
// destroy 7
// destroy 4
// destroy 3

//------------------------------------

// int main()
// {
//     Practice p{3};
//     p = std::move(Practice{4});

//     cout << p.size_ << endl;

//     return 0;
// }

// OUTPUT
// default 
// default
// move
// assign
// destroy 3            ***destroy other in move constructor***
// destroy (random #)   ***destroy other in assignment operator***
// 4
// destroy 4            ***destroy p***
// From
// https://herbsutter.com/2008/01/01/gotw-88-a-candidate-for-the-most-important-const/

// Normally, a temporary object lasts only until the end of the full expression
// in which it appears. However, C++ deliberately specifies that binding a
// temporary object to a reference to const on the stack lengthens the lifetime
// of the temporary to the lifetime of the reference itself, and thus avoids
// what would otherwise be a common dangling-reference error. (Note this only
// applies to stack-based references. It doesn’t work for references that are
// members of objects.)

#include <iostream>
#include <string>

using namespace std;

void fConstIntRef(const int& n) { cout << n << endl; }

void fIntRef(int& n) { cout << n << endl; }


int main() {
  // Lifetime of temporary object is extended for the stack-based reference
  fConstIntRef(7);

  //  We cannot modify a literal, so the compiler enforces "const" int& i = getInt();
  // fIntRef(7); // ERROR! WILL NOT COMPILE.
  
  return 0;
}

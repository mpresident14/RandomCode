// From https://herbsutter.com/2008/01/01/gotw-88-a-candidate-for-the-most-important-const/

// Normally, a temporary object lasts only until the end of the full expression in which 
// it appears. However, C++ deliberately specifies that binding a temporary object to a 
// reference to const on the stack lengthens the lifetime of the temporary to the lifetime 
// of the reference itself, and thus avoids what would otherwise be a common 
// dangling-reference error. (Note this only applies to stack-based references. 
// It doesn’t work for references that are members of objects.)

#include <iostream>
#include <string>

using namespace std;


int getInt(int n)
{
	return n;
}

int main()
{
	// Lifetime of temporary object is extended for the stack-based reference
	const int& i = getInt(5);

	// ERROR! WILL NOT COMPILE. We cannot modify a literal, so the compiler enforces "const"
	// int& i = getInt(); 

	cout << "i=" << i << endl;

	return 0;
}

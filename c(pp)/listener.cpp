#include <cstddef>
#include <iostream>
#include <vector>

using namespace std;

class Listener {
 public:
  // Needs to be virtual destructor b/c mediaLoadListener points to a
  // MediaLoadedListener. Without virtual, delete mediaLoadListener
  // would try to delete a MediaLoadedListener with a Listener destructor,
  // which is undefined behavior.
  virtual ~Listener() = default;
  // " = 0" denotes a pure virtual function that must be implemented
  // by the derived class
  virtual void doAction() = 0;
};

class MediaLoadedListener : public Listener {
 public:
  void doAction() { cout << "Media loading completed.\n"; }
};

class CommentsLoadedListener : public Listener {
 public:
  void doAction() { cout << "Comment loading completed.\n"; }
};

class Loader {
 private:
  // Vector must store pointer to the interface, cannot instantiate abstract
  // class
  vector<Listener*> listeners_;

 public:
  void addListener(Listener* listener) { listeners_.push_back(listener); }

  void load() {
    cout << "Loading..." << endl;
    for (Listener* listener : listeners_) {
      listener->doAction();
    }
  }
};

int main() {
  Listener* mediaLoadListener = new MediaLoadedListener();
  Listener* commentLoadListener = new CommentsLoadedListener();
  Loader loader;

  loader.addListener(mediaLoadListener);
  loader.addListener(commentLoadListener);
  loader.load();

  delete mediaLoadListener;
  delete commentLoadListener;
  return 0;
}
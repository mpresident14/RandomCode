// http://zenol.fr/blog/boost-property-tree/en.html for more info

#include <boost/property_tree/json_parser.hpp>
#include <boost/property_tree/ptree.hpp>
#include <cstddef>
#include <iostream>
#include <string>

using namespace std;
namespace pt = boost::property_tree;

int main(int argc, char const* argv[]) {
  // Create a root
  pt::ptree root;

  // Load the json file in this ptree
  pt::read_json("example.json", root);

  // You can also go through nested nodes
  string msg = root.get<string>("quiz.sport.q1.question");
  cout << msg << '\n' << endl;

  // <ptree>.first is name of the root
  // <ptree>.second is the subtree
  for (pt::ptree::value_type& category : root.get_child("quiz")) {
    cout << "Category: " << category.first << endl;
    for (pt::ptree::value_type& question_num : category.second)
      cout << question_num.first << ": "
           << question_num.second.get<string>("question") << endl;
    ;
  }
}

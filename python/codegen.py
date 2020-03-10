import argparse
import os
import textwrap


def writeFile(filename, code):
  with open(filename, "x") as f:
    f.write(textwrap.dedent(code))


def generateHpp(filename):
  includeGuard = filename.upper().replace(".", "_")
  hppBoilerplate = \
  """\
  #ifndef {0}
  #define {0}

  #include <iostream>
  #include <cstddef>
  #include <string>


  #endif
  """.format(includeGuard)

  writeFile(filename, hppBoilerplate)


def generateCpp(filename):
  cppBoilerplate = \
  """\
  #include <iostream>
  #include <cstddef>
  #include <string>

  using namespace std;


  int main(int, char**)
  {


    return 0;
  }
  """

  writeFile(filename, cppBoilerplate)



if __name__ == "__main__":
  parser = argparse.ArgumentParser(description="Generate cpp/hpp boilerplate.")
  parser.add_argument(
      "filenames",
      nargs='*',
      help="File(s) to create",
  )

  args = parser.parse_args()

  for filename in args.filenames:
    ext = os.path.splitext(filename)[1]
    if ext == ".hpp":
      generateHpp(filename)
    elif ext == ".cpp":
      generateCpp(filename)
    else:
      print(f"Unknown file extension \"{ext}\".")

import argparse
import os


def writeFile(filename, code):
  with open(filename, "x") as f:
    f.write(code)


def underscoreToCamelCase(className):
  classNameCamelCase = className[0].upper()
  i = 1
  length = len(className)

  while i < length:
    if className[i] == "_":
      i += 1
      classNameCamelCase += className[i].upper()
    # If you end with a "_", that's your own fault lol
    else:
      classNameCamelCase += className[i]
    i += 1

  return classNameCamelCase


def generateHpp(filename, className, copyswap):
  includeGuard = filename.upper().replace(".", "_")
  classCode = ""
  if className:
    className = underscoreToCamelCase(className)

    # Copy-and-swap idiom
    if copyswap:
      assignmentOpCode = f"  {className}& operator=({className} other) = default;\n\n"
    else:
      assignmentOpCode = (
        f"  {className}& operator=(const {className}& other) = default;\n"
        f"  {className}& operator=({className}&& other) = default;\n\n"
      )

    # Rest of class
    classCode = (
      f"class {className} {{\n"
      f"public:\n"
      f"  {className}() = default;\n"
      f"  ~{className}() = default;\n"
      f"  {className}(const {className}& other) = default;\n"
      f"  {className}({className}&& other) = default;\n"
      f"{assignmentOpCode}"

      f"private:\n"
      f"}};"
    )

  # .hpp stuff
  hppBoilerplate = (
    f"#ifndef {includeGuard}\n"
    f"#define {includeGuard}\n\n"

    f"#include <iostream>\n"
    f"#include <cstddef>\n"
    f"#include <string>\n\n"

    f"{classCode}\n\n"

    f"#endif\n"
  )

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
  parser.add_argument(
      "-c",
      "--create_class",
      default=False,
      action="store_true",
      help="Create class corresponding to file name.")
  parser.add_argument(
      "-s",
      "--copyswap",
      default=False,
      action="store_true",
      help="Use the copy-and-swap idiom.")

  args = parser.parse_args()

  for filename in args.filenames:
    name, ext = os.path.splitext(filename)
    if ext == ".hpp":
      generateHpp(filename, name if args.create_class else None, args.copyswap)
    elif ext == ".cpp":
      generateCpp(filename)
    else:
      print(f"Unknown file extension \"{ext}\".")

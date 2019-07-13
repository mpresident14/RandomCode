import os
import sys

def help():
  return ("Run this from your top level directory. Your .java files" 
  "should be in src/ and there should be a bin/ folder for output .class files.\n"
  "Syntax: python run_java.py path.to.my.class.ClassName")

if __name__ == "__main__":
    if len(sys.argv) != 2:
      print(help())
      exit()
    pathDots = sys.argv[1]
    pathSlashes = "/".join(pathDots.split("."))
    compileCmd = f"javac -classpath src/ src/{pathSlashes}.java -d bin"
    runCmd = f"java -cp bin/ {pathDots}"

    os.system(compileCmd)
    os.system(runCmd)

import sys
import argparse
import subprocess

def help():
  return "help commented out"
  # return ("Run this from your top level directory. Your .java files " 
  # "should be in src/main and there should be a bin/ folder for output .class files. " 
  # "The CLASSPATH env variable should have src/main, bin/, src/test, and "
  # "C:\Program Files (x86)\junit4.10\junit-4.10.jar.\n"
  # "[Syntax: python run_java.py [-t] path.to.my.class.ClassName]")

def run(pathDots, pathSlashes):
  compileCmd = f"javac -cp \"src/main\" src/main/{pathSlashes}.java -d bin -Xlint"
  runCmd = f"java -cp bin {pathDots}"

  return compileCmd, runCmd

def test(pathDots, pathSlashes):
  compileCmd = f"javac -cp \"C:\Program Files (x86)\junit4.10\junit-4.10.jar;src/test;src/main\" src/test/{pathSlashes}.java -d bin -Xlint"
  runCmd = f"java -cp \"C:\Program Files (x86)\junit4.10\junit-4.10.jar;bin;src/main\" org.junit.runner.JUnitCore {pathDots}"

  return compileCmd, runCmd

if __name__ == "__main__":
    # if len(sys.argv) != 2:
    #   print(help())
    #   exit()
    parser = argparse.ArgumentParser(description='Run java files.')
    parser.add_argument("-t", action='store_true')
    parser.add_argument("className")
    args = vars(parser.parse_args())

    pathDots = args["className"]
    pathSlashes = "/".join(pathDots.split("."))

    compileCmd, runCmd = \
        test(pathDots, pathSlashes) if args["t"] else run(pathDots, pathSlashes)

    subprocess.check_call(compileCmd)
    subprocess.call(runCmd)
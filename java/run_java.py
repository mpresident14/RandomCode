import os
import sys
import argparse

def help():
  return ("Run this from your top level directory. Your .java files " 
  "should be in src/ and there should be a bin/ folder for output .class files. " 
  "The CLASSPATH env variable should have src/, bin/, test/, and "
  "C:\Program Files (x86)\junit4.10\junit-4.10.jar.\n"
  "[Syntax: python run_java.py [-t] path.to.my.class.ClassName]")

def run(pathDots, pathSlashes):
  compileCmd = f"javac src/{pathSlashes}.java -d bin"
  runCmd = f"java {pathDots}"

  return compileCmd, runCmd

def test(pathDots, pathSlashes):
  compileCmd = f"javac test/{pathSlashes}.java -d bin"
  runCmd = f"java org.junit.runner.JUnitCore {pathDots}"

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

    os.system(compileCmd)
    os.system(runCmd)
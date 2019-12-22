import sys
import argparse
import subprocess

# There should be in src/main and src/test and 
# a bin/ folder for output .class and .java files.
  
# NOTE: Classpath separator is colon for Linux, semicolon for Windows

def run(pathDots, pathSlashes):
  compileCmd = f"javac -cp \"src/main\" src/main/{pathSlashes}.java -d bin -Xlint"
  runCmd = f"java -cp bin {pathDots}"

  return compileCmd, runCmd

def test(pathDots, pathSlashes):
  compileCmd = f"javac -cp \"/usr/share/java/junit4-4.12.jar:src/test:src/main\" src/test/{pathSlashes}.java -d bin -Xlint"
  runCmd = f"java -cp \"/usr/share/java/junit4-4.12.jar:bin:src/main\" org.junit.runner.JUnitCore {pathDots}"

  return compileCmd, runCmd

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run java files from top level java directory.')
    parser.add_argument("-t", "--test", action='store_true', default=False, help="Run a test file.")
    parser.add_argument("className", help="path.to.my.class.ClassName")

    args = parser.parse_args()

    pathDots = args.className
    pathSlashes = "/".join(pathDots.split("."))

    compileCmd, runCmd = \
        test(pathDots, pathSlashes) if args.test else run(pathDots, pathSlashes)

    print(compileCmd)
    subprocess.check_call(compileCmd, shell=True) # Super bad security

    print(runCmd)
    print()
    subprocess.call(runCmd, shell=True) # Super bad security 

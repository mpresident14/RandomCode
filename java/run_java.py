import sys
import argparse
import subprocess

# Expected hierarchy
# java
# - bin
# - src
# - test (matching src file tree structure)
# NOTE: Classpath separator is colon for Linux, semicolon for Windows

javac = "javac -g -d bin -Xlint -Xdiags:verbose"
java = "java -XX:+ShowCodeDetailsInExceptionMessages"

def run(pathDots, pathSlashes):
  compileCmd = javac + f" -cp src src/{pathSlashes}.java"
  runCmd = java + f" -cp bin {pathDots}"
  return compileCmd, runCmd

def test(pathDots, pathSlashes):
  compileCmd = javac + f" -cp /usr/share/java/junit4.jar:test:src test/{pathSlashes}.java"
  runCmd = java + f" -cp /usr/share/java/junit4.jar:bin:src org.junit.runner.JUnitCore {pathDots}"
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

    print(compileCmd, end="\n\n")
    subprocess.check_call(compileCmd.split())
    print(runCmd, end="\n\n")
    subprocess.call(runCmd.split())

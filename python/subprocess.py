import subprocess

if __name__ == "__main__":
  x = 42
  subprocess.call(["subprocess.exe", str(x)])
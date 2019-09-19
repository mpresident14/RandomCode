import os
from PIL import Image

path = 'C:/Users/Miles/Desktop/test/'

# type: 'jpeg', 'png', etc.
def convertTo(ext):
  for file_dir in os.listdir(path):
      if (os.path.isfile(path + file_dir)):
        img = Image.open(path + file_dir)
        newName = file_dir[ : file_dir.index('.') + 1] + ext
        img.save(path + newName, ext)

def usingListdir():
  for file_dir in os.listdir(path):
      if (os.path.isdir(path + file_dir)):
        print(file_dir + ' is a directory.')
      else:
        print(file_dir + ' is a file.')

def usingWalk():
  for root, dirs, files in os.walk(path):
    print('root:')
    print(root)
    print('dirs:')
    print(dirs)
    print('files:')
    print(files)

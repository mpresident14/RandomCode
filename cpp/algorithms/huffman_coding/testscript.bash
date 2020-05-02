#!/bin/bash

TESTDIR=$1
cd $TESTDIR

time ../main -f test.txt
mv test.txt.huff test2.txt.huff
time ../main -d -f test2.txt.huff
hexdump test2.txt > new_bytes.txt
output=$(diff orig_bytes.txt new_bytes.txt)
if [ "$output" = "" ]; then
  echo "Success!"
  orig_size=$(wc -c test.txt | awk '{print $1}')
  new_size=$(wc -c test2.txt.huff | awk '{print $1}')
  ratio=$(echo "scale=2 ; $orig_size / $new_size" | bc)
  echo "Compression ratio: $ratio"
else
  echo "Failure :("
fi

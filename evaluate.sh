#!/bin/bash

function usage {
  echo "usage $1:"
  echo "This script launches the program on each patient case and computes some interesting metrics about it"
  echo "It takes in entry the path to the executable jar, and the path to the directory that contains each patient case and its gold ranking"
  exit
}
function evaluate {
rm res.txt 2> /dev/null
for i in "$DIR"*.txt; do
  echo "$i"
  # Get the results and extract only the chapter numbers
  java -jar "$JAR" --search "$i" 2> /dev/null | grep -E '^[A-Z][0-9].*$'| sed -E 's/^([A-Z]([0-9]\.?)+).*$/\1/g' | tr '\n' ' ' >> res.txt
  echo "" >> res.txt
  # get the gold standard for the same file
  cat `echo $i | sed s/.txt/.gs/g` | tr '\n' ' ' >> res.txt
  echo "" >> res.txt

done
Rscript "evaluate.R" "res.txt"
}

if [ $# -ne 2 ]; then
  usage $0
fi

JAR="$1"
DIR="$2"

echo "cleaning the old index"
java -jar "$JAR" --clean
echo "creating the new index"
java -jar "$JAR" --index
echo "Evaluating each patient case and showing the final report"
evaluate


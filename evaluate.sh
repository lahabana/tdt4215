#!/bin/bash

function usage {
  echo "usage $1:"
  echo "This script launches the program on each patient case and computes some interesting metrics about it"
  echo "It takes in entry the path to the executable jar, and the path to the directory that contains each patient case and its gold ranking"
  exit
}

function evaluate {
# We add a sleep to be sure the java program is well started (it's not really necessary)
# The first grep removes all the text for the user
# The first sed removes the title of the chapters
# The tr puts everything on a single line
# The second tr replaces every 'Matches:' by a line end
# we then remove the starting space and the first endline
(sleep 1; find "$DIR"*.txt; echo "") | java -jar "$JAR" --search \
            | grep -E '(^[A-Z][0-9].*$|^Matches:)' \
            | sed -E 's/^([A-Z]([0-9]\.?)+).*$/\1/g' \
            | tr -s '\n' ' ' \
            | tr -s 'Matches:' '\n' \
            | grep -E '^[^\n]' \
            | sed -E 's/^ (.*)$/\1/g' \
            > res.txt

# launch the R script that computes stats with the gold standard
Rscript "evaluate.R" "res.txt" "gs.txt"

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

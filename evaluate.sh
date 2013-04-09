#!/bin/bash

function usage {
  echo "usage $1 --run|evaluate jar-file query-folder
This script launches the program on each patient case
  If the first argument is --run it just runs and display the result
  Otherwise it runs and display some statistics

  jar-file: the fully packages jar that contains the program
            (mvn clean compile assembly:single should help)
  query-folder: the folder that contains the files with the query to run"

exit
}

function evaluate {
    # We add a sleep to be sure the java program is well started (it's not really necessary)
    # The first grep removes all the text for the user
    # The first sed removes the title of the chapters
    # The tr puts everything on a single line
    # The second tr replaces every 'Matches:' by a line end
    # we then remove the starting space and the first endline
    (find "$DIR"*.txt; echo "") | java -jar "$JAR" --search \
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

if [ $# -eq 3 ]; then
    JAR="$2"
    DIR="$3"
    echo "cleaning the old index"
    java -jar "$JAR" --clean
    echo "creating the new index"
    java -jar "$JAR" --index

    if [ "$1" = "--evaluate" ]; then
        echo "Evaluating each patient case and showing the final report"
        evaluate
    elif [ "$1" = "--run" ]; then
        (find "$DIR"*.txt; echo "") | java -jar "$JAR" --search
    else
        usage $0
    fi
else
  usage $0
fi

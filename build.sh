#!/bin/bash

if [ $1 = "-c" ]; then
	file=$( find -regex ".*$2\.java" )
	javac $file
else
	file=$( find -regex ".*$1\.java" )
	fileNExt=$( echo $file | cut -d '.' -f2 )
	javac $file
	java $fileNExt
fi

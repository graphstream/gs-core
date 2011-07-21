#!/bin/bash
#
# This script will take the Array class that can handle only Object arrays and
# make several classes able to contain a given type (actually it uses only Java
# base types, but it could be used to create arrays of anything).
#
# This is a kind of "poor man's template mechanism".
#

# No boolean array, a bit set is better
types="Byte:byte Short:short Int:int Long:long Char:char Float:float Double:double"

for t in ${types}
do

	# Extract the Java type
	type=${t#*:}
	
	# Extract the type name
	Type=${t%:*}

	# Compute the file name
	file=${Type}Array.java
	
	# And create files...
	rm -f ${file}
	sed -e "286r MoreBaseArrayMethods.txt" -e "341,349d" -e "s/\<Array\>/${Type}Array/g" Array.java > ${file}.tmp
	sed -e "s/Object/${type}/" ${file}.tmp > ${file}
	rm -f ${file}.tmp

done
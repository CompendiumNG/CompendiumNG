#!/bin/bash

TMP="./tmp.txt"

echo "by file extension statistics:" > $TMP;
echo "extension	count";
echo "---------	-----"

for EXT in so sh dll txt xml java class jar gif png properties backup sample idx pdf;
do
	COUNT=`find . -name "*.$EXT" | wc -l `
	echo "$EXT	$COUNT";
done

TOTAL=`find . | wc -l`


echo "---------	-----";
echo "total	$TOTAL";


	





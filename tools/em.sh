#!/bin/bash 

# helper script - lists jars in lib directory and add them as local jar artifacts to frag.txt
# frag.txt can be pasted to pom.xml (dependencies)

# this is actually workaround to include locally (in-repository) jar files for which source code haven't been located yet.


for F in `ls lib`;
do
NAME=`echo $F  | cut -d'.' -f1`

FRAG="<dependency> <groupId>$NAME</groupId><artifactId>$NAME</artifactId> <version>1.0</version> <scope>system</scope> <systemPath>\${project.basedir}/lib/$F</systemPath> </dependency>";
echo $FRAG >> frag.txt;
done











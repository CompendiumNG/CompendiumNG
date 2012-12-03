#!/bin/bash 

HOME_PATH=~/Compendium

JAR=`find target -name *-jar-with-dependencies.jar -exec basename {} \; | tail -1`

function pause() {
   read -p "$*"
}

if [ -x $HOME_PATH ]; then
	echo "compendium install directory [$HOME_PATH] already exists - first remove/rename it";
	exit;
fi


echo "if you want to proceed press enter otherwise Ctrl-C "
pause 'Press [Enter] key to continue...'



DIST="Backups \
Compendium.bat \
Compendium.dtd \
Compendium.sh \
Exports \
License.html  \
Linked\ Files \
Movies \
ReadMe.html \
Skins \
System \
Templates \
compendium.icns \
docs \
fobs4jmf.dll \
libfobs4jmf.jnilib \
libfobs4jmf.so \
vpproject \
log.cfg \
target/$JAR"


mkdir $HOME_PATH

for I in $DIST;
do
	echo "copying $I"
	cp -pr $I $HOME_PATH 
done

ln -s $HOME_PATH/$JAR $HOME_PATH/compendium.jar 

chmod u+x $HOME_PATH/Compendium.sh



#CP="lib/AppleJavaExtensions.jar:lib/crew.jar:lib/fobs4jmf.jar:lib/jabberbeans.jar:lib/jhall.jar:lib/jmf-all.jar:lib/jmf-win.jar:lib/kunststoff.jar:lib/triplestore.jar:lib/xml.jar:Compendium-2.0.1-SNAPSHOT-jar-with-dependencies.jar"

# java -Xmx512m -cp $CP:cfg  com.compendium.ProjectCompendium %1 %2 %3 %4 %5 %6 %7 %8 %9

    

echo "add ~/Compendium to your \$PATH"
echo "and run it Compendium.sh"




@echo off
cd ${INSTALL_PATH}
start /b javaw -Xmx512m -Dlog4j.configuration=file:$USER_HOME/log4j.properties -jar CompendiumNG-jar-with-dependencies.jar  %1 %2 %3 %4 %5 %6 %7 %8 %9

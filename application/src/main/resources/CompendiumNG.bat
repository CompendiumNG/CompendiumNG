@echo off
cd ${INSTALL_PATH}
start /b javaw -Xmx512m -Dlog4j.configuration=file:${user.setting.dir}/log4j.properties com.compendium.ProjectCompendium %1 %2 %3 %4 %5 %6 %7 %8 %9

@echo off
cd ${INSTALL_PATH}

start /b javaw -Dlogback.configurationFile="${INSTALL_PATH}\logback.xml" -Dcompendiumng.config.dir="%USERPROFILE%\compendiumng_config" -Ddir.data="%USERPROFILE%\compendiumng_data" -Xmx512m -jar CompendiumNG.jar  %1 %2 %3 %4 %5 %6 %7 %8 %9

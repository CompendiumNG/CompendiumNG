@echo off
cd ${INSTALL_PATH}

rem FIXME: HOME/{.AppName} (Unix-like) and APPDATA\{DeveloperName\AppName} (Microsoft Windows) - for storing application settings. Many open source programs incorrectly use USERPROFILE for application settings in Windows - USERPROFILE should only be used in dialogs that allow user to choose between paths like Documents/Pictures/Downloads/Music, for programmatic purposes APPDATA (roaming), LOCALAPPDATA or PROGRAMDATA (shared between users) is used.
rem ... this is also our case (source: http://en.wikipedia.org/wiki/Environment_variable)

start /b javaw -Dlogback.configurationFile="${INSTALL_PATH}\logback.xml" -Dcompendiumng.config.dir="${INSTALL_PATH}\config" -Xmx512m -jar CompendiumNG.jar  %1 %2 %3 %4 %5 %6 %7 %8 %9

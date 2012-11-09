@echo off

start /b javaw -Xmx256m -classpath "System\lib\compendiumcore.jar;System\lib\compendium.jar;System\lib\AppleJavaExtensions.jar;System\lib\jhall.jar;System\lib\kunststoff.jar;System\lib\jabberbeans.jar;System\lib\mysql-connector-java-5.0.5-bin.jar;System\lib\derby.jar;System\lib\triplestore.jar;System\lib\xml.jar" com.compendium.ProjectCompendium %1 %2 %3 %4 %5 %6 %7 %8 %9

cd %{INSTALL_PATH}
# java -Xmx512m -Dlog4j.configuration=file:%{user.setting.dir}/log4j.properties com.compendium.ProjectCompendium %1 %2 %3 %4 %5 %6 %7 %8 %9
java -Xmx512m -Dlog4j.configuration=file:$HOME/.compendiumng/log4j.properties -jar CompendiumNG-jar-with-dependencies.jar "$@"

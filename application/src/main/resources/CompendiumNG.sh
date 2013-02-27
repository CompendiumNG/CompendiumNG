cd %{INSTALL_PATH}
java -Xmx512m -Dlog4j.configuration=file:%USER_HOME/.compendiumng/log4j.properties -jar CompendiumNG-jar-with-dependencies.jar "$@"

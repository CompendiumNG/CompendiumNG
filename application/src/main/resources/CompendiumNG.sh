cd %{INSTALL_PATH}
java -Dlogback.configurationFile="${HOME}/compendiumng_config/logback.xml" -Xmx512m -jar CompendiumNG-jar-with-dependencies.jar "$@"

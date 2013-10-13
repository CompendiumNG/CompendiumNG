cd %{INSTALL_PATH}
java -Dlogback.configurationFile="${HOME}/compendiumng_config/logback.xml" -Dcompendiumng.config.dir=${HOME}/CompendiumNG-data -Xmx512m -jar CompendiumNG.jar "$@"

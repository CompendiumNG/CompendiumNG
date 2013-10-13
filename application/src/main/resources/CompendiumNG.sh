cd %{INSTALL_PATH}
java -Dlogback.configurationFile="%{INSTALL_PATH}/logback.xml.default" -Dcompendiumng.config.dir=${HOME}/CompendiumNG-data -Xmx512m -jar CompendiumNG.jar "$@"

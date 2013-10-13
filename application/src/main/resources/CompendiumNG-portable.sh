cd %{INSTALL_PATH}
java -Dlogback.configurationFile="%{INSTALL_PATH}/config/logback.xml" %{INSTALL_PATH}/data -Xmx512m -jar CompendiumNG.jar "$@"

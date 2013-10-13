cd %{INSTALL_PATH}
java -Dlogback.configurationFile="%{INSTALL_PATH}/logback.xml.default" -Xmx512m -jar CompendiumNG.jar "$@"

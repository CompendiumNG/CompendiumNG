cd %{INSTALL_PATH}
javaw -Dlogback.configurationFile="%{INSTALL_PATH}/logback.xml" -Dcompendiumng.config.dir="%{INSTALL_PATH}/config" -Xmx512m -jar CompendiumNG.jar "$@"

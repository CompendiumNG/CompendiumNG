cd %{INSTALL_PATH}
javaw -Dlogback.configurationFile="%{INSTALL_PATH}/logback.xml.default" -Xdock:name="Compendium NG" -Dcompendiumng.config.dir="%{INSTALL_PATH}/config" -Xmx512m -jar CompendiumNG.jar "$@"

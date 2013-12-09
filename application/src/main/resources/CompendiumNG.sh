cd %{INSTALL_PATH}
java -Dlogback.configurationFile="%{INSTALL_PATH}/logback.xml.default" -Xdock:name="Compendium NG" -Dcompendiumng.config.dir=${HOME}/compendiumng_config -Ddir.data=${HOME}/compendiumng_data -Xmx512m -jar CompendiumNG.jar "$@"

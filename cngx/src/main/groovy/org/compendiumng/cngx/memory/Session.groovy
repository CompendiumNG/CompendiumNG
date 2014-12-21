package org.compendiumng.cngx.memory
import groovy.util.logging.Slf4j
import org.apache.commons.lang.NotImplementedException

@Slf4j
class Session {

	static def AllSessions = []

	def static ConfigFile = new File("cngx.properties")
	def final static Props = new Properties()
	def static Config

	static {
		if (ConfigFile.exists() && ConfigFile.isFile()) {
			Props.load(new FileInputStream(ConfigFile.getAbsoluteFile()));
			Config = new ConfigSlurper().parse(Props)
		} else {
			log.warn("No configuration file provided: " + ConfigFile.getAbsoluteFile())
		}
	}

	protected Session (Map properties) {


	}

	/**
	 *
	 * @param properties properties of the session
	 * @return established session
	 */
	static Session Create(Map properties) {
		def s = new Session(properties)

		if (s.connect()) {
			log.info("Sucessfully connected to: " + properties["connection"])
			AllSessions << s
		} else {
			log.err("Failed to connect to: " + properties["connection"])
		}
	}

	@Override
	Element createElement(Map properties) {
		def element = new Element(Element.ElementType)
		element.getProperties() << properties
		return element
	}

	@Override
	boolean connect() {
		throw new NotImplementedException()
		false
	}

	@Override
	void deleteElement(Element node, UUID uuid, String rid) {
		throw new NotImplementedException()
	}

	@Override
	Element updateElementProperties(Element element, Map properties) {
		return null
	}

	@Override
	Element[] searchNodes(String criteria) {
		return new Element[0]
	}

	@Override
	Element getNode(String rid) {
		return null
	}

	@Override
	Element getNode(UUID uuid) {
		return null
	}

	@Override
	Element createLink(Element NodeA, Element nodeB, Map properties) {
		return null
	}

	@Override
	Element[] getOutLinks(Element node) {
		return new Element[0]
	}

	@Override
	Element[] getInLinks(Element node) {
		return new Element[0]
	}

	@Override
	Element[] getAllLinks(Element node) {
		return new Element[0]
	}

	@Override
	void link(Element nodeA, Element nodeB, Element ILink) {

	}

	@Override
	void unlinkInLinks(Element node) {

	}

	@Override
	void unlinkOutLinks(Element node) {

	}

	@Override
	void unlinkAllLinks(Element node) {

	}
}

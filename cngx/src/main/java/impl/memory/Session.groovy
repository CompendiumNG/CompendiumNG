package impl.memory
import api.memory.IElement
import api.memory.ISession
import api.memory.IValue
import groovy.util.logging.Slf4j

@Slf4j
class Session extends ISession {

	static def AllSessions = []

	@Override
	public static ISession Create(Map properties) {
		ISession s = new Session()
		s.setProperty(properties)
		def connected = s.connect

		if (connected) {
			log.info("Sucessfully connected to: " + properties["connection"])
			AllSessions << s
		} else {
			log.err("Failed to connect to: " + properties["connection"])
		}
	}

	@Override
	IElement createElement(Map<String, IValue> properties) {
		def element = new IElement(IElement.ElementType)
		element.getProperties() << properties
		return null
	}

	@Override
	void deleteElement(IElement node, UUID uuid, String rid) {
		if (node != null) {
			node.discard()
		}
	}

	@Override
	IElement updateElementProperties(IElement element, Map<String, IValue> properties) {
		element.getProperties() << properties
		return null
	}


	@Override
	public IElement[] searchNodes(String criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement getNode(String rid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement getNode(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement createLink(IElement NodeA, IElement nodeB,
			Map<String, IValue> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement[] getOutLinks(IElement node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement[] getInLinks(IElement node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement[] getAllLinks(IElement node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void link(IElement nodeA, IElement nodeB, IElement ILink) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlinkInLinks(IElement node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlinkOutLinks(IElement node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlinkAllLinks(IElement node) {
		// TODO Auto-generated method stub

	}

}

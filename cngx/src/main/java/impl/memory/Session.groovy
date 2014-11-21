package impl.memory

import api.memory.IElement;
import api.memory.ISession;
import api.memory.IValue;

class Session extends ISession {

	@Override
	public ISession Create(Map properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IElement createElement(Map<String, IValue> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteElement(IElement node) {
		// TODO Auto-generated method stub

	}

	@Override
	public IElement modifyElement(Map<String, IValue> properties) {
		// TODO Auto-generated method stub
		return null;
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

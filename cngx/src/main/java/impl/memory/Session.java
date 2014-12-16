package impl.memory;

import api.memory.*;
import api.memory.elements.*;

import java.util.Map;
import java.util.UUID;

public class Session extends ISession {
	private Store store = null;
	private UUID uuid = UUID.randomUUID();
	private boolean isOpen = false;

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Override
	public String createTag(String tag, Map<String, Value> properties) {
		return null;
	}

	@Override
	public String deleteTag(String tag) {
		return null;
	}

	@Override
	public String modifyTag(String tag, Map<String, String> properties) {
		return null;
	}

	@Override
	public ITag getTag(String tag) {
		return null;
	}

	@Override
	public INode createNode() {
		return null;
	}

	@Override
	public INode createNode(Map<String, Value> properties) {
		return null;
	}

	@Override
	public void deleteNode(INode node) {

	}

	@Override
	public INode modifyNode(Map<String, Value> properties) {
		return null;
	}

	@Override
	public INode[] searchNodes(String criteria) {
		return new INode[0];
	}

	@Override
	public INode getNode(String rid) {
		return null;
	}

	@Override
	public INode getNode(UUID uuid) {
		return null;
	};

	@Override
	public ILink createLink() {
		return null;
	}

	@Override
	public ILink createLink(Map<String, Value> properties) {
		return null;
	}

	@Override
	public ILink[] getOutLinks(INode node) {
		return new ILink[0];
	}

	@Override
	public ILink[] getInLinks(INode node) {
		return new ILink[0];
	}

	@Override
	public ILink[] getAllLinks(INode node) {
		return new ILink[0];
	}

	@Override
	public void connect(INode from, INode to, ILink link) {

	}

	@Override
	public void disconnect(INode from, INode to, ILink link) {

	}

	@Override
	public void disconnectInLinks(INode node) {

	}

	@Override
	public void disconnectOutLinks(INode node) {

	}

	@Override
	public void disconnectAlltLinks(INode node) {

	}



}

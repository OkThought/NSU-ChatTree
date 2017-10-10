package ru.nsu.ccfit.bogush.chat.msg.types;

import ru.nsu.ccfit.bogush.chat.Node;
import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "children")
public class AddChildren implements Message {
	private static final List<Node> NO_CHILDREN = new LinkedList<>();
	private List<Node> children;

	@XmlElement(name = "child")
	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	@XmlAttribute(name = "uuid", required = true)
	public UUID uuid;

	public AddChildren() {
		this(NO_CHILDREN);
	}

	public AddChildren(List<Node> children) {
		this(children, UUID.randomUUID());
	}

	public AddChildren(List<Node> children, UUID uuid) {
		this.children = children;
		this.uuid = uuid;
	}

	@Override
	public byte[] serializeBy(MessageSerializer serializer) throws Exception {
		return serializer.serialize(this);
	}

	@Override
	public void handleBy(MessageHandler handler) throws Exception {
		handler.handle(this);
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public Message copy() {
		return new AddChildren(children);
	}

	@Override
	public String toString() {
		return "addChildren{" +
				"children=" + children +
				", uuid=" + uuid +
				'}';
	}
}

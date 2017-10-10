package ru.nsu.ccfit.bogush.chat.msg.types;

import ru.nsu.ccfit.bogush.chat.Node;
import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "parent")
public class SetParent implements Message {
	@XmlElement(name = "node", required = true)
	public Node node;

	@XmlAttribute(name = "uuid", required = true)
	public UUID uuid;

	public SetParent() {}

	public SetParent(Node node) {
		this.node = node;
		this.uuid = UUID.randomUUID();
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
		return new SetParent(node);
	}

	@Override
	public String toString() {
		return "setParent{" +
				"parent=" + node +
				", uuid=" + uuid +
				'}';
	}
}

package ru.nsu.ccfit.bogush.chat.msg.types;

import ru.nsu.ccfit.bogush.chat.Node;
import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "goodbye")
public class Goodbye implements Message {
	@XmlAttribute(name = "uuid", required = true)
	public UUID uuid;

	@XmlElement(name = "from", required = true)
	public Node node;

	public Goodbye() {}

	public Goodbye(Node node) {
		this(node, UUID.randomUUID());
	}

	public Goodbye(Node node, UUID uuid) {
		this.node = node;
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
		return new Goodbye(node);
	}

	@Override
	public String toString() {
		return "goodbye{from " + node + ", with uuid: " + uuid + '}';
	}
}

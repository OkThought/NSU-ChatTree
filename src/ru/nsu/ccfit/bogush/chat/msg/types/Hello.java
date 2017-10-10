package ru.nsu.ccfit.bogush.chat.msg.types;

import ru.nsu.ccfit.bogush.chat.Node;
import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "hello")
public class Hello implements Message {
	@XmlElement(name = "from", required = true)
	public Node node;

	@XmlAttribute(name = "uuid", required = true)
	public UUID uuid;

	public Hello() {}

	public Hello(Node node) {
		this.node = node;
		uuid = UUID.randomUUID();
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
		return new Hello(node);
	}

	@Override
	public String toString() {
		return "hello{from " + node + " with uuid: " + uuid + "}";
	}
}

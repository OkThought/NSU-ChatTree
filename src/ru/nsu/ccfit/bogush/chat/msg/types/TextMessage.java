package ru.nsu.ccfit.bogush.chat.msg.types;

import ru.nsu.ccfit.bogush.chat.Node;
import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

@XmlRootElement(name = "message")
@XmlType(name = "message")
public class TextMessage implements Message {
	@XmlElement(name = "text", required = true)
	public String text;

	@XmlElement(name = "author", required = true)
	public Node author;

	@XmlAttribute(name = "uuid", required = true)
	public UUID uuid;

	public TextMessage() {}

	public TextMessage(String text, Node author) {
		this.text = text;
		this.author = author;
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
		return new TextMessage(text, author);
	}

	@Override
	public String toString() {
		return "[" + uuid + "] " + author.name + ": " + text;
	}
}

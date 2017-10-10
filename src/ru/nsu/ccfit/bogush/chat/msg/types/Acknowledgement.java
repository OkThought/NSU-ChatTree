package ru.nsu.ccfit.bogush.chat.msg.types;

import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

@XmlRootElement(name = "ackn")
@XmlType(name = "ackn")
public class Acknowledgement implements Message {
	@XmlAttribute(name = "uuid", required = true)
	public UUID uuid;

	public Acknowledgement() {}

	public Acknowledgement(UUID uuid) {
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

	public boolean successOf(Message msg) {
		return msg.getUUID().equals(uuid);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Acknowledgement that = (Acknowledgement) o;

		return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
	}

	@Override
	public Message copy() {
		return new Acknowledgement(uuid);
	}

	@Override
	public int hashCode() {
		return uuid != null ? uuid.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "ackn{" +
				"uuid=" + uuid +
				'}';
	}
}

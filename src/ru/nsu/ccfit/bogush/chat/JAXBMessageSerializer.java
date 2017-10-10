package ru.nsu.ccfit.bogush.chat;

import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageDeserializer;
import ru.nsu.ccfit.bogush.chat.msg.MessageSerializer;
import ru.nsu.ccfit.bogush.chat.msg.types.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class JAXBMessageSerializer implements MessageSerializer, MessageDeserializer {
	public static final String ENCODING = "UTF-8";
	private final Marshaller marshaller;
	private final Unmarshaller unmarshaller;
	private static JAXBContext jaxbContext;

	static {
		try {
			jaxbContext = JAXBContext.newInstance(
					TextMessage.class,
					Goodbye.class,
					AddChildren.class,
					SetParent.class,
					Acknowledgement.class,
					Hello.class
			);
		} catch (JAXBException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}


	public JAXBMessageSerializer() throws JAXBException {
		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	public Message deserialize(byte[] data, int len) throws JAXBException {
//		System.out.println(new String(data, 0, len));
		return (Message) unmarshaller.unmarshal(new ByteArrayInputStream(data, 0, len));
	}

	private byte[] marshal(Message msg) throws JAXBException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		marshaller.marshal(msg, output);
//		marshaller.marshal(msg, System.out);
//		System.out.println();
		return output.toByteArray();
	}

	@Override
	public byte[] serialize(TextMessage msg) throws JAXBException {
		return marshal(msg);
	}

	@Override
	public byte[] serialize(AddChildren msg) throws Exception {
		return marshal(msg);
	}

	@Override
	public byte[] serialize(Acknowledgement msg) throws JAXBException {
		return marshal(msg);
	}

	@Override
	public byte[] serialize(Goodbye msg) throws Exception {
		return marshal(msg);
	}

	@Override
	public byte[] serialize(Hello msg) throws Exception {
		return marshal(msg);
	}

	@Override
	public byte[] serialize(SetParent msg) throws Exception {
		return marshal(msg);
	}
}

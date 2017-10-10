package ru.nsu.ccfit.bogush.chat.msg;

import ru.nsu.ccfit.bogush.chat.msg.types.*;

public interface MessageSerializer {
	byte[] serialize(TextMessage msg) throws Exception;
	byte[] serialize(AddChildren msg) throws Exception;
	byte[] serialize(Acknowledgement msg) throws Exception;
	byte[] serialize(Goodbye msg) throws Exception;
	byte[] serialize(Hello msg) throws Exception;
	byte[] serialize(SetParent msg) throws Exception;
}

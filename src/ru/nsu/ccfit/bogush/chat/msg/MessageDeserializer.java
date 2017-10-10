package ru.nsu.ccfit.bogush.chat.msg;

public interface MessageDeserializer {
	Message deserialize(byte[] data, int len) throws Exception;
}

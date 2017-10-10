package ru.nsu.ccfit.bogush.chat.msg;

import java.util.UUID;

public interface Message {
	byte[] serializeBy(MessageSerializer serializer) throws Exception;
	void handleBy(MessageHandler handler) throws Exception;
	UUID getUUID();
	Message copy();
}

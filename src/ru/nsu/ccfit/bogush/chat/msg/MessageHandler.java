package ru.nsu.ccfit.bogush.chat.msg;

import ru.nsu.ccfit.bogush.chat.msg.types.*;

public interface MessageHandler {
	void handle(TextMessage msg) throws Exception;
	void handle(AddChildren msg) throws Exception;
	void handle(Acknowledgement msg) throws Exception;
	void handle(Goodbye msg) throws Exception;
	void handle(Hello msg) throws Exception;
	void handle(SetParent msg) throws Exception;
}

package ru.nsu.ccfit.bogush.chat;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ConsoleChatClient extends Thread {
	private TextSender sender;

	public ConsoleChatClient(TextSender sender) {
		super(ConsoleChatClient.class.getSimpleName());
		this.sender = sender;
	}

	@Override
	public synchronized void start() {
		super.start();
	}

	@Override
	public void run() {
		try {
			Console console = System.console();
			while (!Thread.interrupted()) {
				if (console.reader().ready()) {
					String line = console.readLine();
					sender.send(line);
				}
				Thread.sleep(100);
			}
		} catch (IOException e) {
			e.printStackTrace();
			interrupt();
		} catch (InterruptedException ignored) {

		}
	}

	@Override
	public void interrupt() {
		System.out.println(ConsoleChatClient.class.getSimpleName() + " is interrupting");
		super.interrupt();
		System.out.println(ConsoleChatClient.class.getSimpleName() + " is interrupted. " +
				"You no longer can type messages in console :(");
	}
}

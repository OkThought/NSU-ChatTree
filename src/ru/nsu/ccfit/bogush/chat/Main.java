package ru.nsu.ccfit.bogush.chat;

import javax.xml.bind.JAXBException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
	public static final int ARGS_MIN = 3;
	public static final int EXIT_FAILURE = -1;

	public static void main(String[] args) throws InterruptedException {
		if (args.length < ARGS_MIN) {
			System.err.println("Too few arguments, required " + ARGS_MIN);
			usage();
			System.exit(EXIT_FAILURE);
		}

		String name = args[0];
		int loss = Integer.parseInt(args[1]);
		int port = Integer.parseInt(args[2]);
		String parentAddress = null;
		int parentPort = 0;
		if (args.length == 5) {
			parentAddress = args[3];
			parentPort = Integer.parseInt(args[4]);
		}

		try {
			final NodeController nodeController = new NodeController(name, loss, port, parentAddress, parentPort);
			final ConsoleChatClient consoleChatClient = new ConsoleChatClient(nodeController);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println(); // add line after possible '^C' character
				consoleChatClient.interrupt();
				nodeController.finish();
			}));
			nodeController.start();
			consoleChatClient.start();

			consoleChatClient.join();
			nodeController.join();
		} catch (UnknownHostException | JAXBException | SocketException e) {
			e.printStackTrace();
			System.exit(EXIT_FAILURE);
		}
	}

	public static void usage() {
		System.out.println("Usage: java -jar ChatTree.jar name loss port [parentAddress parentPort]");
	}
}

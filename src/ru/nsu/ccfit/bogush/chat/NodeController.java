package ru.nsu.ccfit.bogush.chat;

import ru.nsu.ccfit.bogush.chat.msg.Message;
import ru.nsu.ccfit.bogush.chat.msg.MessageHandler;
import ru.nsu.ccfit.bogush.chat.msg.types.*;

import javax.xml.bind.JAXBException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class NodeController extends Thread implements MessageHandler, TextSender {
	private static final int MESSAGE_QUEUE_CAPACITY = 20;
	private static final long TIMEOUT = 600; // millis
	private static final int MAX_PACKET_SIZE = 2 << 10; // 2 kilobytes
	private static final int HISTORY_CAPACITY = 50;
	private final Node currentNode;
	private final UUID[] history = new UUID[HISTORY_CAPACITY];
	private int lastHistoryIndex = 0;
	private int historySize = 0;

	private Node parent = null;

	private List<Node> children = new LinkedList<>();

	private final JAXBMessageSerializer serializer;
	private final DatagramSocket socket;

	private final MessageSender sender;
	private final MessageReceiver receiver;

	private DatagramPacket packet;


	public NodeController(String name, int lossProbability, int port, String parentAddress, int parentPort)
			throws UnknownHostException, JAXBException, SocketException {
		super(NodeController.class.getSimpleName());
//		System.out.println("Created " + NodeController.class.getSimpleName() + " with name " + name);
		InetAddress address = InetAddress.getLocalHost();
		currentNode = new Node(name, address.getHostAddress(), port);
		if (parentAddress != null) {
			parent = new Node("parent", parentAddress, parentPort);
		}
		serializer = new JAXBMessageSerializer();
		socket = new DatagramSocket(port);
		sender = new MessageSender(socket, MESSAGE_QUEUE_CAPACITY, TIMEOUT);
		receiver = new MessageReceiver(socket, MAX_PACKET_SIZE, MESSAGE_QUEUE_CAPACITY, lossProbability);
	}

	@Override
	public synchronized void start() {
		receiver.start();
		sender.start();
		super.start();
	}

	@Override
	public void run() {
		try {
			if (parent != null) {
				send(new Hello(currentNode), parent);
				LinkedList<Node> childList = new LinkedList<>();
				childList.add(currentNode);
				send(new AddChildren(childList), parent);
			}
			while (!Thread.interrupted()) {
				packet = receiver.receive();
				Message message = serializer.deserialize(packet.getData(), packet.getLength());
//				System.out.println("receive\t" + message);
				if (message instanceof Acknowledgement) {
//					System.out.println("receive\t" + message);
					message.handleBy(this);
				} else {
//					System.out.println("receive\t" + message);
					sendOnce(new Acknowledgement(message.getUUID()), packet.getAddress(), packet.getPort());
					if (!isInHistory(message.getUUID())) {
//						System.out.println("receive\t" + message);
						addToHistory(message.getUUID());
						message.handleBy(this);
					}
				}
			}
		} catch (InterruptedException | SocketException ignored) {
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	public void finish() {
		Message closeAlert = new Goodbye(currentNode);
		try {
			sendCopiesToAll(closeAlert);
			repairTree();
		} catch (SocketException ignored) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		sender.finish();
		receiver.interrupt();
		socket.close();
		interrupt();
	}

	private void sendOnce(Message msg, Node to) throws Exception {
		sendOnce(msg, to.getIp(), to.port);
	}

	private void sendOnce(Message msg, InetAddress address, int port) throws Exception {
		byte[] data = msg.serializeBy(serializer);
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//		System.out.println("send ack\t" + msg);
		sender.sendOnce(packet);
	}

	private void send(Message msg, InetAddress address, int port) throws Exception {
		byte[] data = msg.serializeBy(serializer);
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//		System.out.println("send\t" + msg);
		sender.send(packet, msg.getUUID());
	}

	private void send(Message msg, Node to) throws Exception {
//		System.out.println("send to " + to + "\t" + msg);
		send(msg, to.getIp(), to.port);
	}

	private void send(Message msg, List<Node> recipients) throws Exception {
		for (Node recipient : recipients) {
			send(msg, recipient);
		}
	}

	private void sendCopies(Message msg, List<Node> recipients) throws Exception {
		for (Node recipient : recipients) {
			send(msg.copy(), recipient);
		}
	}

	private void sendToAllExcept(Message message, Node excepted) throws Exception {
		if (parent != null && !parent.equals(excepted)) {
			send(message, parent);
		}
		for (Node child : children) {
			if (!child.equals(excepted)) {
				send(message, child);
			}
		}
	}

	private void sendCopiesToAll(Message message) throws Exception {
		if (parent != null) {
			send(message, parent);
		}
		for (Node child : children) {
			send(message.copy(), child);
		}
	}

	private void repairTree() throws Exception {
		if (parent != null && children.size() > 0) {
			send(new AddChildren(children), parent);
			sendCopies(new SetParent(parent), children);
		} else if (parent == null && children.size() > 1) {
			send(new AddChildren(children.subList(1, children.size())), children.get(0));
			sendCopies(new SetParent(children.get(0)), children.subList(1, children.size()));
		}
	}

	private void addToHistory(UUID id) {
		history[lastHistoryIndex] = id;
		lastHistoryIndex++;
		if (historySize < HISTORY_CAPACITY - 1) historySize++;
		if (lastHistoryIndex >= HISTORY_CAPACITY) {
			lastHistoryIndex = 0;
		}
	}

	private boolean isInHistory(UUID id) {
		for (int i = 0; i < historySize; i++) {
			if (history[i].equals(id)) return true;
		}
		return false;
	}

	private void print(TextMessage textMessage) {
		System.out.println(textMessage.author.name + ": " + textMessage.text);
	}

	@Override
	public void send(String text) {
//		System.out.println("Sending text: " + text);
		TextMessage msg = new TextMessage(text, currentNode);
		System.out.print("\033[1A"); // move caret to the previous line
		print(msg);
		try {
			sendCopiesToAll(msg);
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	@Override
	public void handle(TextMessage msg) throws Exception {
		if (!msg.author.equals(currentNode)) {
			print(msg);
		}
		Node senderNode = new Node(packet.getAddress().getHostAddress(), packet.getPort());
		sendToAllExcept(msg, senderNode);
	}

	@Override
	public void handle(AddChildren msg) throws Exception {
		if (msg.getChildren().size() != 0) {
			for (Node child : msg.getChildren()) {
				if (!children.contains(child)) {
					children.add(child);
//					System.out.println("\tadded " + child);
				}
			}
		}
	}

	@Override
	public void handle(SetParent msg) throws Exception {
		parent = msg.node;
	}

	@Override
	public void handle(Acknowledgement msg) throws Exception {
		sender.removeById(msg.getUUID());
	}

	@Override
	public void handle(Goodbye msg) throws Exception {
		System.out.println(msg.node.name + " left chat");
		if (msg.node.equals(parent)) {
			parent = null;
		} else if(!children.remove(msg.node)){
//			System.err.println("Couldn't remove " + msg.node.name);
		}
		Node senderNode = new Node(packet.getAddress().getHostAddress(), packet.getPort());
		sendToAllExcept(msg, senderNode);
	}

	@Override
	public void handle(Hello msg) throws Exception {
		System.out.println(msg.node.name + " entered chat");
		Node senderNode = new Node(packet.getAddress().getHostAddress(), packet.getPort());
		sendToAllExcept(msg, senderNode);
	}
}

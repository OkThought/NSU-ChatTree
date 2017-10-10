package ru.nsu.ccfit.bogush.chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class MessageReceiver extends Thread {
	private final DatagramSocket socket;
	private final int packetSize;
	private BlockingQueue<DatagramPacket> queue;
	private final int lossProbability;
	private final IntStream randomInts = new Random().ints();

	public MessageReceiver(DatagramSocket socket, int packetSize, int queueCapacity, int lossProbability) {
		super(MessageReceiver.class.getSimpleName());
		this.socket = socket;
		this.packetSize = packetSize;
		queue = new LinkedBlockingQueue<>(queueCapacity);
		this.lossProbability = lossProbability;
	}

	public DatagramPacket receive() throws InterruptedException {
		return queue.take();
	}

	private int rand() {
		return ThreadLocalRandom.current().nextInt(0, 99 + 1);
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				DatagramPacket packet = new DatagramPacket(new byte[packetSize], packetSize);
				socket.receive(packet);
				if (rand() >= lossProbability) {
					queue.put(packet);
				}
			}
		} catch (SocketException ignored) {
		} catch (Exception e) {
			interrupt();
		}
	}
}

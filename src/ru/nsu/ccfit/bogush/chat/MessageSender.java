package ru.nsu.ccfit.bogush.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.UUID;

public class MessageSender extends Thread {
	private LinkedList<Entry> packetsToResend;
	private final long timeout;
	private final DatagramSocket socket;
	private int capacity;
	private boolean noMorePackets = false;
	private final Object lockTheList = new Object();
	private final Object lockTheSocket = new Object();

	public MessageSender(DatagramSocket socket, int capacity, long timeout) {
		super(MessageSender.class.getSimpleName());
		this.socket = socket;
		this.capacity = capacity;
		packetsToResend = new LinkedList<>();
		this.timeout = timeout;
	}

	private void sendPacket(DatagramPacket packet) throws IOException {
		synchronized (lockTheSocket) {
			socket.send(packet);
		}
	}

	public void sendOnce(DatagramPacket packet) throws IOException {
		sendPacket(packet);
	}

	public void send(DatagramPacket packet, UUID id) throws Exception {
		sendOnce(packet);
		synchronized (lockTheList) {
			if (noMorePackets) return;
			if (packetsToResend.size() == capacity) {
				packetsToResend.removeFirst();
			}
			packetsToResend.add(new Entry(packet, id, System.currentTimeMillis()));
			lockTheList.notifyAll();
		}
	}

	public void removeById(final UUID id) {
		synchronized (lockTheList) {
			packetsToResend.remove(new Entry(null, id, 0));
			lockTheList.notifyAll();
//			System.out.println("packets left:" + packetsToResend.size());
		}
	}

	public void finish() {
		synchronized (lockTheList) {
			noMorePackets = true;
			try {
				while(!packetsToResend.isEmpty()) {
					lockTheList.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		interrupt();
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Entry entry;
				long timeSent;
				synchronized (lockTheList) {
					while(packetsToResend.isEmpty()) {
						lockTheList.wait();
					}
					entry = packetsToResend.removeFirst();
					timeSent = entry.timeSent;
					entry.timeSent = System.currentTimeMillis();
					packetsToResend.add(entry);
				}
				long currentTime = System.currentTimeMillis();
				long sleepRemaining = currentTime - timeSent;
				sleepRemaining = timeout - sleepRemaining;
				if (sleepRemaining > 0) sleep(sleepRemaining);
				sendPacket(entry.packet);
			}
		} catch (InterruptedException | SocketException ignored) {
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	private static class Entry {
		private DatagramPacket packet;
		private UUID id;
		private long timeSent;

		private Entry(DatagramPacket packet, UUID id, long timeSent) {
			this.packet = packet;
			this.id = id;
			this.timeSent = timeSent;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Entry that = (Entry) o;

			return id.equals(that.id);
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}
}

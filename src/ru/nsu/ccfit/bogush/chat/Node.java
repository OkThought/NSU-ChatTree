package ru.nsu.ccfit.bogush.chat;

import javax.xml.bind.annotation.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

@XmlRootElement(name = "node")
@XmlType(name = "node")
@XmlAccessorType(XmlAccessType.NONE)
public class Node {
	private static final String ANONYMOUS = "Incognito";

	@XmlAttribute(name = "name")
	public String name;

	public String address;

	private InetAddress ip = null;

	@XmlElement(name = "port")
	public int port;

	public Node() {
	}

	public Node(String address, int port) throws UnknownHostException {
		this(ANONYMOUS, address, port);
	}

	public Node(String name, String address, int port) throws UnknownHostException {
		this.name = name;
		this.address = address;
		this.port = port;
		ip = InetAddress.getByName(address);
	}

	@XmlElement(name = "address")
	public void setAddress(String address) throws UnknownHostException {
		this.address = address;
		ip = InetAddress.getByName(address);
	}

	public String getAddress() {
		return address;
	}

	public InetAddress getIp() {
		return ip;
	}

	@Override
	public String toString() {
		return name;
//		return "[" + name + ", " + address + ":" + port + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Node node = (Node) o;

		if (port != node.port) return false;
		return ip != null ? ip.equals(node.ip) : node.ip == null;
	}

	@Override
	public int hashCode() {
		int result = ip != null ? ip.hashCode() : 0;
		result = 31 * result + port;
		return result;
	}
}

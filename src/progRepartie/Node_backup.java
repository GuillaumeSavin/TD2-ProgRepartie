package progRepartie;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Node_backup
{

	// this will be used for both sending and receiving of messages
	final DatagramSocket udp;

	// the port on which the UDP server will listen to
	final int port;

	// a node has some neighbors
	final Set<String> neighbors = new HashSet<>();
	final Set<Long> receivedMessage = new HashSet<>();

	public Node_backup(int port) throws SocketException {
		this.port = port;
		this.udp = new DatagramSocket(port);

		Thread serverThread = new Thread(() -> {
			try {
				while (true) {
					// creates a new packet for the reception of a new message
					byte[] buf = new byte[10000];
					DatagramPacket p = new DatagramPacket(buf, buf.length);

					// blocks until a packet arrives
					udp.receive(p);

					// converts the bytes to a Java message
					ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
					Message_backup msg =  (Message_backup) in.readObject();
					in.close();




					// registers the sender as a new neighbors for this node
					neighbors.add(p.getAddress().getHostName());

					if(!receivedMessage.contains(msg.id))
					{
						receivedMessage.add(msg.id);
						broadcast(msg);
					}

					// forwards the message to all my neighbors
					//receivedMessage.add(msg.id);
					//broadcast(msg);

					// prints the incoming message
					System.out.println(p.getAddress().getHostName() + "> " + msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		serverThread.start();
	}

	public void broadcast(Message_backup msg) throws IOException {
		for (String n : neighbors) {
			send(msg, n);
		}
	}

	/*
	 * Sends the given message to the specified recipient node.
	 */
	public void send(Message_backup msg, String recipient) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(msg);
		byte[] buf = bos.toByteArray();
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		p.setAddress(InetAddress.getByName(recipient));
		p.setPort(port);
		udp.send(p);
	}

	public static void main(String[] args) throws IOException {

		System.out.println("My IP is " + InetAddress.getLocalHost().getHostAddress());
		// creates a new node listening on port 1234
		Node_backup node = new Node_backup(1234);
		node.neighbors.add("134.59.143.69");
		node.neighbors.add("134.59.143.109");
		node.neighbors.add("134.59.143.121");
		node.neighbors.add("134.59.143.117");
		node.neighbors.add("134.59.143.118");

		Scanner scanner = new Scanner(System.in);
		/*
		 * node.neighbors.add("134.59.143.120"); neighbors.add("134.59.143.71");
		 * neighbors.add("134.59.143.70"); neighbors.add("10.0.2.15");
		 * neighbors.add("134.59.143.115");
		 */

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(10000);
					System.out.println("I now have " + node.neighbors.size() + " neighbors");
				} catch (InterruptedException e) {
				}
			}
		}).start();

		while (true) {
			//String line; //= scanner.nextLine();
			//line = "Kanade Forever !";
			Message_backup msg = new Message_backup("Kanade Tachibana Forever !");
			node.broadcast(msg);
		}

	}

}

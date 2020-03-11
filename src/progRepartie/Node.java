package progRepartie;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Node {

	// this will be used for both sending and receiving of messages
	final DatagramSocket udp;
	String userName;
	// the port on which the UDP server will listen to
	final int port;

	// a node has some neighbors
	final Set<Neighbor> neighbors = new HashSet<>();
	final Set<Long> receivedMsg = new HashSet<>();

	public Node(int port) throws SocketException {
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
					ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( buf ) );
					try
					{
						Message m = ( Message ) in.readObject();
						in.close();
						//String msg = new String(buf);

						// registers the sender as a new neighbors for this node
						Neighbor neighbor = new Neighbor(p.getAddress().getHostName(),m.senders.get(m.senders.size() - 1));
						neighbors.remove(neighbor);
						neighbors.add(neighbor);

						// forwards the message to all my neighbors if it was not already received
						if (!receivedMsg.contains(m.id)) {
							receivedMsg.add(m.id);
							m.senders.add(userName);
							broadcast(m);
							// prints the incoming message
							System.out.println(p.getAddress().getHostName() + "> " + m);
						}
					}
					catch (Exception e)
					{
						System.err.println("Invalid message from " + p.getAddress().getHostName());
					}




				}
			} catch (Exception e) {

				e.printStackTrace();
			}

		});

		serverThread.start();
	}

	public void broadcast(Message msg) throws IOException {
		for (Neighbor n : neighbors) {
			send(msg, n);
		}
	}

	/*
	 * Sends the given message to the specified recipient node.
	 */
	public void send(Message m, Neighbor recipient) throws IOException
	{
		//String userName = System.getProperty("user.name");

		if(m.senders.isEmpty() || ! m.senders.get(m.senders.size() - 1).equals(userName))
		{
			m.senders.add(userName);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); // defines that streamed bytes will be stored in an array
		ObjectOutputStream oos = new ObjectOutputStream(bos); // generates a stream of bytes out of an object
		oos.writeObject(m); // do the conversion
		oos.close();
		byte[] buf = bos.toByteArray(); // obtain the byte array that was written out
		DatagramPacket p = new DatagramPacket(buf, buf.length); // then send it through an UDP datagram
		p.setAddress(InetAddress.getByName(recipient.ip));
		p.setPort(port);
		udp.send(p);
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		System.out.println("My IP is " + InetAddress.getLocalHost().getHostAddress());
		// creates a new node listening on port 1234
		Node node = new Node(1234);
		node.userName = "Kanade Tachibana";
		//node.neighbors.add(new Neighbor("134.59.143.70",null));
		node.neighbors.add(new Neighbor("134.59.143.109",null));
		node.neighbors.add(new Neighbor("134.59.143.69",null));
		node.neighbors.add(new Neighbor("134.59.143.121",null));
		node.neighbors.add(new Neighbor("134.59.143.117",null));
		node.neighbors.add(new Neighbor("134.59.143.118",null));
		node.neighbors.add(new Neighbor("134.59.143.110",null));


		Scanner scanner = new Scanner(System.in); //lit string sur entrée standard
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
			Thread.sleep(10000);

			//String line = scanner.nextLine(); //reads a line from stdin
			//Message m = new Message("Kanade Tachibana Forever !");


			Message m = new Message(node.neighbors);
			//m.content = "Kanade Tachibana Forever ! ";

			node.broadcast(m);
		}

	}

}

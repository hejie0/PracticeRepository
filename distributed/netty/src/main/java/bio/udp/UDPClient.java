package bio.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
	public static void main(String[] args) throws IOException {
		String str = "hello udp server";
		DatagramPacket sendPacket = new DatagramPacket(str.getBytes(), str.length(), InetAddress.getByName("localhost"), 8000);
		DatagramSocket client = new DatagramSocket();
		client.send(sendPacket); //发送udp包到服务端

		byte[] buff = new byte[1024];
		DatagramPacket recvPacket = new DatagramPacket(buff, buff.length);
		client.receive(recvPacket); //接收来自服务端的udp包
		System.out.println(new String(buff, 0, recvPacket.getLength()));
		client.close();
	}
}

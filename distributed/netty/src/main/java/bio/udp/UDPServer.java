package bio.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
	public static void main(String[] args) throws IOException {
		DatagramSocket server = new DatagramSocket(8000); //创建udp服务
		byte[] buff = new byte[1024];
		DatagramPacket recvPacket = new DatagramPacket(buff, buff.length);
		server.receive(recvPacket); //接收来自客户端的udp包, 等待接收包
		System.out.println(new String(buff, 0, recvPacket.getLength()));

		String str = "hello udp client";
		DatagramPacket sendPacket = new DatagramPacket
				(str.getBytes(), str.length(), recvPacket.getAddress(), recvPacket.getPort());
		server.send(sendPacket);
		server.close();
	}
}

package zkclient.demo;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

/**
 * zk序列化工具
 * MyZkSerializer
 */
public class MyZkSerializer implements ZkSerializer {
	String charset = "UTF-8";
	
	/**
	 * 解码，二进制转UTF-8字符串
	 */
	public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		try {
			return new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			throw new ZkMarshallingError(e);
		}
	}
	/**
	 * 编码，字符串转UTF-8二进制
	 */
	public byte[] serialize(Object obj) throws ZkMarshallingError {
		try {
			return String.valueOf(obj).getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new ZkMarshallingError(e);
		}
	}
}

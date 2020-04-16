package NIO;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * HeJie
 * @author ABrightFuture
 * 
 * 一、通道（Channel）：用于源节点与目标节点的连接。在Java NIO中负责缓冲区中数据的传输。
 *                                              通道本身不存储数据，因此需要配合缓冲区进行传输。
 * 二、通道的主要实现类
 * java.nio.channels.Channel接口：
 * 		|--FileChannel
 * 		|--SocketChannel
 * 		|--ServerSocketChannel
 * 		|--DatagramChannel
 * 
 * 三、获取通道
 * 1. Java 针对支持通道的类提供了getChannel()方法
 *		本地IO：
 *		FileInputStream/FileOutputStream
 *		RandomAccessFile
 *
 *		网络IO：
 *		Socket
 *		ServerSocket
 *		DatagramSocket
 *
 * 2. JDK 1.7 中的NIO.2 针对各个通道提供了静态方法open()
 * 3. JDK 1.7 中的NIO.2 的Files工具类的newByteChannel()
 * 
 * 四、通道之间的数据传输
 * transferFrom();
 * transferTo();
 *
 * 五、分散(Scatter)与聚集(Gather)
 * 分散读取(Scattering Reads)：将通道中的数据分散到多个缓冲区中
 * 聚集写入(Gathering Writes)：将多个缓冲区中的数据聚集到通道中
 *
 * 六、字符集：Charset
 * 编码：字符串-->字符数组
 * 解码：字符数组-->字符串
 */
public class TestChannel {

	@Test
	public void testChannel6() throws Exception {
		Charset utf8 = Charset.forName("UTF-8");
		//CharsetEncoder encoder = charset.newEncoder(); //编码器
		//CharsetDecoder decoder = charset.newDecoder(); //解码器

		CharBuffer charBuffer = CharBuffer.allocate(1024);
		charBuffer.put("编解码测试");
		charBuffer.flip();

		ByteBuffer byteBuffer = utf8.encode(charBuffer); //编码
		//CharBuffer charBuffer1 = utf8.decode(byteBuffer); //解码

		System.out.println(Arrays.toString(byteBuffer.array()));
		//System.out.println(Arrays.toString(charBuffer1.array()));

		Charset gbk = Charset.forName("GBK");
		CharBuffer charBuffer2 = gbk.decode(byteBuffer);
		System.out.println(Arrays.toString(charBuffer2.array()));
	}

	@Test
	public void testChannel5(){
		//遍历字符集
		Map<String, Charset> map = Charset.availableCharsets();
		Set<Map.Entry<String, Charset>> keySet = map.entrySet();
		for (Map.Entry<String, Charset> entry : keySet){
			System.out.println(entry.getKey() +"="+ entry.getValue());
		}
	}

	@Test
	public void testChannel4() throws IOException {
		RandomAccessFile raf1 = new RandomAccessFile("C:/Users/22516/Desktop/testSql.txt", "rw");
		//获取通道
		FileChannel inChannel = raf1.getChannel();
		//分配指定大小的缓冲区
		ByteBuffer buf1 = ByteBuffer.allocate(10);
		ByteBuffer buf2 = ByteBuffer.allocate(1024000);
		//分散读取
		ByteBuffer[] bufs = {buf1, buf2};
		inChannel.read(bufs);

		for (ByteBuffer byteBuffer : bufs){
			byteBuffer.flip();
		}
		System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
		System.out.println(new String(bufs[1].array(), 1, bufs[1].limit()));

		//聚集写入
		RandomAccessFile raf2 = new RandomAccessFile("testSql.txt", "rw");
		FileChannel outChannel = raf2.getChannel();
		outChannel.write(bufs);
	}

	@Test
	//·通道之间的数据传输（直接缓冲区方式）
	public void testChannel3() throws IOException {
		FileChannel inChannel = FileChannel.open(Paths.get("E:/Project/ry.zip"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("D:/ry3.zip"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
		
		//·将inChannel通道中的缓冲区转到outChannel通道中。（ 数据的开始位置 0，数据的结束位置 inChannel.size()，传输的目标通道 outChannel）
		inChannel.transferTo(0, inChannel.size(), outChannel);
		//·将inChannel通道中的缓冲区转到outChannel通道中。（数据通道 inChannel， 数据的开始位置 0，数据的结束位置 inChannel.size()）
		//outChannel.transferFrom(inChannel, 0, inChannel.size());
		
		outChannel.close();
		inChannel.close();
	}
	
	@Test //·测试 直接缓冲区 和 非直接缓冲区 的效率
	public void test() {
		
		long start = System.currentTimeMillis();
		testChannel1();
		long end = System.currentTimeMillis();
		System.out.println("非直接缓冲区时间：" + (end - start));
		
		start = System.currentTimeMillis();
		testChannel2();
		end = System.currentTimeMillis();
		System.out.println("直接缓冲区时间：" + (end - start));
		//·测试结果：非直接缓冲区时间：2001。直接缓冲区时间：316。可以看出直接缓冲区效率高
	}
	
	@Test
	//·使用直接缓冲区完成文件的复制（内存映射文件）
	public void testChannel2() {
		try (//1.获取通道         READ读模式、WRITE写模式、CREATE不存在创建文件 存在不创建、CREATE_NEW不存在则创建文件  存在则报错
			 FileChannel inChannel = FileChannel.open(Paths.get("E:/Project/ry.zip"), StandardOpenOption.READ);
			 FileChannel outChannel = FileChannel.open(Paths.get("D:/ry2.zip"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
			) {
			
			//2.内存映射文件
			MappedByteBuffer inMapBuffer = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
			MappedByteBuffer outMapBuffer = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
			
			//3.直接对缓冲区进行数据的读写操作
			byte[] dst = new byte[inMapBuffer.limit()];
			inMapBuffer.get(dst);
			outMapBuffer.put(dst);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	//·利用通道完成文件复制（非直接缓冲区）
	public void testChannel1() {
		
		try (FileInputStream fis = new FileInputStream("E:/Project/ry.zip");
			 FileOutputStream fos = new FileOutputStream("D:/ry1.zip");
			 //1.获取通道
			 FileChannel inChannel = fis.getChannel();
			 FileChannel outChannel = fos.getChannel();
			) {
			
			//2.分配指定大小的缓冲区
			ByteBuffer allocate = ByteBuffer.allocate(1024);
			
			//3.将通道中的数据存入缓冲区中
			while(inChannel.read(allocate) != -1) {
				allocate.flip(); //·切换读数据的模式
				//4.将缓冲区中的数据写入通道中
				outChannel.write(allocate);
				allocate.clear(); //·清空缓冲区
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

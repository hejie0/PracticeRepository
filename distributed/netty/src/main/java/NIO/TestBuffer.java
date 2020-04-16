package NIO;


import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author ABrightFuture
 * 一、缓冲区（Buffer）：在Java NIO中负者数据的存取。缓冲区就是数组。用于存储不同数据类型的数据
 * 根据数据类型不同（boolean 除外），提供了相应类型的缓冲区
 * ByteBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * CharBuffer
 * 
 * 上述缓冲区的管理方式几乎一致，通过 allocate() 获取缓冲区
 * 
 * 二、缓冲区存取数据的两个核心方法：
 * put(): 存入数据到缓冲区中
 * get(): 获取缓冲区中的数据
 * 
 * 三、缓冲区中的4个核心属性：
 * capacity：容量，表示缓冲区中最大存储数据的容量。一旦声明不能改变。
 * limit：界限，表示缓冲区中可以操作数据的大小（limit后面的数据不能进行读写）。
 * position：位置，表示缓冲区中正在操作数据的位置。
 * mark：标记，表示记录当前 position 的位置。可以通过reset()恢复到 mark 的位置。
 * 
 * 0 <= mark <= position <= limit <= capacity
 * 
 * 五、直接缓冲区 与 非直接缓冲区：
 * 非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在JVM的内存中。
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在操作系统的物理内存中。在某种情况可以提高效率。
 */
public class TestBuffer {
	
	@Test
	public void test() {
		ByteBuffer allocate = ByteBuffer.allocateDirect(1024);
		
		System.out.println(allocate.isDirect()); // 判断是否是直接缓冲区
	}
	
	@Test //切记reset() 不可配合 rewind() clear()使用
	public void testReset() {
		ByteBuffer allocate = ByteBuffer.allocate(1024);
		allocate.put("abcde".getBytes());
		allocate.flip();
		System.out.println("==============flip==================");
		print(allocate);
		
		byte[] dst = new byte[allocate.limit()];
		allocate.get(dst, 0, 2);
		System.out.println("==============get==================");
		print(allocate);
		allocate.mark(); //标记一下position
		System.out.println("\n" + new String(dst, 0, dst.length));
		
		byte[] dst2 = new byte[allocate.limit()];
		allocate.get(dst2, 0, 2);
		System.out.println("==============get==================");
		print(allocate);
		System.out.println("\n" + new String(dst2, 0, dst.length));
		
		allocate.reset(); //position恢复到mark位置
		System.out.println("==============reset==================");
		print(allocate);
		
		//8.判断缓冲区中是否还有剩余数据
		if(allocate.hasRemaining()) {
			//	获取缓冲区中可以操作的数量
			System.out.println("缓冲区中可以操作的数量：" + allocate.remaining());
		}

		byte[] dst3 = new byte[allocate.limit()];
		allocate.get(dst3, 0, 3);
		System.out.println("==============get==================");
		print(allocate);
		System.out.println("\n" + new String(dst3, 0, dst.length));
	}
	
	@Test //其他数据类型操作基本一致
	public void testByte() {
		//1.分配一个指定大小的缓冲区
		ByteBuffer allocate = ByteBuffer.allocate(1024);
		System.out.println("==============allocate==============");
		print(allocate);  //打印缓冲区信息
		
		//2.添加数据
		byte[] b = "hello 你好呀 ^_^".getBytes();
		allocate.put(b); //将数据添加到缓冲区中
		System.out.println("==============put===================");
		print(allocate);  //打印缓冲区信息
		
		//3.切换读数据模式
		allocate.flip();  //切换读数据模式 调用该方法后 limit=position position=0
		System.out.println("==============flip==================");
		print(allocate);  //打印缓冲区信息
		
		//4.利用get()读取缓冲区中的数据
		int limit = allocate.limit();
		byte[] temp = new byte[limit];
		allocate.get(temp);		 //获取缓冲区中的数据
		System.out.println("\n" + new String(temp, 0, temp.length)); //byte转String打印
		
		//获取完数据后 position = 获取完数据的位置，也就是temp.length。方便以后继续读取。
		System.out.println("==============get===================");
		print(allocate);
		
		//5.rewind() 将position = 0
		allocate.rewind();
		System.out.println("==============rewind================");
		print(allocate);
		
		//6.清空缓冲区。但是里面的数据并没有被清空 依然存在，但是处于“被遗忘”状态。恢复到最初状态。
		allocate.clear();
		System.out.println("==============clear=================");
		print(allocate);
		
		//7.继续读取数据，测试clear后是否可以读取到之前的数据
		byte[] temp2 = new byte[limit];
		allocate.get(temp2);
		System.out.println("\n" + new String(temp2, 0, temp2.length));
		
	}
	
	/** Buffer是ByteBuffer相关类的父类 */
	public void print(Buffer allocate) {
		System.out.println(allocate.position()); //获取缓冲区中可以操作的位置
		System.out.println(allocate.limit());    //获取缓冲区中剩余的容量
		System.out.println(allocate.capacity()); //获取缓冲区的容量
	}
	
}

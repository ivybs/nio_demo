package com.ivyb.nio.nio_day01;

/*
* 一.缓存区（buffer）:在java nio中负责数据的存取。
* 缓冲区就是数组，用于存储不同数据类型的数据。
* 根据数据类型不同（boolean除外），提供改了相应类型的缓冲区：
* ByteBuffer\CharBuffer\ShortBuffer\IntBuffer\LongBuffer\FloatBuffer\DoubleBuffer
* 上述缓冲区的管理方式几乎一致，都是通过allocate()获取缓冲区
*
*二. 缓冲区用于存取数据的两个核心方法：
* put():存入数据到缓冲区中
* get():获取缓冲区中的数据
*
* 三.缓冲区中的四个核心属性
* capacity: 容量，表示缓冲区中最大存储数据的容量。一旦声明不能改变。
* limit：界限，表示缓冲区中可以操作数据的大小。（超过limit大小后数据不能进行读写）
* position：位置，表示缓冲区中正在操作数据的位置、
* 0 <= mark <= position <= limit <= capacity
* mark: 标记，表示记录当前position的位置，可以通过reset()回复到mark的位置
*
* 四.直接缓冲区与非直接缓冲区
* 非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在jvm内存中
* 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率，耗费资源大。由于我们是将数据放在物理内存中
* 数据放进去之后就归操作系统关了，我们就管不了了。有一些数据能长时间在内存中和大量数据可以使用这种方法。
*
*
*
*
*
* */

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.ByteBuffer;
@Slf4j
public class TestBuffer {

    @Test
    public void test3(){
        // 分配直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        // 使用isDirect方法判断，当前的缓冲区是否是直接缓冲区
        System.out.println(byteBuffer.isDirect());
    }



    @Test
    public void test2(){
        String str = "abcde";
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        byte[] dst = new byte[byteBuffer.limit()];
        byteBuffer.get(dst,0,2);
        System.out.println(new String(dst,0,2));
        System.out.println(byteBuffer.position());
        // mark（） 标记
        byteBuffer.mark();
        byteBuffer.get(dst,2,2);
        System.out.println(new String(dst,2,2));
        System.out.println(byteBuffer.position());

        // reset():恢复到mark的位置
        byteBuffer.reset();
        System.out.println(byteBuffer.position());

        // 判断缓冲区中是否还有剩余的数据
        if (byteBuffer.hasRemaining()){
            // 获取缓冲区中可以操作的数量
            System.out.println(byteBuffer.remaining());
        }



    }


    @Test
    public void test1(){
        String str = "abcde";

        //1.分配一个指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        log.info("------------------allocate()---------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //2.利用put()方法存入缓冲区中
        byteBuffer.put(str.getBytes());
        log.info("------------------put()---------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //3.flip()切换成读取数据的模式
        byteBuffer.flip();
        log.info("------------------flip()---------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //4.利用get()方法读取缓冲区中的数据
        byte[] dst = new byte[byteBuffer.limit()];
        byteBuffer.get(dst);
        log.info("------------------get()---------------");
        System.out.println(new String(dst,0,dst.length));
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //5.rewind() 回到读模式，可以重新开始读刚才的数据。可重复读数据。
        byteBuffer.rewind();
        log.info("------------------rewind()---------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //6.clear() 清空缓冲区
        // 更详细点应该是：重置了缓冲区的三个重要属性
        // 缓冲区中的数据并没有被清空。但是这些数据处于“被遗忘”状态（重置三个属性）
        byteBuffer.clear();
        log.info("------------------clear()---------------");
        System.out.println((char) byteBuffer.get(1));
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
    }
}

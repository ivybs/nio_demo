package com.ivyb.nio.nio_day01;
/*
* 一.通道：用于源节点与目标节点的连接，在java nio中负
* 责缓冲区中数据的传输。
* 通道本身是不存储任何数据的，(这句话存疑)
* 因此需要配合缓冲区才能完成数据传输。
*
* 二.通道的主要实现类
* java.nio.channels.Channel接口：
*       |---FileChannel  本体文件传输
*       |---SocketChannel   网络文件传输（TCP）
*       |---ServerSocketChannel 网络文件传输（TCP）
*       |---DatagramChannel 网络文件传输（UDP）
*
*
* 三.获取通道
* 1.Java针对支持通道的类提供了getChannel（）方法
*       本地IO:FileInputStream/FileOutputStream/RandomAccessFile
*       网络IO:Socket/ServerSocket/DatagramSocket
*
* 2.在JDK 1.7 中的NIO.2针对各个通道提供了一个静态方法 open()
*
* 3.在JDK 1.7 中的NIO.2的Files工具类的newByteChannel()
*
*
* 四. 通道之间的数据传输
* transferFrom()
* transferTo()
*
*
* 五.分散（Scatter）与聚集(Gather)
* 分散读取（Scattering Reads）：将通道中的数据，分散到多个缓冲区去
* 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
*
*
*
* 六.字符集：Charset
* 编码：字符串转换成字节数组的过程
* 解码：字节数组转换成字符串的过程
* */


import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class TestChannel {

    //
    @Test
    public void test6() throws CharacterCodingException {
        Charset charset = Charset.forName("GBK");
        // 编码与解码就是charBuffer和ByteBuffer之间的转换
        // 获取编码器
        CharsetEncoder charsetEncoder = charset.newEncoder();
        // 获取解码器
        CharsetDecoder charsetDecoder = charset.newDecoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("*****ivyb嘻嘻*****");
        charBuffer.flip();

        // 编码：将 CharBuffer ==> ByteBuffer
        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);


        for(int i = 0;i<14;i++){
            System.out.println(byteBuffer.get(i));
        }
        // 解码：将ByteBuffer ==> CharBuffer
        //byteBuffer.flip();    //这里教程里是写出来才能读取，但是实际上，flip之后ByteBuffer里面数据就没有了
        //System.out.println(byteBuffer.limit());

        CharBuffer charBuffer1 = charsetDecoder.decode(byteBuffer);
        System.out.println(charBuffer1.toString());

        System.out.println("-----------------");
        // GBK编码 UTF-8解码
        Charset charset1 = Charset.forName("UTF-8");
        // 而这里flip后才能正常进行decode
        byteBuffer.flip();
        CharBuffer charBuffer2 = charset1.decode(byteBuffer);
        System.out.println(charBuffer2.toString());
    }




    // 字符集
    @Test
    public void test5(){
        // 看看支持多少种字符集
        Map<String,Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String,Charset>> set = map.entrySet();
        for (Map.Entry<String,Charset> entry :set){
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }




    // 分散和聚集
    @Test
    public void test4() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt","rw");
        //获取通道
        FileChannel channel = randomAccessFile.getChannel();
        //分配指定大小的缓冲区
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(100);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);

        //分散读取
        ByteBuffer[] byteBuffers = {byteBuffer1,byteBuffer2};
        channel.read(byteBuffers);

        for (ByteBuffer byteBuffer: byteBuffers){
            byteBuffer.flip();
        }
        // byteBuffers[0].array() -->把缓冲区变成数组，从下标为0的地方开始，长度为缓冲区中的数据长度
        // 这里得出数据长度要结合之前的flip方法后Limit属性的变化。
        //new String(byteBuffers[0].array(),0,byteBuffers[0].limit())
        System.out.println(new String(byteBuffers[0].array(),0,byteBuffers[0].limit()));
        System.out.println("---------------------- ");
        System.out.println(new String(byteBuffers[1].array(),0,byteBuffers[1].limit()));


        // 聚集写入
        RandomAccessFile randomAccessFile1 = new RandomAccessFile("2.txt","rw");
        FileChannel channel1 = randomAccessFile1.getChannel();
        channel1.write(byteBuffers);
    }


    // 3.通道之间的数据传输(直接缓冲区)
    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("D:/","test_demo","nio_demo","1.jpg"), StandardOpenOption.READ);
        // create_new: 不存在就创建，存在就报错
        // create: 不存在就创建，存在就覆盖
        FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"),StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW,StandardOpenOption.READ);
        //inChannel.transferTo(0,inChannel.size(),outChannel);
        outChannel.transferFrom(inChannel,0,inChannel.size());
        inChannel.close();
        outChannel.close();
    }


    //2.使用直接缓冲区完成文件的复制（内存映射文件）-----只有ByteBuffer支持
    @Test
    public void test2() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("D:/","test_demo","nio_demo","1.jpg"), StandardOpenOption.READ);
        // create_new: 不存在就创建，存在就报错
        // create: 不存在就创建，存在就覆盖
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"),StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW,StandardOpenOption.READ);
        // 从位置0开始读，读取数据长度为inChannel的数据
        // MappedByteBuffer 和 allocateDirect方法是一个道理，即分配一个直接缓冲区
        // 现在这个缓冲区在物理内存中
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY,0,inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE,0,inChannel.size());
        // 由于是直接放在物理内存中，因此不需要再用通道去读、写数据
        // 直接对缓冲区中的数据进行读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);
        inChannel.close();
        outChannel.close();
    }


    //1.利用通道完成文件的复制（非直接缓冲区）
    @Test
    public void test1(){
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel inputStreamChannel = null;
        FileChannel outputStreamChannel = null;
        try {
            fileInputStream = new FileInputStream("1.jpg");
            fileOutputStream = new FileOutputStream("2.jpg");

            //1.获取通道
            inputStreamChannel = fileInputStream.getChannel();
            outputStreamChannel = fileOutputStream.getChannel();

            //2.分配指定大小缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //3.将通道中的数据存入缓冲区中(将缓冲区中的数据卸载到通道中，目的是将数据)
            while (inputStreamChannel.read(byteBuffer) != -1){
                byteBuffer.flip();//切换成读取数据的模式
                //4.将缓冲区中的数据写入通道
                outputStreamChannel.write(byteBuffer);
                byteBuffer.clear();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (outputStreamChannel != null){
                try {
                    outputStreamChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamChannel != null){
                try {
                    inputStreamChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }






    }
}

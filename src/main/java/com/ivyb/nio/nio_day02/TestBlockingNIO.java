package com.ivyb.nio.nio_day02;

/*
* 一.使用NIO完成网络通信的三个核心
*
* 1.通道（Channel）：负责连接
*   java.nio.channels.Channel接口
*       |--SelectableChannel
*           |--SocketChannel  TCP
*           |--ServerSocketChannel  TCP
*           |--DatagramChannel  UDP
*           |--Pipe.SinkChannel
*           |--Pipe.SourceChannel
*
* 2.缓冲区（Buffer）：负责数据的存取
*
* 3.选择器（Selector）：是SelectableChannel 的多路复用器，
* 用于监控SelectableChannel的IO状况
*
*
* */


import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestBlockingNIO {

    // 客户端
    @Test
    public void client() throws IOException {
        //1.获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
        FileChannel inChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);

        // 2.分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 3.读取本地文件，并发送到服务端
        if (inChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        }

        // 4.关闭通道
        inChannel.close();
        socketChannel.close();


    }


    @Test
    public void server() throws IOException {
        // 1.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2.绑定连接端口号
        serverSocketChannel.bind(new InetSocketAddress(9898));

        // 3.获取客户端连接的通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        // 4.分配一个指定大小的缓冲区
        FileChannel outChannel = FileChannel.open(Paths.get("5.txt"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 5.接受客户端的数据，并保存到本地
        if (socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            outChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        // 6.关闭对应通道
        serverSocketChannel.close();
        outChannel.close();
        socketChannel.close();
    }
}
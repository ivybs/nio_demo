package com.ivyb.nio.nio_day02;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestBlockingNIO2 {

    //客户端
    @Test
    public void client() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
        FileChannel inChannel = FileChannel.open(Paths.get("1.txt"), StandardOpenOption.READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if (inChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        // 接受服务端的反馈
        int len = socketChannel.read(byteBuffer);
        if (len  != -1){
            byteBuffer.flip();
            System.out.println(byteBuffer.get(0));
            byteBuffer.clear();
        }
        inChannel.close();
        socketChannel.close();


    }

    // 服务端
    @Test
    public void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9898));
        SocketChannel socketChannel = serverSocketChannel.accept();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel outChannel = FileChannel.open(Paths.get("8.txt"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        if (socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            outChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        // 发送反馈给客户端
        byteBuffer.put("succeed".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        socketChannel.close();
        serverSocketChannel.close();
        outChannel.close();
    }



}

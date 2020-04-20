package com.ivyb.nio.nio_day02;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class TestNonBlockingNIO {

    @Test
    // 客户端
    public void client() throws IOException {
        // 1.获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

        // 2.切换成非阻塞模式
        socketChannel.configureBlocking(false);
        
        // 3.分配一个指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        
        // 4.发送数据给服务端
        byteBuffer.put(new Date().toString().getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        // 每当有用户输入时（terminal），就会向服务端发送请求
        // 可以使用这个原理制作聊天室 只不过需要多个线程。
//        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNext()){
//            String str = scanner.next();
//            byteBuffer.put((new Date().toString()+ "\t" +str).getBytes());
//            byteBuffer.flip();
//            socketChannel.write(byteBuffer);
//        }

        //5.关闭
        byteBuffer.clear();
        socketChannel.close();


    }


    // 服务端
    @Test
    public void server() throws IOException {
        // 1.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2.切换成非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //3.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9898));

        //4.获取选择器
        Selector selector = Selector.open();

        //5.将通道注册到选择器上  selector监控serverSocketChannel这个通道的“接受”事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //6. 轮询式的获取选择器上已经“准备就绪”的事件
        while (selector.select() > 0){
            // 7.获取当前选择器中所有注册的“选择键（已就绪的监听事件）”
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()){
                // 8.获取准备就绪的事件
                SelectionKey selectionKey = it.next();
                // 9.判断具体是什么事件准备就绪
                if (selectionKey.isAcceptable()){
                    // 10.若接受就绪，那么就获取客户端的连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    // 11.切换非阻塞模式
                    socketChannel.configureBlocking(false);

                    //12.将该通道注册到选择器上
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    //13.获取当前选择器上读就绪状态的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    //14.读取数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    if (socketChannel.read(byteBuffer)>0){
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(),0,10));
                        byteBuffer.clear();
                    }
                }

                //15.取消选择键 SelectionKey
                it.remove();
            }

        }

    }
}

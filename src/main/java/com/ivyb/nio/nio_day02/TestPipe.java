package com.ivyb.nio.nio_day02;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {

    @Test
    public void test1() throws IOException {
        //1.获取管道
        Pipe pipe = Pipe.open();

        //2.将缓冲区中的数据写入管道(放入一个线程中)
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Pipe.SinkChannel sinkChannel = pipe.sink();
        byteBuffer.put("ivy".getBytes());
        byteBuffer.flip();
        sinkChannel.write(byteBuffer);

        // 3.pipe读取缓冲区中的数据（放入另一个线程中）
        Pipe.SourceChannel sourceChannel = pipe.source();
        byteBuffer.flip();
        sourceChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array(),0,byteBuffer.limit()));

        sourceChannel.close();
        sinkChannel.close();

    }
}

package com.cc.netty;

import com.cc.commonUtils.SerializationByProtostuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        System.out.println(genericClass.getName() +" 解码 ");
        // 消息 = 消息的长度（int） + 消息的内容（Object）
        if(in.readableBytes()<4){
            return;
        }
        // 标记此时的 readerIndex
        in.markReaderIndex();
        // 获取 消息的长度信息
        int length = in.readInt();
        // 如果可读取的消息大小小于消息的长度信息(丢包处理)
        if(in.readableBytes()<length){
            in.resetReaderIndex();
            return;
        }
        byte[] bytes=  new byte[length];
        // 将 ByteBuf 数据存入到 Byte[] 中
        in.readBytes(bytes);
        // 将字节数据序列化为 java 对象
        Object o = SerializationByProtostuff.deserialize(bytes,genericClass);
        list.add(o);
    }
}

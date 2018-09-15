package com.cc.netty;

import com.cc.commonUtils.SerializationByProtostuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *   编码器
 */
public class RpcEncoder extends MessageToByteEncoder{

    private Class<?> c;

    public RpcEncoder(Class<?> c){
        this.c = c;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        System.out.println("encoder : " + c.getName());
        // 如果 o 的类型是 c 或者 c 的子类
        if(c.isInstance(o)){
            //  将 对象o 序列化为 byte[]
           byte[] bytes =  SerializationByProtostuff.serialize(o);
           //  写入 byte 数组的大小
           byteBuf.writeInt(bytes.length);
           //  写入 byte 数据
           byteBuf.writeBytes(bytes);
        }
    }
}

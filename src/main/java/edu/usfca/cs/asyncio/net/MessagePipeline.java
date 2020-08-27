package edu.usfca.cs.asyncio.net;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import edu.usfca.cs.asyncio.codec.MessageEncoder;
import edu.usfca.cs.asyncio.codec.MessageDecoder;

public class MessagePipeline extends ChannelInitializer<SocketChannel> {

    private ChannelInboundHandlerAdapter inboundHandler;
    private MessageEncoder encoder;

    public MessagePipeline(ChannelInboundHandlerAdapter inboundHandler) {
        this.inboundHandler = inboundHandler;
        encoder = new MessageEncoder();
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new MessageDecoder()); /* Inbound, stateful */
        ch.pipeline().addLast(encoder); /* Outbound */
        ch.pipeline().addLast(inboundHandler); /* Inbound */
    }
}

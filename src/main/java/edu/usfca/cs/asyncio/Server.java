package edu.usfca.cs.asyncio;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import edu.usfca.cs.asyncio.codec.PrefixedMessage;
import edu.usfca.cs.asyncio.net.ServerMessageRouter;

@ChannelHandler.Sharable
public class Server extends ChannelInboundHandlerAdapter {

    ServerMessageRouter messageRouter;

    private Map<ChannelHandlerContext, ConnectionStats> statMap
        = new HashMap<>();

    public Server() {

    }

    public void start()
    throws IOException {
        messageRouter = new ServerMessageRouter(this);
        messageRouter.listen(7777);
        System.out.println("Listening for connections on port 7777");
    }

    public static void main(String[] args)
    throws IOException {
        Server s = new Server();
        s.start();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        /* A connection has been established */
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("Connection established: " + addr);
        statMap.put(ctx, new ConnectionStats());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        /* A channel has been disconnected */
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("Connection lost: " + addr);
        System.out.println(statMap.get(ctx));
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    throws Exception {
        /* Writable status of the channel changed */
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof PrefixedMessage) {
            /* Handle the message */
            PrefixedMessage pmsg = (PrefixedMessage) msg;
            String str = new String(pmsg.payload());
            ConnectionStats stats = statMap.get(ctx);
            if (stats != null) {
                stats.messages.add(str);
                stats.bytes += pmsg.payload().length;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}

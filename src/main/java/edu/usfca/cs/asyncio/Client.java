package edu.usfca.cs.asyncio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import edu.usfca.cs.asyncio.net.MessagePipeline;
import edu.usfca.cs.asyncio.codec.PrefixedMessage;
import edu.usfca.cs.asyncio.util.PerformanceTimer;

public class Client extends ChannelInboundHandlerAdapter {

    private static final int toSend = 1000000;

    private String hostname = "localhost";
    private int port = 7777;

    public Client() {
        /* Do nothing: use default host/port */
    }

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static void main(String[] args)
    throws IOException {
        if (args.length >= 2) {
            Client c = new Client(args[0], Integer.parseInt(args[1]));
            c.send();
        } else {
            Client c = new Client();
            c.send();
        }
    }

    public void send() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        MessagePipeline pipeline = new MessagePipeline(this);

        Bootstrap bootstrap = new Bootstrap()
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(pipeline);

        PerformanceTimer pt = new PerformanceTimer();
        pt.start();

        System.out.println("Connecting to " + hostname + ":" + port);
        ChannelFuture cf = bootstrap.connect(hostname, port);
        cf.syncUninterruptibly();

        List<ChannelFuture> writes = new ArrayList<>();
        Channel chan = cf.channel();
        for (int i = 0; i < toSend; ++i) {
            PrefixedMessage msg = new PrefixedMessage(
                    ("Hello world! This is a test. It is test number: " + i).getBytes());
            writes.add(chan.write(msg));
        }
        chan.flush();
        for (ChannelFuture write : writes) {
            write.syncUninterruptibly();
        }
        pt.stop();
        double time = pt.getLastResult();

        System.out.println("Sent " + toSend + " messages in " + time / 1000 + " s");
        System.out.println("Transferred " + (toSend / time) * 1000 + " messages/s");

        /* Don't quit until we've disconnected: */
        System.out.println("Shutting down");
        workerGroup.shutdownGracefully();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        /* A connection has been established */
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("Connection established: " + addr);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        /* A channel has been disconnected */
        InetSocketAddress addr
            = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("Connection lost: " + addr);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
    throws Exception {
        /* Writable status of the channel changed */
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        /* We're just going to ignore any replies */
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}

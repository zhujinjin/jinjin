package netty2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Client {

	public static void main(String[] args) throws Exception{
		//创建一个事件组
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)   //指定NIO的模式
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				//分隔符
				ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
				sc.pipeline().addLast(new StringDecoder());
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		//发起连接，指定连接服务器端的端口号
		ChannelFuture cf1 = b.connect("127.0.0.1", 8765).sync();
		
		//发送消息
		cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("aaa$_".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("bbbb$_".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("cccccc$_".getBytes()));
		
		//释放
		cf1.channel().closeFuture().sync();
		group.shutdownGracefully();
		
	}
}
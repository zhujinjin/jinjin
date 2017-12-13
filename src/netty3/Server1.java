package netty3;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server1 {
	public static void main(String[] args) throws Exception {
		//1 创建两个Nio事件组 
		//一个是用于处理服务器端接收客户端连接的
		//一个是进行网络通信的（网络读写的）
		EventLoopGroup pGroup = new NioEventLoopGroup();
		EventLoopGroup cGroup = new NioEventLoopGroup();
		
		//2 创建辅助工具类，用于服务器通道的一系列配置
		ServerBootstrap b = new ServerBootstrap();
		b.group(pGroup, cGroup)		//绑定俩个事件组
		.channel(NioServerSocketChannel.class)		//指定NIO的模式
		.option(ChannelOption.SO_BACKLOG, 1024)		//设置tcp缓冲区，将每个连接的客户端对象放到TCP缓冲区中
		.option(ChannelOption.SO_SNDBUF, 32*1024)	//设置发送数据缓冲大小，单位byte
		.option(ChannelOption.SO_RCVBUF, 32*1024)	//这是接收数据缓冲大小
		.option(ChannelOption.SO_KEEPALIVE, true)	//保持连接
		.childHandler(new ChannelInitializer<SocketChannel>() {//用来监听已经连接的客户端的动作和状态。
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				//3 在这里配置具体数据接收方法的处理
				sc.pipeline().addLast(new Server1Handler());
			}
		});
		
		//4 进行绑定，绑定端口8765 
		ChannelFuture cf1 = b.bind(8765).sync();
		ChannelFuture cf2 = b.bind(8764).sync();//开第二个端口
		
		//5 等待关闭
		cf1.channel().closeFuture().sync();
		cf2.channel().closeFuture().sync();
		
		//释放
		pGroup.shutdownGracefully();
		cGroup.shutdownGracefully();
	}
}
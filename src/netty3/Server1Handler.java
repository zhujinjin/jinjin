package netty3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class Server1Handler extends ChannelHandlerAdapter {
   //ChannelHandlerContext的channel已被激活。
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("server channel active... ");
	}
   //从当前Channel的对端读取消息。
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
			ByteBuf buf = (ByteBuf) msg;
			byte[] req = new byte[buf.readableBytes()];
			//将buf读到req字节数组中
			buf.readBytes(req);
			String body = new String(req, "utf-8");
			System.out.println("Server :" + body );
			//服务器端给客户端的响应
			String response = "Hi client!" ;
			ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
//			.addListener(ChannelFutureListener.CLOSE);
			
	}
    //消息读取完毕后执行。
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
			throws Exception {
		System.out.println("读完了");
		ctx.flush();
	}
    //处理异常.
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)
			throws Exception {
		ctx.close();
	}
}
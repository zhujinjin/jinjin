package oio;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 传统socket服务端
 * 总结：
	阻塞点
		server.accept();
		inputStream.read(bytes);
	单线程情况下只能有一个客户端
	用线程池可以有多个客户端连接，但是非常消耗性能
 */
public class OioServer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		/*
		java通过Executors提供四种线程池，分别为：
		newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
		newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
		newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
		newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。*/
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();//线程池
		//创建socket服务,监听10101端口
		ServerSocket server=new ServerSocket(10101);
		System.out.println("服务器启动！");
		while(true){
			//获取一个套接字（阻塞）
			final Socket socket = server.accept();
			System.out.println("来了一个新客户端！");
			newCachedThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					//业务处理
					handler(socket);
				}
			});
			
		}
	}
	
	/**
	 * 读取数据
	 * @param socket
	 * @throws Exception
	 */
	public static void handler(Socket socket){
			try {
				byte[] bytes = new byte[1024];
				InputStream inputStream = socket.getInputStream();
				
				while(true){
					//读取数据（阻塞）
					int read = inputStream.read(bytes);
					if(read != -1){
						System.out.println(new String(bytes, 0, read));
					}else{
						break;
					}
				}
			} catch (Exception e) { 	
				e.printStackTrace();
			}finally{
				try {
					System.out.println("socket关闭");
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
}

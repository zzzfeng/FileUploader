package client;

import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import codec.FileInfoCodecFactory;
import handle.FileInfoClientHandler;

/**
 * 客户端
 * 
 * @author king_fu
 * 
 */
public class Client {
	private static final String HOSTNAME = "localhost";

	private static final int PORT = 9012;

	private static final long CONNECT_TIMEOUT = 30 * 1000L; // 30秒

	public static void main(String[] args) throws Throwable {

		NioSocketConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		/*
		 * connector.getFilterChain().addLast("logger", new LoggingFilter());
		 * connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter(
		 * new ObjectSerializationCodecFactory()));
		 */
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new FileInfoCodecFactory()));
		connector.setHandler(new FileInfoClientHandler());

		IoSession session;
		for (;;) {
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
				future.awaitUninterruptibly();
				session = future.getSession();
				break;
			} catch (RuntimeIoException e) {
				e.printStackTrace();
				Thread.sleep(5000);
			}
		}

		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();
	}
}

package handle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import model.FileInfo;

public class FileInfoServerHandler extends IoHandlerAdapter {

	File destDir = new File("D:\\fileTest\\upload");

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("客戶端又來了");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("客戶端:" + session.getRemoteAddress().toString());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("服務器與客戶端交互發生異常");
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		FileInfo info = (FileInfo) message;

		OutputStream os = null;

		try {
			// 用ObjectOutputStream的方式进行保存，打开之后会有乱码现象
			/*
			 * os = new ObjectOutputStream(new FileOutputStream(
			 * pm.getServerFilePath() + System.currentTimeMillis() +
			 * fm.getFileName())); os.write(fm.getFileStream()); os.flush();
			 */
			// 用PrintStream
			os = new PrintStream(new FileOutputStream(destDir + File.pathSeparator + info.getFileName()));
			os.write(info.getFileContent());
			os.flush();
		} finally {
			os.close();
		}
	}

}

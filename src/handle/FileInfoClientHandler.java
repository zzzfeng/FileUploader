package handle;

import java.io.File;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import model.FileInfo;
import util.BytesUtil;

public class FileInfoClientHandler extends IoHandlerAdapter {

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub

		File srcDir = new File("D:\\fileTest\\source");
		if (srcDir.isDirectory()) {
			File[] files = srcDir.listFiles();

			for (int i = 1; i <= files.length; i++) {
				FileInfo info = new FileInfo();

				System.out.println("序号：【" + i + "】文件名：【" + files[i - 1].getName() + "】加进队列中");

				info.setId(i);
				info.setFileNameLength(files[i - 1].getName().getBytes().length);
				info.setFileName(files[i - 1].getName());
				byte[] content = BytesUtil.getBytes(files[i - 1].getAbsolutePath());
				info.setFileContent(content);
				info.setFileContentLenth(content.length);

				session.write(info);
			}
		} else {
			System.out.println("源文件选择错误");
		}

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("客户端與服务器交互發生異常");
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("客户端信息已送達");
	}

}

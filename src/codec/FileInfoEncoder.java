package codec;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import model.FileInfo;

public class FileInfoEncoder extends ProtocolEncoderAdapter {

	CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

	@Override
	public void encode(IoSession session, Object msg, ProtocolEncoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("客户端开始编码，准备发往服务器......");
		FileInfo info = (FileInfo) msg;
		IoBuffer ioBuffer = IoBuffer.allocate(12 + info.getFileContentLenth() + info.getFileNameLength())
				.setAutoExpand(true);
		ioBuffer.putInt(info.getId());
		ioBuffer.putInt(info.getFileNameLength());
		ioBuffer.putInt(info.getFileContentLenth());
		ioBuffer.putString(info.getFileName(), encoder);
		System.out.println("FileName : " + info.getFileName());
		ioBuffer.put(info.getFileContent());

		ioBuffer.flip();
		out.write(ioBuffer);

	}

}

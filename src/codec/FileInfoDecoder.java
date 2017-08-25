package codec;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import model.FileInfo;

public class FileInfoDecoder extends CumulativeProtocolDecoder {

	private boolean isFinish = false; // 是否已经处理所有数据（当前）
	private boolean isFirst = true; // 是否是第一次进来
	private FileInfo info = new FileInfo();
	private int dataPool = 0;
	private IoBuffer newIoBuffer = IoBuffer.allocate(0).setAutoExpand(true);
	private boolean readHead = false; // 是否读取头部信息
	private int needData = 0; // 一个文件中需要的字节数
	private int headData = 0; // 一个文件中需要的字节数

	public FileInfoDecoder() {
		System.out.println("创建解析器新对象");
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("服务器对客户端发来的消息进行解码。解码开始");

		try {
			this.readFile(in);
			if (isFinish) {
				// 解析完成
				out.write(info);
				dataInit();

			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void readFile(IoBuffer in) throws CharacterCodingException {
		// TODO Auto-generated method stub
		CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
		IoBuffer bufTmp = null;
		byte[] byteValue = null;
		if (isFirst) {
			bufTmp = IoBuffer.allocate(newIoBuffer.remaining() + in.remaining()).setAutoExpand(true);
			bufTmp.order(newIoBuffer.order());

			if (!readHead) {
				// 未读取了头部信息
				info.setFileNameLength(in.getInt(in.position() + 4)); // 文件名长度（字节）

				info.setFileContentLenth(in.getInt(in.position() + 8)); // 文件长度（字节）
				needData = info.getFileContentLenth() + info.getFileNameLength() + 12;// 文件所在数据包的总大小
				headData = info.getFileNameLength() + 12; // 头部信息
				byteValue = new byte[needData];

				readHead = true;
			}
			// 判断是否可以读取所有的数据
			if (needData > newIoBuffer.remaining() + in.remaining()) {
				bufTmp.put(newIoBuffer);
				bufTmp.put(in);
			} else {
				bufTmp.put(newIoBuffer);
				in.get(byteValue);
				bufTmp.put(byteValue);
			}
			bufTmp.flip();
			newIoBuffer = bufTmp;

		} else {
			bufTmp = IoBuffer.allocate(newIoBuffer.remaining() + in.remaining()).setAutoExpand(true);
			bufTmp.order(newIoBuffer.order());

			if (info.getFileContentLenth() > newIoBuffer.remaining() + in.remaining()) {
				bufTmp.put(newIoBuffer);
				bufTmp.put(in);
			} else {

				byteValue = new byte[in.remaining()
						- (newIoBuffer.remaining() + in.remaining() - info.getFileContentLenth())];
				bufTmp.put(newIoBuffer);
				in.get(byteValue);
				bufTmp.put(byteValue);
			}
			bufTmp.flip();
			newIoBuffer = bufTmp;
		}

		// 判断是否可以读取头部信息
		if (headData <= newIoBuffer.remaining() + in.remaining()) {
			info.setId(newIoBuffer.getInt());
			info.setFileNameLength(newIoBuffer.getInt()); // 文件名长度（字节）

			info.setFileContentLenth(newIoBuffer.getInt()); // 文件长度（字节）
			info.setFileName(newIoBuffer.getString(info.getFileNameLength(), decoder)); // 文件名。UTF-8格式

			dataPool = info.getFileContentLenth();
			headData = Integer.MAX_VALUE;
			System.out.println("文件序号：" + info.getId() + "   文件名：" + info.getFileName());
		}

		if (info.getFileContentLenth() > (newIoBuffer.remaining())) {
			// 2.14把limit改成remaining
			/*
			 * bfm.setFileStreamLength(bfm.getFileStreamLength() -
			 * (newIoBuffer.remaining())); // 2.14把limit改成remaining
			 */if (isFirst) { // 第一次
				isFirst = false;
				System.out.println("【服务器解析】未解析数据：" + newIoBuffer.remaining() + "字节");
			}
			isFinish = false; // 数据未读取完。
		} else {
			int remainingData = newIoBuffer.remaining(); // 总共多少
			System.out.println("【服务器解析】当前数据池中总共多少：" + remainingData + " 字节");
			if (remainingData >= dataPool) {
				byteValue = new byte[dataPool];
			} else {
				// byteValue = new byte[remainingData];
				System.out.println("【服务器解析】数据长度未满足，【退出】");
				return; // 退出
			}
			// byteValue = new byte[remainingData];
			newIoBuffer.get(byteValue);
			System.out.println("当前读取的文件大小：" + Math.ceil(dataPool * 1.0 / 1024) + "KB");
			info.setFileContent(byteValue);
			isFinish = true;
			System.out.println("【服务器解析】解析完成");
		}

	}

	private void dataInit() {
		// TODO Auto-generated method stub
		isFinish = false; // 是否已经处理所有数据
		isFirst = true; // 是否是第一次进来
		info = new FileInfo(); // 保存对象
		readHead = false;
	}

}

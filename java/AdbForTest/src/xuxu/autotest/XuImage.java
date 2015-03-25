package xuxu.autotest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import xuxu.autotest.utils.DataUtils;
import xuxu.autotest.utils.ImageUtils;
import xuxu.autotest.utils.ShellUtils;

public class XuImage {
	private String tempFile = System.getProperty("java.io.tmpdir");
	private String temp = new File(tempFile + "screenshot").getAbsolutePath();
	
	private static XuImage image = null;
	
	public static XuImage getXuImage() {
		if (image == null) {
			image = new XuImage();
		}
		
		return image;
	}

	private XuImage() {
		File dumpFile = new File(temp);
		if (!dumpFile.exists()) {
			dumpFile.mkdir();
		}
	}

	/**
	 * 截取屏幕
	 */
	public XuImage screenShot() {
		ShellUtils.getShellOut(ShellUtils
				.shell("screencap -p /data/local/tmp/temp.png"));
		ShellUtils.getShellOut(ShellUtils.cmd("pull /data/local/tmp/temp.png "
				+ temp));
		ShellUtils.shell("rm /data/local/tmp/temp.png");
		return this;
	}

	/**
	 * 截取指定矩形区域的图片
	 * 
	 * @param uper_left_x
	 *            矩形区域的左上角x坐标
	 * @param uper_left_y
	 *            矩形区域的左上角y坐标
	 * @param low_right_x
	 *            矩形区域的右下角x坐标
	 * @param low_right_y
	 *            矩形区域的右下角y坐标
	 */
	public XuImage getSubImage(int uper_left_x, int uper_left_y, int low_right_x,
			int low_right_y) {
		InputStream input = null;
		OutputStream out = null;
		try {
			input = new FileInputStream(new File(temp + "/temp.png").getAbsolutePath());
			out = new FileOutputStream(new File(temp + "/sub_temp.png").getAbsolutePath());
			ImageUtils.cutImage(input, out, "png", uper_left_x, uper_left_y,
					low_right_x - uper_left_x, low_right_y - uper_left_y);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			input.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File tmp = new File(new File(temp + "/temp.png").getAbsolutePath());
		if (tmp.exists()) {
			tmp.delete();
		}

		File sub_temp = new File(new File(temp + "/sub_temp.png").getAbsolutePath());
		if (sub_temp.exists()) {
			sub_temp.renameTo(tmp);
		}

		return this;
	}

	/**
	 * 将截屏文件与目标图片进行对比
	 * 
	 * @param targetImagePath
	 *            目标图片存储路径
	 * @return 图片相同，返回true，否则返回false
	 */
	public boolean sameAs(String targetImagePath) {
		boolean result = false;
		try {
			result = ImageUtils.sameAs(targetImagePath, new File(temp + "/temp.png").getAbsolutePath(), 1.0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 将截屏文件写入本地，以当前系统时间戳命名，格式为PNG
	 * 
	 * @param path
	 *            存储路径
	 */
	public void writeToFile(String path) {
		String oldPath = new File(temp + "/temp.png").getAbsolutePath();
		copyFile(oldPath, new File(path + "/" + DataUtils.getCurrentSystemTime()
				+ ".png").getAbsolutePath());
	}

	private void copyFile(String oldPath, String newPath) {
		try {
			int byteRead = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024 * 5];
				while ((byteRead = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteRead);
				}
				inStream.close();
				fs.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

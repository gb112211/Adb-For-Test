package xuxu.autotest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MonkeyScriptUtils {
	private static String tempFile = System.getProperty("java.io.tmpdir");
	
	public static void writeScript(String param) {
		File file = null;
		try {
			file = createFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("count= 1\n").append("speed= 1.0\n").append("start data >>\n").append(param);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(sb.toString().getBytes("utf-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ShellUtils.cmd("push " + file.getAbsolutePath() + " /data/local/tmp");
	}

	private static File createFile() throws IOException {
		File file = new File(tempFile + "/monkey.txt");
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		} else {
			file.createNewFile();
		}
		
		return file;
	}
}

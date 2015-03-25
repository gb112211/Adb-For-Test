package xuxu.autotest.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtils {
	public static Process cmd(String command) {
		//mac请使用adb的绝对路径
		return process("adb " + command);
	}

	public static Process shell(String command) {
		//mac请使用adb的绝对路径
		return process("adb shell " + command);
//		return process(command);
	}
	
	public static BufferedReader shellOut(Process ps) {
		BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		return br;
	}

	public static String getShellOut(Process ps) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = shellOut(ps);
		String line;

		try {
			while ((line = br.readLine()) != null) {
//				sb.append(line);
			sb.append(line + System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString().trim();
	}

	private static Process process(String command) {
		Process ps = null;
		try {
			ps = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ps;
	}
}

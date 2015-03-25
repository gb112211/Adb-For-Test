package xuxu.autotest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import xuxu.autotest.utils.ReUtils;
import xuxu.autotest.utils.ShellUtils;
import xuxu.autotest.element.Element;

/**
 * 
 * @author xuxu, 274925460@qq.com
 *
 */
public class AdbDevice {
	public AdbDevice() {
		ShellUtils.cmd("wait-for-device");
	}

	/**
	 * 获取设备的id号
	 * 
	 * @return 返回设备id号
	 */
	public String getDeviceId() {
		Process ps = ShellUtils.shell("getprop ro.boot.serialno");
		String serialno = ShellUtils.getShellOut(ps);

		return serialno;
	}

	/**
	 * 获取设备中Android的版本号，如4.4.2
	 * 
	 * @return 返回Android版本号
	 */
	public String getAndroidVersion() {
		Process ps = ShellUtils.shell("getprop ro.build.version.release");
		String androidVersion = ShellUtils.getShellOut(ps);

		return androidVersion;
	}

	/**
	 * 获取设备中SDK的版本号
	 * 
	 * @return 返回SDK版本号
	 */
	public int getSdkVersion() {
		Process ps = ShellUtils.shell("getprop ro.build.version.sdk");
		String sdkVersion = ShellUtils.getShellOut(ps);

		return new Integer(sdkVersion);
	}

	/**
	 * 获取设备屏幕的分辨率
	 * 
	 * @return 返回分辨率数组
	 */
	public int[] getScreenResolution() {
		Pattern pattern = Pattern.compile("([0-9]+)");
		Process ps = ShellUtils
				.shell("dumpsys display | grep PhysicalDisplayInfo");
		ArrayList<Integer> out = ReUtils.matchInteger(pattern,
				ShellUtils.getShellOut(ps));

		int[] resolution = new int[] { out.get(0), out.get(1) };

		return resolution;
	}

	/**
	 * 返回设备电池电量
	 * 
	 * @return 返回电量数值
	 */
	public int getBatteryLevel() {
		Process ps = ShellUtils.shell("dumpsys battery | grep level");
		String out = ShellUtils.getShellOut(ps);

		int level = new Integer(out.split(": ")[1]);

		return level;
	}

	/**
	 * 返回设备电池温度
	 * 
	 * @return 返回温度数值
	 */
	public double getBatteryTemp() {
		Process ps = ShellUtils.shell("dumpsys battery | grep temperature");
		String out = ShellUtils.getShellOut(ps);

		double temp = new Integer(out.split(": ")[1]) / 10.0;

		return temp;
	}

	/**
	 * 获取电池充电状态: 1 : BATTERY_STATUS_UNKNOWN, 未知状态 2 : BATTERY_STATUS_CHARGING,
	 * 充电状态 3 : BATTERY_STATUS_DISCHARGING, 放电状态 4 :
	 * BATTERY_STATUS_NOT_CHARGING, 未充电 5 : BATTERY_STATUS_FULL, 充电已满
	 * 
	 * @return 返回状态数值
	 */
	public int getBatteryStatus() {
		Process ps = ShellUtils.shell("dumpsys battery | grep status");
		String out = ShellUtils.getShellOut(ps);

		int status = new Integer(out.split(": ")[1]);

		return status;
	}

	/**
	 * 获取设备上当前界面的package和activity
	 * 
	 * @return 返回package/activity
	 */
	public String getFocusedPackageAndActivity() {
		Pattern pattern = Pattern.compile("([a-zA-Z0-9.]+/.[a-zA-Z0-9.]+)");
		Process ps = ShellUtils
				.shell("dumpsys input | grep FocusedApplication");
		
//		Process ps = ShellUtils.shell("dumpsys window w | grep \\/ | grep name=");

		ArrayList<String> component = ReUtils.matchString(pattern,
				ShellUtils.getShellOut(ps));

//		 会有FocusedApplication: <null>情况发生
		if (component.isEmpty()) {
			return ReUtils
					.matchString(
							pattern,
							ShellUtils.getShellOut(ShellUtils
									.shell("dumpsys window w | grep \\/ | grep name=")))
					.get(0);
		}

		return component.get(0);
	}

	/**
	 * 获取设备上当前界面的包名
	 * 
	 * @return 返回包名
	 */
	public String getCurrentPackageName() {
		return getFocusedPackageAndActivity().split("/")[0];
	}

	/**
	 * 获取设备上当前界面的activity
	 * 
	 * @return 返回activity名
	 */
	public String getCurrentActivity() {
		return getFocusedPackageAndActivity().split("/")[1];
	}

	/**
	 * 
	 * @param packageName
	 *            应用对应的包名
	 * @return 返回pid值
	 */
	public int getPid(String packageName) {
		Pattern pattern = Pattern.compile("([\" \"][0-9]+)");
		Process ps = ShellUtils.shell("ps | grep -w " + packageName);

		ArrayList<Integer> num = ReUtils.matchInteger(pattern,
				ShellUtils.getShellOut(ps));

		if (num.isEmpty()) {
			throw new TestException("应用包名不存在或者进程未开启...");
		}

		return num.get(0);
	}

	/**
	 * 
	 * @param pid
	 *            进程的pid值
	 * @return 进程被杀死，返回true，否则返回false
	 */
	public boolean killProcess(int pid) {
		Process ps = ShellUtils.shell("kill " + pid);
		String out = ShellUtils.getShellOut(ps);

		if (out.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 退出当前应用
	 * 
	 */
	public void quitCurrentApp() {
		ShellUtils.shell("am force-stop " + getCurrentPackageName());
	}

	/**
	 * 重置当前应用，清除当前应用的数据且重启该应用
	 * 
	 */
	public void resetApp() {
		String component = getFocusedPackageAndActivity();
		clearAppDate(getCurrentPackageName());
		startActivity(component);

	}

	/**
	 * 获取设备中的系统应用列表
	 * 
	 * @return 返回系统应用列表
	 */
	public ArrayList<String> getSystemAppList() {
		ArrayList<String> sysApp = new ArrayList<String>();
		Process ps = ShellUtils.shell("pm list packages -s");
		StringBuilder sb = new StringBuilder();
		BufferedReader br = ShellUtils.shellOut(ps);

		String line;
		try {
			while ((line = br.readLine()) != null) {
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

		Pattern pattern = Pattern.compile(("[a-z]+:[a-zA-Z0-9.]+"));
		ArrayList<String> result = ReUtils.matchString(pattern, sb.toString());

		for (String string : result) {
			sysApp.add(string.split(":")[1]);
		}

		return sysApp;
	}

	/**
	 * 获取设备中的第三方应用列表
	 * 
	 * @return 返回第三方应用列表
	 */
	public ArrayList<String> getThirdAppList() {
		ArrayList<String> thirdApp = new ArrayList<String>();
		Process ps = ShellUtils.shell("pm list packages -3");
		StringBuilder sb = new StringBuilder();
		BufferedReader br = ShellUtils.shellOut(ps);

		String line;
		try {
			while ((line = br.readLine()) != null) {
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

		Pattern pattern = Pattern.compile(("[a-z]+:[a-zA-Z0-9.]+"));
		ArrayList<String> result = ReUtils.matchString(pattern, sb.toString());

		for (String string : result) {
			thirdApp.add(string.split(":")[1]);
		}

		return thirdApp;
	}

	/**
	 * 获取启动应用所花的时间
	 * 
	 * @param component
	 *            package/activity
	 * @return 返回时间值
	 */
	public int getAppStartTotalTime(String component) {
		Process ps = ShellUtils.shell("am start -W " + component
				+ " | grep TotalTime");
		String out = ShellUtils.getShellOut(ps).trim();

		return new Integer(out.split(": ")[1]);
	}

	public XuImage getSceenshot() {
		return XuImage.getXuImage().screenShot();
	}
	
	/**
	 * 复制设备上的文件至本地
	 * 
	 * @param remotePath
	 *            设备上文件路径
	 * @param localPath
	 *            本地路径
	 */
	public void pullFile(String remotePath, String localPath) {
		ShellUtils.getShellOut(ShellUtils.cmd("pull " + remotePath + " "
				+ localPath));
	}

	/**
	 * 推送本地文件至设备
	 * 
	 * @param localPath
	 *            本地文件路径
	 * @param remotePath
	 *            设备上的存储路径
	 */
	public void pushFile(String localPath, String remotePath) {
		ShellUtils.getShellOut(ShellUtils.cmd("push " + localPath + " "
				+ remotePath));
	}

	/**
	 * 安装应用
	 * 
	 * @param appPath
	 *            apk文件所在路径,apk名不能是中文名
	 */
	public void installApp(String appPath) {
		ShellUtils.getShellOut(ShellUtils.cmd("install " + appPath));
	}

	/**
	 * 判断应用是否已经安装
	 * 
	 * @param packageName
	 *            应用的包名
	 * @return 已安装，返回true，否则返回false
	 */
	public boolean isInstalled(String packageName) {
		if (getThirdAppList().contains(packageName)
				|| getSystemAppList().contains(packageName)) {
			return true;
		}
		return false;
	}

	/**
	 * 卸载指定应用
	 * 
	 * @param packageName
	 *            应用包名，非apk名
	 */
	public void removeApp(String packageName) {
		ShellUtils.getShellOut(ShellUtils.cmd("uninstall " + packageName));
	}

	/**
	 * 清除应用的用户数据
	 * 
	 * @param packageName
	 *            应用的包名
	 * @return 清楚成功返回true, 否则返回false
	 */
	public boolean clearAppDate(String packageName) {
		Process ps = ShellUtils.shell("pm clear " + packageName);
		if (ShellUtils.getShellOut(ps).equals("Success")) {
			return true;
		}
		return false;
	}

	/**
	 * 重启设备
	 */
	public void reboot() {
		ShellUtils.cmd("reboot");
	}

	/**
	 * 重启设备进入fastboot模式
	 */
	public void fastboot() {
		ShellUtils.cmd("reboot bootloader");
	}

	/**
	 * 启动一个应用
	 * 
	 * @param component
	 *            应用包名加主类名，packageName/Activity
	 */
	public void startActivity(String component) {
		ShellUtils.shell("am start -n " + component);
	}

	/**
	 * 使用默认浏览器打开一个网页
	 * 
	 * @param url
	 *            网页地址
	 */
	public void startWebpage(String url) {
		ShellUtils.shell("am start -a android.intent.action.VIEW -d " + url);
	}

	/**
	 * 使用拨号器拨打号码
	 * 
	 * @param number
	 *            电话号码
	 */
	public void callPhone(int number) {
		ShellUtils.shell("am start -a android.intent.action.CALL -d tel:"
				+ number);
	}

	/**
	 * 发送一个按键事件
	 * 
	 * @param keycode
	 *            键值
	 */
	public void sendKeyEvent(int keycode) {
		ShellUtils.shell("input keyevent " + keycode);
		sleep(500);
	}

	/**
	 * 长按物理键，需要android 4.4以上
	 * 
	 * @param keycode
	 *            键值
	 */
	public void longPressKey(int keycode) {
		ShellUtils.shell("input keyevent --longpress " + keycode);
		sleep(500);
	}

	/**
	 * 发送一个点击事件
	 * 
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 */
	public void tap(int x, int y) {
		ShellUtils.shell("input tap " + x + " " + y);
		sleep(500);
	}

	/**
	 * 发送一个点击事件
	 * 
	 * @param x
	 *            x小于1，自动乘以分辨率转换为实际坐标，大于1，当做实际坐标处理
	 * @param y
	 *            y小于1，自动乘以分辨率转换为实际坐标，大于1，当做实际坐标处理
	 */
	public void tap(double x, double y) {
		double[] coords = ratio(x, y);
		ShellUtils.shell("input tap " + coords[0] + " " + coords[1]);
		sleep(500);
	}

	/**
	 * 发送一个点击事件
	 * 
	 * @param e
	 *            元素对象
	 */
	public void tap(Element e) {
		ShellUtils.shell("input tap " + e.getX() + " " + e.getY());
		sleep(500);
	}

	/**
	 * 发送一个滑动事件
	 * 
	 * @param startX
	 *            起始x坐标
	 * @param startY
	 *            起始y坐标
	 * @param endX
	 *            结束x坐标
	 * @param endY
	 *            结束y坐标
	 * @param ms
	 *            持续时间
	 */
	public void swipe(int startX, int startY, int endX, int endY, long ms) {
		if (getSdkVersion() < 19) {
			ShellUtils.shell("input swipe " + startX + " " + startY + " "
					+ endX + " " + endY);
		} else {
			ShellUtils.shell("input swipe " + startX + " " + startY + " "
					+ endX + " " + endY + " " + ms);
		}

		sleep(500);
	}

	/**
	 * 发送一个滑动事件
	 * 
	 * @param startX
	 *            起始x坐标
	 * @param startY
	 *            起始y坐标
	 * @param endX
	 *            结束x坐标
	 * @param endY
	 *            结束y坐标
	 * @param ms
	 *            持续时间
	 */
	public void swipe(double startX, double startY, double endX, double endY,
			long ms) {
		double[] coords = ratio(startX, startY, endX, endY);
		if (getSdkVersion() < 19) {
			ShellUtils.shell("input swipe " + coords[0] + " " + coords[1] + " "
					+ coords[2] + " " + coords[3]);
		} else {
			ShellUtils.shell("input swipe " + coords[0] + " " + coords[1] + " "
					+ coords[2] + " " + coords[3] + " " + ms);
		}

		sleep(500);
	}

	/**
	 * 发送一个滑动事件
	 * 
	 * @param e1
	 *            起始元素
	 * @param e2
	 *            终点元素
	 * @param ms
	 *            持续时间
	 */
	public void swipe(Element e1, Element e2, long ms) {
		if (getSdkVersion() < 19) {
			ShellUtils.shell("input swipe " + e1.getX() + " " + e1.getY() + " "
					+ e2.getX() + " " + e2.getY());
		} else {
			ShellUtils.shell("input swipe " + e1.getX() + " " + e1.getY() + " "
					+ e2.getX() + " " + e2.getY() + " " + ms);
		}

		sleep(500);
	}

	/**
	 * 左滑屏幕
	 */
	public void swipeToLeft() {
		swipe(0.8, 0.5, 0.2, 0.5, 500);
	}

	/**
	 * 右滑屏幕
	 */
	public void swipeToRight() {
		swipe(0.2, 0.5, 0.8, 0.5, 500);
	}

	/**
	 * 上滑屏幕
	 */
	public void swipeToUp() {
		swipe(0.5, 0.7, 0.5, 0.3, 500);
	}

	/**
	 * 下滑屏幕
	 */
	public void swipeToDown() {
		swipe(0.5, 0.3, 0.5, 0.7, 500);
	}

	/**
	 * 发送一个长按事件
	 * 
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 */
	public void longPress(int x, int y) {
		swipe(x, y, x, y, 1500);
	}

	/**
	 * 发送一个长按事件
	 * 
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 */
	public void longPress(double x, double y) {
		swipe(x, y, x, y, 1500);
	}

	/**
	 * 发送一个长按事件
	 * 
	 * @param e
	 *            元素对象
	 */
	public void longPress(Element e) {
		swipe(e, e, 1500);
	}

	/**
	 * 发送一段文本，只支持英文，多个空格视为一个空格
	 * 
	 * @param text
	 *            英文文本
	 */
	public void sendText(String text) {
		String[] str = text.split(" ");
		ArrayList<String> out = new ArrayList<String>();
		for (String string : str) {
			if (!string.equals("")) {
				out.add(string);
			}
		}

		int length = out.size();
		for (int i = 0; i < length; i++) {
			ShellUtils.shell("input text " + out.get(i));
			sleep(100);
			if (i != length - 1) {
				sendKeyEvent(AndroidKeyCode.SPACE);
			}
		}
	}

	/**
	 * 清除文本
	 * 
	 * @param text
	 *            获取到的文本框中的text
	 */
	public void clearText(String text) {
		int length = text.length();
		for (int i = length; i > 0; i--) {
			sendKeyEvent(AndroidKeyCode.BACKSPACE);
		}
	}

	private double[] ratio(double x, double y) {
		int[] display = getScreenResolution();
		double[] coords = new double[2];

		if (x < 1) {
			coords[0] = display[0] * x;
		} else {
			coords[0] = x;
		}

		if (y < 1) {
			coords[1] = display[1] * y;
		} else {
			coords[1] = y;
		}

		return coords;
	}

	private double[] ratio(double startX, double startY, double endX,
			double endY) {
		int[] display = getScreenResolution();
		double[] coords = new double[4];

		if (startX < 1) {
			coords[0] = display[0] * startX;
		} else {
			coords[0] = startX;
		}

		if (startY < 1) {
			coords[1] = display[1] * startY;
		} else {
			coords[1] = startY;
		}

		if (endX < 1) {
			coords[2] = display[0] * endX;
		} else {
			coords[2] = endX;
		}

		if (endY < 1) {
			coords[3] = display[1] * endY;
		} else {
			coords[3] = endY;
		}

		return coords;
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

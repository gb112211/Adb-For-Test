package testdemo;

import java.util.ArrayList;

import xuxu.autotest.AdbDevice;
import xuxu.autotest.AndroidKeyCode;
import xuxu.autotest.element.Element;
import xuxu.autotest.element.Position;

/**
 * demo为ApiDemos.apk,使用的真机平台为android 4.3
 * 
 * @author xuxu
 * 
 */

public class Test {
	
	public static void main(String[] args) throws Exception {
		//获取adb连接
		AdbDevice device = new AdbDevice();
		
		//获取设备信息
		System.out.println("设备序列号: " + device.getDeviceId());
		int[] resolution = device.getScreenResolution();
		System.out.println("设备屏幕分辨率: " + resolution[0] + "x" + resolution[1]);
		System.out.println("设备Android版本: " + device.getAndroidVersion());
		System.out.println("设备SDK版本: " + device.getSdkVersion());
		System.out.println("设备电池状态： " + device.getBatteryStatus());
		System.out.println("设备电池温度： " + device.getBatteryTemp());
		System.out.println("设备电池电量： " + device.getBatteryLevel());
		
		//安装d:\app\ApiDemos.apk
		device.installApp("d:\\app\\ApiDemos.apk");
		//启动ApiDemos
		device.startActivity("com.example.android.apis/.ApiDemos");
		Thread.sleep(2000);
		
		//获取获取当前应用的包名、类名、包名/类名
		System.out.println("package name: " + device.getCurrentPackageName());
		System.out.println("activite: " + device.getCurrentActivity());
		String component = device.getFocusedPackageAndActivity();
		System.out.println("package name/activity: " + component);
		
		//获取当前应用的pid
		System.out.println("PID: " + device.getPid(device.getCurrentPackageName()));
		//退出当前应用
		device.quitCurrentApp();
		Thread.sleep(1000);
		
		//重新启动应用
		device.startActivity(component);
		Thread.sleep(2000);
		
		//通过元素的text属性定位，点击App>>Dialog>>Single choice list>>Traffic>>OK
		Position position = new Position();
		Element e_text = position.findElementByText("App");
		device.tap(e_text);
		Thread.sleep(1000);
		
		Element e_dialog = position.findElementByText("Dialog");
		device.tap(e_dialog);
		Thread.sleep(1000);
		
		Element e_single = position.findElementByText("Single choice list");
		device.tap(e_single);
		Thread.sleep(1000);
		
		//通过元素的class定位：Traffic>>OK
		ArrayList<Element> textViews = position.findElementsByClass("android.widget.CheckedTextView");
		device.tap(textViews.get(2));
		Thread.sleep(1000);
		
		Element e_ok = position.findElementsByClass("android.widget.Button").get(1);
		device.tap(e_ok);
		Thread.sleep(1000);
		
		//遍历的点击当前界面所有的button
		ArrayList<Element> buttons = position.findElementsByClass("android.widget.Button");
		for (Element element : buttons) {
			device.tap(element);
			Thread.sleep(1000);
			device.sendKeyEvent(AndroidKeyCode.BACK);
		}
		
		//点击Text Entry dialog
		device.tap(buttons.get(buttons.size() - 1));
		Thread.sleep(1000);
		
		//需要英文键盘
	    //输入Name：test, Password: TEST, 点击OK
		ArrayList<Element> edits = position.findElementsByClass("android.widget.EditText");
		device.tap(edits.get(0));
		device.sendText("test");
		
		device.tap(edits.get(1));
		device.sendText("TEST");
		
		device.tap(position.findElementByText("OK"));
		
	}
}
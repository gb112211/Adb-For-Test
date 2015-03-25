package xuxu.autotest.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {
	
	public static String getCurrentSystemTime() {
		//设置日期格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		// new Date()为获取当前系统时间
		String time = dateFormat.format(new Date());
		
		return time;
	}
	
	public static String getCalendar() {
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1; 
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String time = year + "-" + month + "-" + day + "-" + hour + "-"
				+ minute + "-" + second;
		
		return time;	
	}
}

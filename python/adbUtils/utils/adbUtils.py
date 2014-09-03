#!/usr/bin/env python
#coding=utf-8

import os
import platform
import re
import keycode


PATH = lambda p: os.path.abspath(p)

#判断系统类型，windows使用findstr，linux使用grep
system = platform.system()
if system is "Windows":
    find_util = "findstr"
else:
    find_util = "grep"

#判断是否设置环境变量ANDROID_HOME
if "ANDROID_HOME" in os.environ:
    if system == "Windows":
        command = os.path.join(os.environ["ANDROID_HOME"], "platform-tools", "adb.exe")
    else:
        command = os.path.join(os.environ["ANDROID_HOME"], "platform-tools", "adb")
else:
    raise EnvironmentError(
        "Adb not found in $ANDROID_HOME path: %s." % os.environ["ANDROID_HOME"])

#adb命令
def adb(args):
    return os.popen(command + " " + str(args))
#adb shell命令
def shell(args):
    return os.popen(command + " shell " + str(args))


class ADB(object):

    def __init__(self):
        """
        等待连接上设备或模拟器
        """
        adb("wait-for-device")

    def getDeviceState(self):
        """
        获取设备状态： offline | bootloader | device
        """
        return adb("get-state").read().split("\n")[0]

    def getDeviceID(self):
        """
        获取设备id号，return serialNo
        """
        return adb("get-serialno").read().split("\n")[0]

    def getPid(self, packageName):
        """
        获取进程pid
        args:
        - packageName -: 应用包名
        usage: getPid("com.android.settings")
        """
        if system is "Windows":
            string = shell("ps | findstr " + packageName + "$").read()
        
        string = shell("ps | grep -w " + packageName).read()

        if string == '':
            return "the process doesn't exist."

        pattern = re.compile(r"\d+")
        result = string.split(" ")
        result.remove(result[0])

        return  pattern.findall(" ".join(result))[0]

    def killProcess(self, pid):
        """
        杀死应用进程
        args:
        - pid -: 进程pid值
        usage: killProcess(154)
        注：杀死系统应用进程需要root权限
        """
        if shell("kill " + str(pid)).read().split(": ")[-1] == "":
            return "kill success"
        else:
            return shell("kill " + str(pid)).read().split(": ")[-1]

    def quitApp(self, packageName):
        """
        退出app，类似于kill掉进程
        usage: quitApp("com.android.settings")
        """
        shell("am force-stop " + packageName)

    def getFocusedPackageAndActivity(self):
        """
        获取当前应用界面的包名和Activity，返回的字符串格式为：packageName/activityName
        """
        pattern = re.compile(r"[a-zA-Z0-9\.]+/.[a-zA-Z0-9\.]+")
        out = shell("dumpsys window w | " + find_util + " \/ | " + find_util + " name=").read()

        return pattern.findall(out)[0]

    def getCurrentPackageName(self):
        """
        获取当前运行的应用的包名
        """
        return self.getFocusedPackageAndActivity().split("/")[0]

    def getCurrentActivity(self):
        """
        获取当前运行应用的activity
        """
        return self.getFocusedPackageAndActivity().split("/")[-1]
    def getBatteryLevel(self):
        """
        获取电池电量
        """
        level = shell("dumpsys battery | " + find_util + " level").read().split(": ")[-1]

        return int(level)

    def getBatteryStatus(self):
        """
        获取电池充电状态
        BATTERY_STATUS_UNKNOWN：未知状态
        BATTERY_STATUS_CHARGING: 充电状态
        BATTERY_STATUS_DISCHARGING: 放电状态
        BATTERY_STATUS_NOT_CHARGING：未充电
        BATTERY_STATUS_FULL: 充电已满
        """
        statusDict = {1 : "BATTERY_STATUS_UNKNOWN",
                      2 : "BATTERY_STATUS_CHARGING",
                      3 : "BATTERY_STATUS_DISCHARGING",
                      4 : "BATTERY_STATUS_NOT_CHARGING",
                      5 : "BATTERY_STATUS_FULL"}
        status = shell("dumpsys battery | " + find_util + " status").read().split(": ")[-1]

        return statusDict[int(status)]

    def getBatteryTemp(self):
        """
        获取电池温度
        """
        temp = shell("dumpsys battery | " + find_util + " temperature").read().split(": ")[-1]

        return int(temp) / 10.0

    def getScreenResolution(self):
        """
        获取设备屏幕分辨率，return (width, high)
        """
        pattern = re.compile(r"\d+")
        out = shell("dumpsys display | " + find_util + " PhysicalDisplayInfo").read()
        display = pattern.findall(out)

        return (int(display[0]), int(display[1]))

    def reboot(self):
        """
        重启设备
        """
        adb("reboot")

    def fastboot(self):
        """
        进入fastboot模式
        """
        adb("reboot bootloader")

class AppInfo(object):
    """
    获取部分app信息
    """
    def __init__(self):
        pass

    def getSystemAppList(self):
        """
        获取设备中安装的系统应用包名列表
        """
        sysApp = []
        for packages in shell("pm list packages -s").readlines():
            sysApp.append(packages.split(":")[-1].splitlines()[0])

        return sysApp

    def getThirdAppList(self):
        """
        获取设备中安装的第三方应用包名列表
        """
        thirdApp = []
        for packages in shell("pm list packages -3").readlines():
            thirdApp.append(packages.split(":")[-1].splitlines()[0])

        return thirdApp

    def getMatchingAppList(self, keyword):
        """
        模糊查询与keyword匹配的应用包名列表
        usage: getMatchingAppList("qq")
        """
        matApp = []
        for packages in shell("pm list packages " + keyword).readlines():
            matApp.append(packages.split(":")[-1].splitlines()[0])

        return matApp

    def getAppStartTotalTime(self, component):
        """
        获取启动应用所花时间
        usage: getAppStartTotalTime("com.android.settings/.Settings")
        """
        time = shell("am start -W " + component + " | " + find_util +" TotalTime") \
            .read().split(": ")[-1]
        return int(time)

    def installApp(self, appFile):
        """
        安装app，app名字不能含中文字符
        args:
        - appFile -: app路径
        usage: install("d:\\apps\\Weico.apk")
        """
        adb("install " + appFile)

    def isInstall(self, packageName):
        """
        判断应用是否安装，已安装返回True，否则返回False
        usage: isInstall("com.example.apidemo")
        """
        if self.getMatchingAppList(packageName):
            return True
        else:
            return False

    def removeApp(self, packageName):
        """
        卸载应用
        args:
        - packageName -:应用包名，非apk名
        """
        adb("uninstall " + packageName)

    def clearAppData(self, packageName):
        """
        清除应用用户数据
        usage: clearAppData("com.android.contacts")
        """
        if "Success" in shell("pm clear " + packageName).read().splitlines():
            return "clear user data success "
        else:
            return "make sure package exist"

    def resetCurrentApp(self):
        """
        重置当前应用
        """
        packageName = ADB().getCurrentPackageName()
        component = ADB().getFocusedPackageAndActivity()
        self.clearAppData(packageName)
        Action().startActivity(component)


class Action(object):
    def __init__(self):
        self.display = ADB().getScreenResolution()
        self.width = self.display[0]
        self.high = self.display[1]

    def startActivity(self, component):
        """
        启动一个Activity
        usage: startActivity(component = "com.android.settinrs/.Settings")
        """
        shell("am start -n " + component)

    def startWebpage(self, url):
        """
        使用系统默认浏览器打开一个网页
        usage: startWebpage("http://www.baidu.com")
        """
        shell("am start -a android.intent.action.VIEW -d " + url)

    def callPhone(self, number):
        """
        启动拨号器拨打电话
        usage: callPhone(10086)
        """
        shell("am start -a android.intent.action.CALL -d tel:" + str(number))

    def sendKeyEvent(self, keycode):
        """
        发送一个按键事件
        args:
        - keycode -:
        http://developer.android.com/reference/android/view/KeyEvent.html
        usage: sendKeyEvent(keycode.HOME)
        """
        shell("input keyevent " + str(keycode))

    def longPressKey(self, keycode):
        """
        发送一个按键长按事件，Android 4.4以上
        usage: longPressKey(keycode.HOME)
        """
        shell("input keyevent --longpress " + str(keycode))

    def touch(self, e=None, x=None, y=None):
        """
        触摸事件
        usage: touch(e), touch(x=0.5,y=0.5)
        """
        if(e != None):
            x = e[0]
            y = e[1]
        if(0< x < 1):
            x = x * self.width
        if(0<y<1):
            y = y * self.high

        shell("input tap " + str(x) + " " + str(y))

    def touchByElement(self, element):
        """
        点击元素
        usage: touchByElement(Element().findElementByName(u"计算器"))
        """
        shell("input tap " + str(element[0]) + " " + str(element[1]))

    def touchByRatio(self, ratioWidth, ratioHigh):
        """
        通过比例发送触摸事件
        args:
        - ratioWidth -:width占比, 0<ratioWidth<1
        - ratioHigh -: high占比, 0<ratioHigh<1
        usage: touchByRatio(0.5, 0.5) 点击屏幕中心位置
        """
        shell("input tap "+ str(ratioWidth * self.width) + " " + str(ratioHigh * self.high))


    def swipeByCoord(self, start_x, start_y, end_x, end_y, duration = " "):
        """
        滑动事件，Android 4.4以上可选duration(ms)
        usage: swipe(800, 500, 200, 500)
        """
        shell("input swipe " + str(start_x) + " " + str(start_y) + \
              " " + str(end_x) + " " + str(end_y) + " " + str(duration))

    def swipe(self, e1=None, e2=None, start_x=None, start_y=None, end_x=None, end_y=None, duration=" "):
        """
        滑动事件，Android 4.4以上可选duration(ms)
        usage: swipe(e1, e2)
               swipe(e1, end_x=200, end_y=500)
               swipe(start_x=0.5, start_y=0.5, e2)
        """
        if(e1 != None):
            start_x = e1[0]
            start_y = e1[1]
        if(e2 != None):
            end_x = e2[0]
            end_y = e2[1]
        if(0< start_x < 1):
            start_x = start_x * self.width
        if(0<start_y<1):
            start_y = start_y * self.high
        if(0<end_x<1):
            end_x = end_x * self.width
        if(0<end_y<1):
            end_y = end_y * self.high

        shell("input swipe " + str(start_x) + " " + str(start_y) + \
              " " + str(end_x) + " " + str(end_y) + " " + str(duration))

    def swipeByRatio(self, start_ratioWidth, start_ratioHigh, end_ratioWidth, end_ratioHigh, duration = " "):
        """
        通过比例发送滑动事件，Android 4.4以上可选duration(ms)
        usage: swipeByRatio(0.9, 0.5, 0.1, 0.5) 左滑
        """
        shell("input swipe " + str(start_ratioWidth * self.width) + " " + str(start_ratioHigh * self.high) + \
              " " + str(end_ratioWidth * self.width) + " " + str(end_ratioHigh * self.high) + " " +\
             str(duration))

    def swipeToLeft(self):
        """
        左滑屏幕
        """
        self.swipeByRatio(0.8, 0.5, 0.2, 0.5)

    def swipeToRight(self):
        """
        右滑屏幕
        """
        self.swipeByRatio(0.2, 0.5, 0.8, 0.5)

    def swipeToUp(self):
        """
        上滑屏幕
        """
        self.swipeByRatio(0.5, 0.8, 0.5, 0.2)

    def swipeToDown(self):
        """
        下滑屏幕
        """
        self.swipeByRatio(0.5, 0.2, 0.5, 0.8)

    def longPress(self, e=None, x=None, y=None):
        """
        长按屏幕的某个坐标位置, Android 4.4
        usage: longPress(e)
               longPress(x=0.5, y=0.5)
        """
        self.swipe(e1=e, e2=e, start_x=x, start_y=y, end_x=x, end_y=y, duration=2000)

    def longPressElement(self, e):
        """
       长按元素, Android 4.4
        """
        shell("input swipe " + str(e[0]) + " " + str(e[1]) + " "  + str(e[0]) + " " + str(e[1]) + str(2000))

    def longPressByRatio(self, ratioWidth, ratioHigh):
        """
        通过比例长按屏幕某个位置, Android.4.4
        usage: longPressByRatio(0.5, 0.5) 长按屏幕中心位置
        """
        self.swipeByRatio(ratioWidth, ratioHigh, ratioWidth, ratioHigh, duration=2000)

    def sendText(self, string):
        """
        发送一段文本，只能包含英文字符和空格，多个空格视为一个空格
        usage: sendText("i am unique")
        """
        text = str(string).split(" ")
        out = []
        for i in text:
            if i != "":
                out.append(i)
        length = len(out)
        for i in xrange(length):
            shell("input text " + out[i])
            if i != length - 1:
                self.sendKeyEvent(keycode.SPACE)

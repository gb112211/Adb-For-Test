#!/usr/bin/env python
#coding=utf-8

from adbUtils.utils.adbUtils import ADB, AppInfo, Action;
from adbUtils.utils.element import Element
from adbUtils.utils import keycode

import os
import time

#demo为ApiDemos.apk,使用的真机平台为android 4.3

PATH = lambda p : os.path.abspath(p)

def test():
    #获取adb连接
    adb = ADB()

    app = AppInfo()
    action = Action()

    #获取设备信息
    print "设备序列号: " + adb.getDeviceID()
    print "设备状态： " + adb.getDeviceState()
    print "设备屏幕分辨率: " + str(adb.getScreenResolution())
    print "设备电池状态： " + adb.getBatteryStatus()
    print "设备电池温度： " + str(adb.getBatteryTemp())
    print "设备电池电量： " + str(adb.getBatteryLevel())

    #安装当前目录下的ApiDemos.apk
    if(not app.isInstall("com.example.android.apis")):
        #应用不存在则安装
        app.installApp(PATH(os.getcwd() + "/app/ApiDemos.apk"))
    else:
        #存在则先卸载，后安装
        app.removeApp("com.example.android.apis")
        app.installApp(PATH(os.getcwd() + "/app/ApiDemos.apk"))
        
    action.startActivity("com.example.android.apis/.ApiDemos")
    time.sleep(2)

    #获取当前应用的包名、类名、包名/类名
    print adb.getCurrentPackageName()
    print adb.getCurrentActivity()
    component = adb.getFocusedPackageAndActivity()
    print component

    #获取当前应用的pid
    print adb.getCurrentPackageName() + "-pid: " + str(adb.getPid(adb.getCurrentPackageName()))
    #结束当前应用
    adb.quitApp(adb.getCurrentPackageName())

    #重新启动
    action.startActivity(component)
    #延时2s
    time.sleep(2)
    #通过元素的text属性定位，点击App>>Dialog>>Single choice list>>Traffic>>OK
    element = Element()
    
    e_app = element.findElementByName("App")
    action.touchByElement(e_app)
    time.sleep(1)

    e_dialog = element.findElementByName("Dialog")
    action.touchByElement(e_dialog)
    time.sleep(1)

    e_single = element.findElementByName("Single choice list")
    action.touchByElement(e_single)
    time.sleep(1)

    #通过元素的class定位：Traffic>>OK
    textViews = element.findElementsByClass("android.widget.CheckedTextView")
    action.touchByElement(textViews[2])
    time.sleep(1)

    e_ok = element.findElementsByClass("android.widget.Button")[1]
    action.touchByElement(e_ok)
    time.sleep(1)

    #遍历的点击当前界面所有的button
    buttons = element.findElementsByClass("android.widget.Button")
    for button in buttons:
        action.touchByElement(button)
        time.sleep(1)
        action.sendKeyEvent(keycode.BACK)

    #点击Text Entry dialog
    action.touchByElement(buttons[-1])
    time.sleep(1)
    #需要英文键盘
    #输入Name：test, Password: TEST, 点击OK
    edits = element.findElementsByClass("android.widget.EditText")
    action.touchByElement(edits[0])
    action.sendText("test")

    action.touchByElement(edits[-1])
    action.sendText("TEST")

    action.touchByElement(element.findElementByName("OK"))

if __name__ == "__main__":
    test()

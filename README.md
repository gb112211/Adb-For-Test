Adb-For-Test
============

通过adb命令实现简单的点击、滑动、长按等效果，可通过比例定位元素、通过元素定位（需要Android版本高于4.0），拥有monkeyrunner的绝大部分功能 

###为何写这么个东西？

1.目前所做的工作是对rom的测试，所以基本上所有的测试环境都集中在Android4.2、4.3、4.4平台，平台比较固定
2.Android测试中最为熟悉的就是adb命令，很多功能都可通过adb命令去实现，因此几乎可以使用任何的编程语言去编写脚本调用adb命令，去实现对设备的操作
3.将日常工作中封装的一些由adb命令完成的功能整合在一起，方便于使用
4.作为自己学习java、python的一种实践

###优点

1.分别采用两种语言（java、python）实现，需要的环境都极其的简单，不需要root
2.基本上都以adb命令完成功能，可从代码中剥离部分代码形成日常用的脚本（参考AndroidTestScripts项目）
3.由于是通过adb命令去发送事件，所以可跨应用进行测试，可用来扩展其他Android测试框架
4.通过设备分辨率与坐标转换，可以方便的以屏幕比例作为参数来操作设备
5.调用了uiautomator命令，Android4.0以上，可通过界面元素进行定位，可获取界面内单个元素区域的坐标范围，以此截取图片（区别于截取整个屏幕）
6.可采用截图对比全图、部分区域图片、获取当前界面某个元素、获取当前界面的Activity等各种属性，对测试结果进行判断

###缺点

1.部分功能需要较高的Android版本，低版本的未去验证，所以局限性很大，纯当练手
2.初学java、python，代码写得比较烂
3.很多地方未做异常处理
4.有疑问有吐槽请联系：Email: xuxu_1988@126.com  QQ:274925460

###需要的环境

环境变量：需配置ANDROID_HOME
python：python2.7，如果用到截图对比功能，需要安装PIL库，PIL无法安装时可用Pillow [http://www.pythonware.com/products/pil/index.htm](http://www.pythonware.com/products/pil/index.htm "PIL地址")
java: jdk1.6以上，不需要其他任何第三方jar包

###如何使用

python：使用setup.py install 安装，未去强制判断要求安装PIL，使用ImageUtils时则需要安装
java：导入Adb-For-Test.jar即可

###简单例子

在TestDemo目录下分别有python、java的例子，使用的apk是ApiDemos.apk.

这里使用python写一个截取桌面应用图标的例子：
```
#coding=utf-8

from adbUtils.utils.adbUtils import ADB
from adbUtils.utils.imageUtils import ImageUtils
from adbUtils.utils.element import Element

adb = ADB()
element = Element()

#获取图片区域
icon = element.getElementBoundsById("android.widget.TextView")[0]
image = ImageUtils()
#截取图片
image.screebShot().subImage(icon).writeToFile(os.getcwd(), "image")

#加载需要对比的目标图片
#load = image.loadImage(os.getcwd() + "\\image.png")
#print image.screenShot().subImage(icon).sameAs(load)
```
#!/usr/bin/env python
#coding=utf-8

#图片处理，需要PIL库

import tempfile
import subprocess
import os
import shutil
from PIL import Image

PATH = lambda p: os.path.abspath(p)

class ImageUtils(object):

    def __init__(self):
        """
        初始化，获取系统临时文件存放目录
        """
        self.tempFile = tempfile.gettempdir()

    def screenShot(self):
        """
        截取设备屏幕
        """
        subprocess.call("adb shell screencap -p /data/local/tmp/temp.png")
        os.popen("adb pull /data/local/tmp/iuniTemp.png " + self.tempFile)

        return self

    def writeToFile(self, dirPath, imageName, form = "png"):
        """
        将截屏文件写到本地
        usage: screenShot().writeToFile("d:\\screen", "image")
        """
        if not os.path.isdir(dirPath):
            os.makedirs(dirPath)
        shutil.copyfile(PATH(self.tempFile + "/temp.png"), PATH(dirPath + "/" + imageName + "." + form))
        os.popen("adb shell rm /data/local/tmp/temp.png")

    def loadImage(self, imageName):
        """
        加载本地图片
        usage: lodImage("d:\\screen\\image.png")
        """
        if os.path.isfile(imageName):
            load = Image.open(imageName)
            return load
        else:
            print "image is not exist"

    def subImage(self, box):
        """
        截取指定像素区域的图片
        usage: box = (100, 100, 600, 600)
              screenShot().subImage(box)
        """
        image = Image.open(PATH(self.tempFile + "/temp.png"))
        newImage = image.crop(box)
        newImage.save(PATH(self.tempFile + "/temp.png"))

        return self

    #http://testerhome.com/topics/202
    def sameAs(self,loadImage):
        """
        比较两张截图的相似度，完全相似返回True
        usage： load = loadImage("d:\\screen\\image.png")
                screen().subImage(100, 100, 400, 400).sameAs(load)
        """
        import math
        import operator

        image1 = Image.open(PATH(self.tempFile + "/temp.png"))
        image2 = loadImage


        histogram1 = image1.histogram()
        histogram2 = image2.histogram()

        differ = math.sqrt(reduce(operator.add, list(map(lambda a,b: (a-b)**2, \
                                                         histogram1, histogram2)))/len(histogram1))
        if differ == 0:
            return True
        else:
            return False

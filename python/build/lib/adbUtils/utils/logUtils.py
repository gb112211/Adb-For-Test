#!/usr/bin/env python

import os
import time

PATH = lambda p: os.path.abspath(p)

class Log(object):

    def __init__(self, logPath, fileName):
        self.path = logPath
        self.fileName = fileName

        if not os.path.isdir(self.path):
            os.makedirs(self.path)

        self.logFile = file(PATH(self.path + "/" + self.fileName), "a")

    def info(self, info):

        timestamp = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        self.logFile.write("INFO : " + timestamp + " " +str(info) + "\n")
        self.logFile.flush()

    def debug(self,debugInfo):

        timestamp = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        self.logFile.write("DEBUG: " + timestamp + " " +str(debugInfo) + "\n")
        self.logFile.flush()

    def error(self, errorInfo):

        timestamp = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        self.logFile.write("ERROR: " + timestamp + " " +str(errorInfo) + "\n")
        self.logFile.flush()

    def close(self):
        self.logFile.close()

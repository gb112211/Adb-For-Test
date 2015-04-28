#!/usr/bin/env python

import os
import time

__author__ = "xuxu"

PATH = lambda p: os.path.abspath(p)

class Log(object):

    def __init__(self, logPath, fileName):
        self.path = logPath
        self.fileName = fileName

        if not os.path.isdir(self.path):
            os.makedirs(self.path)

        self.logFile = file(PATH("%s/%s" %(self.pathself.fileName)), "a")

    def info(self, info):

        timestamp = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        self.logFile.write("INFO : %s %s\n" %(timestamp, str(info)))
        self.logFile.flush()

    def debug(self,debugInfo):

        timestamp = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        self.logFile.write("DEBUG: %s %s\n" %(timestamp, str(debugInfo)))
        self.logFile.flush()

    def error(self, errorInfo):

        timestamp = time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        self.logFile.write("ERROR: %s %s\n" %(timestamp, str(errorInfo)))
        self.logFile.flush()

    def close(self):
        self.logFile.close()

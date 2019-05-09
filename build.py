# -*- coding: utf-8 -*-
"""
Created on Mon Apr 22 19:10:45 2019

@author: Sonic
"""

import os
import re
import sys
import platform

sysType = platform.system()
curPath = os.getcwd()
pathLib = os.path.join(curPath, "lib")
pathBin = os.path.join(curPath, "bin")
pathSrc = os.path.join(curPath, "src")
pathPath = os.path.join(curPath, "path")
suffix = ".+\.java"
print("the project is being builded...\n")
print("The current directory : " + curPath + "\n")


def clear():
    classList = os.listdir(pathBin)
    for class_ in classList:
        if re.match(".+\.class", class_):
            pathTmp = os.path.join(pathBin, class_)
            print("Delete: " + pathTmp)
            os.remove(pathTmp)

def findPattern(path, pattern):
    fp = open(path, "r", encoding = 'utf-8')
    strr = fp.read()
    if (re.search(pattern, strr)):
        print("\nThe main function entry is here\n-> " + path + "\n")
        return (True, path)
    else :
        return (False, "")
    fp.close()


def register(path, filename, suffix): 
    #获取指定目录下的子目录和文件名称
    print("Register source files to path\\srclist.txt...\n")
    srclist = open(filename, 'w')
    for root,dirs,files in os.walk(path,topdown=True):
        for name in files:
            if re.match(suffix, name):
                srclist.write(os.path.join(root,name) + "\n")
                print(os.path.join(root,name)[len(path):] + " is registered")
    srclist.close()
    
def findMain(system):
    for root,dirs,files in os.walk(pathSrc,topdown=True):
        for name in files:
            if re.match(suffix, name):
                tmp = findPattern(os.path.join(root,name), " main\(.*\)")
                if tmp[0]:
                    s = tmp[1][len(pathSrc) + 1:]
                    print("debug: " + pathSrc)
                    if system == "Windows":
                    	s = s.replace("\\", ".")
                    elif system == "Linux":
                    	s = s.replace("/", ".")
                    return s[0 : len(s) - 5]
                    


def getLib(system):
    jars = ""
    if system == "Windows":
        splitter = ";"
    elif system == "Linux":
        splitter = ":"
    for root,dirs,files in os.walk(pathLib,topdown=True):
        for name in files:
            jars = jars + splitter + os.path.join(root,name)
    return jars[1:]
            
def writeMANIFEST():
    mf = open("MANIFEST.MF", "w")
    mf.write("Manifest-Version: 1.0\n")
    mf.write("Main-Class: " + findMain(sysType) + "\n")
    jars = "Class-Path: ."
    for root,dirs,files in os.walk(pathLib,topdown=True):
        for name in files:
            jars = jars + " " + os.path.join("lib", name)
    jars = jars + " bin"
    mf.write(jars + "\n")
    mf.write("\n")
    mf.close()      

def rmrf(path):
    if (os.path.exists(path)):
       for root, dirs, files in os.walk(path, topdown=False):
           for name in files:
               os.remove(os.path.join(root, name))
           for name in dirs:
               os.rmdir(os.path.join(root, name))
       os.rmdir(path)


rmrf(pathPath)
rmrf(pathBin)
os.mkdir(pathBin)
os.mkdir(pathPath)
register(pathSrc, os.path.join("path", "srclist.txt"), ".+\.java")
jars = getLib(sysType)
#print("jars : " + jars)
writeMANIFEST()
if sysType == "Windows":
	splitter = " & "
elif sysType == "Linux":
	splitter = " ; "

cmd = "@javac -encoding UTF-8 -cp " + jars + " @{} -d bin".format(os.path.join("path", "srclist.txt")) + splitter + "@jar -cvfm {}.jar MANIFEST.MF -C bin/ .".format(sys.argv[1])
os.system(cmd)
rmrf(pathPath)
rmrf(pathBin)
os.remove(os.path.join(curPath, "MANIFEST.MF"))




# -*- coding: utf-8 -*-
"""
Created on Mon Apr 22 19:10:45 2019

@author: Sonic
"""

import os
import re
import sys

suffix = ".+\.java"
print("the project is being builded...\n")
print("The current directory : " + os.getcwd() + "\n")

def clear():
    classList = os.listdir(os.getcwd() + "\\bin\\")
    for class_ in classList:
        if re.match(".+\.class", class_):
            path = os.getcwd() + "\\bin\\" + class_
            print("Delete: " + path)
            os.remove(path)

def findPattern(path, pattern):
    fp = open(path, "r")
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
    
def findMain():
    path = os.getcwd() + "\\src\\"
    for root,dirs,files in os.walk(path,topdown=True):
        for name in files:
            if re.match(suffix, name):
                tmp = findPattern(os.path.join(root,name), " main\(.*\)")
                if tmp[0]:
                    s = tmp[1][len(path):]
                    s = s.replace("\\", ".")
                    return s[0 : len(s) - 5]
                    


def getLib(system):
    jars = ""
    pathLib = os.getcwd() + "\\lib\\"
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
    mf.write("Main-Class: " + findMain() + "\n")
    pathLib = os.getcwd() + "\\lib\\"
    jars = "Class-Path: ."
    for root,dirs,files in os.walk(pathLib,topdown=True):
        for name in files:
            jars = jars + " " + "lib\\" + name
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


rmrf(os.getcwd() + "\\path")
rmrf(os.getcwd() + "\\bin")
os.mkdir(os.getcwd() + "\\bin")
os.mkdir(os.getcwd() + "\\path")
register(os.getcwd() + "\\src\\", "path\\srclist.txt", ".+\.java")
jars = getLib("Windows")
#print("jars : " + jars)
writeMANIFEST()
cmd = "@javac -encoding UTF-8 -cp " + jars + " @path\\srclist.txt -d bin" + " & " + "@jar -cvfm {}.jar MANIFEST.MF -C bin/ .".format(sys.argv[1])
os.system(cmd)
rmrf(os.getcwd() + "\\path")
rmrf(os.getcwd() + "\\bin")
os.remove(os.getcwd() + "\\MANIFEST.MF")




@echo off

mode con: cols=150 lines=9999
CLS
cmd /k java -jar PackageJsonParser.jar -paths c:\src\BK25_Workspace\Packages\TCI c:\src\BK25_Workspace\Packages\KWI -duration 30 -unit minutes

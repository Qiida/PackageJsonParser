@echo off

mode con: cols=140 lines=35
cmd /k java -jar PackageJsonParser.jar -recursive -batch 10 -in C:\src\BK25_Workspace\Packages\TCI\DD_TSP\_TSI_overall\R23-09 C:\src\BK25_Workspace\Packages\TCI\DD_TSP\_TSI_overall\R24-09 -out C:\src\PackageJsonParser\out\artifacts\PackageJsonParser_jar\out
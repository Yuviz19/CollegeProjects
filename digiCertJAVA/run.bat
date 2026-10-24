@echo off
cd /d %~dp0
javac -cp "lib/*" -d bin src\model\*.java src\db\*.java src\ui\*.java src\util\*.java src\Main.java
if errorlevel 1 exit /b 1
echo Compiled OK. Starting app...
java -cp "bin;lib/*" Main

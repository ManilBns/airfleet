@echo off
cd /d %~dp0

REM Lancer l'application Java avec JNI et MySQL
java --enable-native-access=ALL-UNNAMED -Djava.library.path=bin -cp "bin;AirFleet.jar;C:\Users\ASUS\git\repository\AirFleet\lib\mysql-connector-j-9.5.0.jar" app.Main

pause

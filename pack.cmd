@echo off

del builds\IC2NuclearControl-dev.zip
del builds\IC2NuclearControl-server-dev.zip
"C:\Program Files\7-Zip\7z" a builds\IC2NuclearControl-dev.zip ..\mcp\reobf\minecraft\*  %~dp0client\img %~dp0client\sound
"C:\Program Files\7-Zip\7z" a builds\IC2NuclearControl-server-dev.zip ..\mcp\reobf\minecraft_server\* 

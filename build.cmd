@echo off
xcopy /Q /S /Y client\*  ..\mcp\src\minecraft\
xcopy /Q /S /Y common\*  ..\mcp\src\minecraft\
xcopy /Q /S /Y server\*  ..\mcp\src\minecraft_server\
xcopy /Q /S /Y common\*  ..\mcp\minecraft_server\

cd ..\mcp
runtime\bin\python\python_mcp runtime\recompile.py && runtime\bin\python\python_mcp runtime\reobfuscate.py
cd %~dp0
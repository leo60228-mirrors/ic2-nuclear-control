@echo off

xcopy /Q /S /Y builds\IC2NuclearControl-dev.zip  ..\mc\.minecraft\mods\
xcopy /Q /S /Y builds\IC2NuclearControl-server-dev.zip  ..\mc\server\minecraft\mods\

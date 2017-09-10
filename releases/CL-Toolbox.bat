set DIR=%~dp0
IF "%DIR:~-1%"=="\" set DIR=%DIR:~0,-1%
java -Dfile.encoding="UTF-8" -jar "%DIR%\CL-Toolbox.jar" %*
ECHO OFF
REM File: toffi.cmd
REM Script requires java.exe to be already provided in the command prompt

SET MY_PATH=%~dp0
SET LIB_PATH=%MY_PATH%..\..\lib\toffi
SET LOG4J_XML=%MY_PATH%..\log4j2.xml

java --module-path "%LIB_PATH%" ^
  -Dlog4j.configurationFile=%LOG4J_XML% ^
  --add-modules org.goffi.toffi,java.sql ^
  -m org.goffi.toffi/org.goffi.toffi.App %*

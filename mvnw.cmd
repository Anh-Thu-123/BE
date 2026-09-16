@echo off
setlocal
set DIR=%~dp0
set MVN_VERSION=3.9.9
set MVN_HOME=%DIR%.mvn\dist\apache-maven-%MVN_VERSION%

if not exist "%MVN_HOME%\bin\mvn.cmd" (
  echo Local Maven not found, downloading apache-maven-%MVN_VERSION% ...
  if not exist "%DIR%.mvn\dist" mkdir "%DIR%.mvn\dist"
  powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VERSION%/apache-maven-%MVN_VERSION%-bin.zip' -OutFile '%DIR%.mvn\dist\maven.zip'"
  powershell -NoProfile -Command "Expand-Archive -Path '%DIR%.mvn\dist\maven.zip' -DestinationPath '%DIR%.mvn\dist' -Force"
  del "%DIR%.mvn\dist\maven.zip"
)

rem Goi thang classworlds launcher (khong qua mvn.cmd) - script mvn.cmd goc bi loi tren mot so may Windows
rem khi JAVA_HOME chua khoang trang; goi truc tiep on dinh hon va cung tranh duoc loi doc classpath
rem chua ky tu Unicode trong duong dan du an (xem README - muc "Ghi chu build tren Windows").
java "-Dclassworlds.conf=%MVN_HOME%\bin\m2.conf" "-Dmaven.home=%MVN_HOME%" "-Dmaven.multiModuleProjectDirectory=%DIR%" -classpath "%MVN_HOME%\boot\plexus-classworlds-2.8.0.jar" org.codehaus.plexus.classworlds.launcher.Launcher %*

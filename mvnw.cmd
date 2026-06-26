@echo off
@REM Maven wrapper for Scholar Flow Backend

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9-bin\33b4b2b4\apache-maven-3.9.9"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo ERROR: Maven not found at %MAVEN_HOME%
    echo Please run: java -jar .mvn\wrapper\maven-wrapper.jar
    exit /b 1
)

"%MAVEN_HOME%\bin\mvn.cmd" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" %*

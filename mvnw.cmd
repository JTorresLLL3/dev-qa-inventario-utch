@echo off
setlocal

set "MAVEN_BIN=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.16\56ba1f9f\bin\mvn.cmd"
if exist "%MAVEN_BIN%" (
    call "%MAVEN_BIN%" %*
    exit /b %ERRORLEVEL%
)

for /f "delims=" %%i in ('dir /b /s "%USERPROFILE%\.m2\wrapper\dists\mvn.cmd" 2^>nul') do (
    call "%%i" %*
    exit /b %ERRORLEVEL%
)

echo Maven Wrapper no encontro mvn.cmd en %USERPROFILE%\.m2\wrapper\dists
exit /b 1

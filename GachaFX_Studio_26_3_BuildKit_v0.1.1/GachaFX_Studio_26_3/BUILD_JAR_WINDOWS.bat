@echo off
cd /d "%~dp0"
echo Java 25 required. Gradle 9.6 required.
java -version
gradle --version
if errorlevel 1 (echo Gradle not installed. Use GitHub Actions instead.& pause & exit /b 1)
gradle build --stacktrace
if errorlevel 1 (echo Build failed. Copy error log for debugging.& pause & exit /b 1)
echo Build finished. Check build\libs for the remapped mod JAR.
pause

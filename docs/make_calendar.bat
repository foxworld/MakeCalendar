@echo off
rem ?? ??? ?? (EUC-KR / CP949)
chcp 949 > nul

rem ?? ?? ??
set JAVA_BIN=C:\java\java-1.8.0\bin\java.exe
set HOME_PATH=C:\pgims\pginfo\bin
set JAR_BIN=MakeCalendar-0.0.1.jar

rem ?? ?? ?? (YYYYMMDD ??)
set TODAY=%date:~0,4%%date:~5,2%%date:~8,2%

rem ??? ?? ?? ???? ?? ?? ??
if "%~1"=="" goto usage
if "%~2"=="" goto usage
if "%~1"=="-h" goto usage
if "%~1"=="--help" goto usage

rem ?? ???? ?? ??
set YEAR=%~1
set COUNTRY_CODE=%~2

rem 3?? ????(ONLY_HOLIDAY) ??? ??
rem ?? ???? ???? ??? false
set ONLY_HOLIDAY=false

rem 3?? ????? ??? ?? (???? ?? ?? ??)
if /i "%~3"=="ONLY-HOLIDAY" set ONLY_HOLIDAY=true
if /i "%~3"=="true" set ONLY_HOLIDAY=true

echo.
echo ^>^> %YEAR%?? [%COUNTRY_CODE%] ?? ?? ????? ?????...

echo TODAY=%TODAY%
echo YEAR=%YEAR%
echo COUNTRY_CODE=%COUNTRY_CODE%
echo ONLY_HOLIDAY=%ONLY_HOLIDAY%

rem ?? ???? ?? ? Java ??
cd /d "C:\IdeaProjects\ksnet\MakeCalendar\target"

echo ?? ??: "%JAVA_BIN%" -Dspring.profiles.active=dev -jar "%JAR_BIN%" --YEAR="%YEAR%" --COUNTRY-CODE="%COUNTRY_CODE%" --ONLY-HOLIDAY=%ONLY_HOLIDAY%
"%JAVA_BIN%" -Dspring.profiles.active=dev -jar "%JAR_BIN%" --YEAR="%YEAR%" --COUNTRY-CODE="%COUNTRY_CODE%" --ONLY-HOLIDAY=%ONLY_HOLIDAY%

goto end

:usage
echo.
echo [make_calendar ???]
echo "make_calendar.bat [YEAR] [ALL^|KOR^|USA^|JPN^|HGK^|CHN] <[ONLY-HOLIDAY]>"
echo ??: make_calendar.bat 2026 ALL
echo ??: make_calendar.bat 2026 KOR
echo ??: make_calendar.bat 2026 KOR ONLY-HOLIDAY
exit /b 0

:end
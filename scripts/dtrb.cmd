@echo off

setlocal

rem ***************************************************
rem * If the DTRB_HOME environment variable is not    *
rem * set, assume that the home directory is the      *
rem * parent of the directory containing this batch   *
rem * file.                                           *
rem ***************************************************

set HOME=%~dp0..
if defined DTRB_HOME set HOME=%DTRB_HOME%

rem ***************************************************
rem * Build Detroubulator's class path.               *
rem ***************************************************

set DTRB_CP=%HOME%\lib\*

rem ***************************************************
rem * Use the DTRB_JAVA_HOME environment variable to  *
rem * locate java.exe. If that variable is undefined, *
rem * use the JAVA_HOME environment variable instead. *
rem * If that's also undefined, assume that java.exe  *
rem * is in the current user's PATH.                  *
rem ***************************************************

set JAVACMD=java.exe
if defined JAVA_HOME set JAVACMD=%JAVA_HOME%\bin\java.exe
if defined DTRB_JAVA_HOME set JAVACMD=%DTRB_JAVA_HOME%\bin\java.exe
"%JAVACMD%" -Djava.util.logging.config.class=org.detroubulator.core.Logging -classpath "%CLASSPATH%;%DTRB_CP%" org.detroubulator.core.Launcher %*

@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Workflow startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables, and ensure extensions are enabled
setlocal EnableExtensions

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and WORKFLOW_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

"%COMSPEC%" /c exit 1

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

"%COMSPEC%" /c exit 1

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Workflow-1.0-SNAPSHOT.jar;%APP_HOME%\lib\client-4e618f09a0c649dde3fdf829df443ce0b8831e65.jar;%APP_HOME%\lib\authlib-7.0.63.jar;%APP_HOME%\lib\brigadier-1.3.10.jar;%APP_HOME%\lib\commons-lang3-3.19.0.jar;%APP_HOME%\lib\datafixerupper-9.0.19.jar;%APP_HOME%\lib\fastutil-8.5.18.jar;%APP_HOME%\lib\gson-2.13.2.jar;%APP_HOME%\lib\joml-1.10.8.jar;%APP_HOME%\lib\logging-1.6.11.jar;%APP_HOME%\lib\netty-codec-base-4.2.7.Final.jar;%APP_HOME%\lib\netty-transport-4.2.7.Final.jar;%APP_HOME%\lib\netty-buffer-4.2.7.Final.jar;%APP_HOME%\lib\netty-resolver-4.2.7.Final.jar;%APP_HOME%\lib\netty-common-4.2.7.Final.jar;%APP_HOME%\lib\oshi-core-6.9.0.jar;%APP_HOME%\lib\jspecify-1.0.0.jar;%APP_HOME%\lib\skyblocker-liap-v6.7.0-beta.1+26.1.2.jar;%APP_HOME%\lib\guava-32.1.2-jre.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\commons-io-2.11.0.jar;%APP_HOME%\lib\log4j-slf4j2-impl-2.25.2.jar;%APP_HOME%\lib\slf4j-api-2.0.17.jar;%APP_HOME%\lib\error_prone_annotations-2.41.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.8.20.jar;%APP_HOME%\lib\log4j-core-2.25.2.jar;%APP_HOME%\lib\log4j-api-2.25.2.jar;%APP_HOME%\lib\jna-platform-5.17.0.jar;%APP_HOME%\lib\jna-5.17.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-qual-3.33.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.8.20.jar;%APP_HOME%\lib\kotlin-stdlib-1.8.20.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.8.20.jar;%APP_HOME%\lib\annotations-13.0.jar


@rem Execute Workflow
@rem endlocal doesn't take effect until after the line is parsed and variables are expanded
@rem which allows us to clear the local environment before executing the java command
endlocal & "%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %WORKFLOW_OPTS%  -classpath "%CLASSPATH%" me.alex.workflow.Main %* & call :exitWithErrorLevel

:exitWithErrorLevel
@rem Use "%COMSPEC%" /c exit to allow operators to work properly in scripts
"%COMSPEC%" /c exit %ERRORLEVEL%

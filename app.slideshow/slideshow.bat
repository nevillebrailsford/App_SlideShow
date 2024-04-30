@echo off

rem this file is stored in the SlideShow.app directory
rem and executes the slide show application.

echo *===============================
echo * Run the SlideShow application.
echo *===============================

SETLOCAL

echo *=================================
echo * Set up the required directories.
echo *=================================

set ROOT_DIR=C:\Users\nevil\Projects\SlideShow.app
set JAR_NAME=SlideShowApp.jar
set DATA_DIR=C:\Users\nevil\OneDrive\Projects\data

echo *=============================================
echo * Invoke the program - may take a few moments.
echo *=============================================

java.exe -jar %ROOT_DIR%\%JAR_NAME% 

ENDLOCAL
#!/bin/zsh

echo "*==============================="
echo "* Run the slideshow application."
echo "*==============================="

echo "*================================="
echo "* Set up the required directories."
echo "*================================="

ROOT_DIR=/Users/nevil/Projects/SlideShow.app
JAR_NAME=SlideShowApp.jar
DATA_DIR=/Users/nevil/OneDrive/Projects/data

echo "*============================================="
echo "* Invoke the program - may take a few moments."
echo "*============================================="

java -jar ${ROOT_DIR}/${JAR_NAME}  --name=slideshow.test --dir=c:/test/data
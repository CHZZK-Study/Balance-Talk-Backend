#!/bin/bash
PROJECT_PATH="/home/ubuntu/balance_talk"
DEPLOY_LOG_PATH="$PROJECT_PATH/logs/deploy.log"
DEPLOY_ERR_LOG_PATH="$PROJECT_PATH/logs/deploy_err.log"
APPLICATION_LOG_PATH="$PROJECT_PATH/logs/application.log"
BUILD_PATH="$PROJECT_PATH/build/libs"
JAR_PATH="$BUILD_PATH/*.jar"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)

echo "===== 배포 시작 : $(date +%c) =====" >> $DEPLOY_LOG_PATH

echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $BUILD_PATH

echo "> 애플리케이션 재구동" >> $DEPLOY_LOG_PATH
pkill -9 -ef $JAR_NAME

DEPLOY_JAR=$BUILD_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH
nohup java -jar -Dspring.profiles.active=dev $DEPLOY_JAR >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH & 

sleep 3

echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH

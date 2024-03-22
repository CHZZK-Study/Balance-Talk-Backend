#!/bin/bash
DEPLOY_LOG_PATH="$PROJECT_PATH/log/deploy.log"
DEPLOY_ERR_LOG_PATH="$PROJECT_PATH/log/deploy_err.log"
PROJECT_PATH="/home/ubuntu/balance_talk"
JAR_PATH="$PROJECT_PATH/build/libs/*.jar"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)

echo "===== 배포 시작 : $(date +%c) =====" >> $DEPLOY_LOG_PATH

echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $PROJECT_PATH

echo "> 현재 동작중인 애플리케이션 PID 체크" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 동작중인 애플리케이션이 존재하지 않음" >> $DEPLOY_LOG_PATH
else
  echo "> 현재 동작중인 애플리케이션이 존재하여 강제 종료 진행" >> $DEPLOY_LOG_PATH
  echo "> kill -9 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  kill -9 $CURRENT_PID
fi

DEPLOY_JAR=$PROJECT_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH
nohup java -jar -Dspring.profiles.active=dev $DEPLOY_JAR >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH & 

sleep 3

echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH

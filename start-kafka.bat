@echo off
title Kafka Broker (KRaft Mode)
echo ===================================================
echo Starting Apache Kafka Broker (Port: 9092)...
echo ===================================================
cd /d C:\kafka
call .\bin\windows\kafka-server-start.bat .\config\server.properties
pause

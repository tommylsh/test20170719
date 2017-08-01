#!/bin/sh
cd "/home/oracle/config/domains/standalone_domain"
echo "bin/startWebLogic.sh ---> /home/oracle/config/domains/standalone_domain/servers/ESB_Dev_AdminServer/logs/ESB_Dev_AdminServer.out"
nohup bin/startWebLogic.sh > servers/ESB_Dev_AdminServer/logs/ESB_Dev_AdminServer.out 2>&1 &

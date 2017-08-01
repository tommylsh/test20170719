#!/bin/sh
cd "/home/oracle/config/domains/base_domain"
echo "bin/startWebLogic.sh ---> /home/oracle/config/domains/base_domain/servers/AdminServer/logs/AdminServer.out"
nohup bin/startWebLogic.sh > servers/AdminServer/logs/AdminServer.out 2>&1 &

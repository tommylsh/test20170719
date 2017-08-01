#!/bin/sh
cd "/home/oracle/config/domains/base_domain"
echo "bin/startManagedWebLogic.sh app_server_1 ---> /home/oracle/config/domains/base_domain/servers/app_server_1/logs/app_server_1.out"
nohup bin/startManagedWebLogic.sh app_server_1> servers/app_server_1/logs/app_server_1.out 2>&1 &

#!/bin/sh
cd "/home/oracle/config/domains/base_domain"
echo "bin/startManagedWebLogic.sh app_server_2 ---> /home/oracle/config/domains/base_domain/servers/app_server_2/logs/app_server_2.out"
nohup bin/startManagedWebLogic.sh app_server_2> servers/app_server_2/logs/app_server_2.out 2>&1 &

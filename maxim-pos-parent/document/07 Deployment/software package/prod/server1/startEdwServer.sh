#!/bin/sh
cd "/home/oracle/config/domains/base_domain"
echo "bin/startManagedWebLogic.sh edw_server_1 ---> /home/oracle/config/domains/base_domain/servers/edw_server_1/logs/edw_server_1.out"
nohup bin/startManagedWebLogic.sh edw_server_1> servers/edw_server_1/logs/edw_server_1.out 2>&1 &

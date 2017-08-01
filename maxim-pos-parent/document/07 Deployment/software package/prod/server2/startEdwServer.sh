#!/bin/sh
cd "/home/oracle/config/domains/base_domain"
echo "bin/startManagedWebLogic.sh edw_server_2 ---> /home/oracle/config/domains/base_domain/servers/edw_server_2/logs/edw_server_2.out"
nohup bin/startManagedWebLogic.sh edw_server_2> servers/edw_server_2/logs/edw_server_2.out 2>&1 &

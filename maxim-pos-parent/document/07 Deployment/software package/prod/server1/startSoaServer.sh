#!/bin/sh
cd "/home/oracle/config/domains/standalone_domain"
echo "bin/startManagedWebLogic.sh soa_server1 ---> /home/oracle/config/domains/standalone_domain/servers/soa_server1/logs/soa_server1.out"
nohup bin/startManagedWebLogic.sh soa_server1> servers/soa_server1/logs/soa_server1.out 2>&1 &

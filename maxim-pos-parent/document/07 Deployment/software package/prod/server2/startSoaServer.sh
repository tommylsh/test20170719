#!/bin/sh
cd "/home/oracle/config/domains/standalone_domain"
echo "bin/startManagedWebLogic.sh soa_server2 ---> /home/oracle/config/domains/standalone_domain/servers/soa_server2/logs/soa_server2.out"
nohup bin/startManagedWebLogic.sh soa_server2> servers/soa_server2/logs/soa_server2.out 2>&1 &

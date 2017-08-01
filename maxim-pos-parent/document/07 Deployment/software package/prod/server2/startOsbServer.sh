#!/bin/sh
cd "/home/oracle/config/domains/standalone_domain"
echo "bin/startManagedWebLogic.sh osb_server2 ---> /home/oracle/config/domains/standalone_domain/servers/osb_server2/logs/osb_server2.out"
nohup bin/startManagedWebLogic.sh osb_server2> servers/osb_server2/logs/osb_server2.out 2>&1 &

#!/bin/sh
cd "/home/oracle/config/domains/standalone_domain"
echo "bin/startManagedWebLogic.sh osb_server1 ---> /home/oracle/config/domains/standalone_domain/servers/osb_server1/logs/osb_server1.out"
nohup bin/startManagedWebLogic.sh osb_server1> servers/osb_server1/logs/osb_server1.out 2>&1 &

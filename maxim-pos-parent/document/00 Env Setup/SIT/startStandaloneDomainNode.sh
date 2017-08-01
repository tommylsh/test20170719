#!/bin/sh
cd "/home/oracle/config/domains/standalone_domain"
echo "nohup bin/startNodeManager.sh --> /home/oracle/config/domains/standalone_domain/nodemanager/nodemanager.out"
nohup bin/startNodeManager.sh > nodemanager/nodemanager.out 2>&1 &

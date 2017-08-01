#!/bin/sh
cd "/home/oracle/config/domains/base_domain"
echo "bin/startNodeManager.sh --> /home/oracle/config/domains/base_domain/nodemanager/nodemanager.out"
nohup bin/startNodeManager.sh > nodemanager/nodemanager.out 2>&1 &

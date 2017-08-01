#Start script
/u01/app/oracle/product/11.2.0/xe/config/scripts/startdb.sh>/dev/null 2>&1 &
/home/oracle/config/domains/standalone_domain/bin/startNodeManager.sh>/dev/null 2>&1 &
/home/oracle/config/domains/standalone_domain/startWebLogic.sh &
/home/oracle/config/domains/standalone_domain/bin/startManagedWebLogic.sh esb_ManagedServer_1 >/dev/null 2>&1 &
/home/oracle/config/domains/standalone_domain/bin/startManagedWebLogic.sh esb_ManagedServer_2 >/dev/null 2>&1 &
/home/oracle/config/domains/standalone_domain/bin/startManagedWebLogic.sh osb_server1 >/dev/null 2>&1 &
/home/oracle/config/domains/standalone_domain/bin/startManagedWebLogic.sh soa_server1 >/dev/null 2>&1 &

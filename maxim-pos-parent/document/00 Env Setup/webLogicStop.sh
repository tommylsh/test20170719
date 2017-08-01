#stop script
/home/oracle/config/domains/standalone_domain/bin/stopManagedWebLogic.sh esb_ManagedServer_1 &
/home/oracle/config/domains/standalone_domain/bin/stopManagedWebLogic.sh esb_ManagedServer_2 &
/home/oracle/config/domains/standalone_domain/bin/stopManagedWebLogic.sh osb_server1 &
/home/oracle/config/domains/standalone_domain/bin/stopManagedWebLogic.sh soa_server1 &
/home/oracle/config/domains/standalone_domain/stopWebLogic.sh>/dev/null 2>&1 &
/u01/app/oracle/product/11.2.0/xe/config/scripts/stopdb.sh &
nodeProcess=$(jps | grep NodeManager | awk '{print $1}')
if [["$nodeProcess != '' ]]; 
	then kill ${nodeProcess}; 
fi

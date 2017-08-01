# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
	. ~/.bashrc
fi

# User specific environment and startup programs

export JAVA_HOME=/home/oracle/products/jdk1.8.0_101
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:WL_HOME%/server/lib:%WL_HOME%/server/liblogic.jar
export WLS_HOME=/home/oracle/products/wlserver

export ORACLE_HOME=/home/oracle/products/Oracle_Home
export DOMAIN_HOME=/home/oracle/config/domains
export WL_HOME=$ORACLE_HOME/wlserver

export PATH=$WL_HOME/server/bin:$JAVA_HOME/bin:$JRE_HOME/bin:$HOME/bin:$PATH

PATH=$PATH:$HOME/bin

export PATH

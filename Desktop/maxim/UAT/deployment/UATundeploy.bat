call loadConfig "config.ini" GENERAL
call loadConfig "config.ini" UAT

SET PUTTY_FILE_SCRIPT=_deployment.txt

echo %JAVA_PATH%/bin/java -cp %WEBLOGIC_JAR%  weblogic.Deployer -url %WEBLOGIC_ADMIN_URL% -user %WEBLOGIC_USER% -password %WEBLOGIC_PASSWORD% -undeploy -name %DEPLOY_NAME% >  %PUTTY_FILE_SCRIPT%

rem weblogic.Deployer -url http://10.10.33.25:7001 -user weblogic -password Passw0rd -undeploy -name maxim-pos-web


%PUTTY_PATH%\plink.exe -m "%PUTTY_FILE_SCRIPT%" -pw %SSH_PASSWORD% %SSH_USER%@%SSH_SERVER%
pause
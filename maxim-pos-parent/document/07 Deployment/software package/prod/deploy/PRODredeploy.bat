call loadConfig "config.ini" GENERAL
call loadConfig "config.ini" PROD

SET PUTTY_FILE_SCRIPT=_deployment.txt

echo %JAVA_PATH%/bin/java -cp %WEBLOGIC_JAR%  weblogic.Deployer -url %WEBLOGIC_ADMIN_URL% -user %WEBLOGIC_USER% -password %WEBLOGIC_PASSWORD% -redeploy -name %DEPLOY_NAME% -targets %DEPLOY_SERVER% -source %WAR_UPLOAD_PATH%/%WAR_FILENAME%>  %PUTTY_FILE_SCRIPT%

%PUTTY_PATH%\plink.exe -m "%PUTTY_FILE_SCRIPT%" -pw %SSH_PASSWORD% %SSH_USER%@%SSH_SERVER%
pause
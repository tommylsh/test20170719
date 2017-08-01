call loadConfig "config.ini" GENERAL
call loadConfig "config.ini" PROD

SET PUTTY_FILE_SCRIPT=_deployment.txt

echo %JAVA_PATH%/bin/java -cp %WEBLOGIC_JAR%  weblogic.Deployer -url %WEBLOGIC_ADMIN_URL% -user %WEBLOGIC_USER% -password %WEBLOGIC_PASSWORD% -deploy -name %DEPLOY_REST_NAME% -targets %DEPLOY_REST_SERVER% -source %WAR_UPLOAD_PATH%/%REST_FILENAME%>  %PUTTY_FILE_SCRIPT%

%PUTTY_PATH%\plink.exe -m "%PUTTY_FILE_SCRIPT%" -pw %SSH_PASSWORD% %SSH_USER%@%SSH_SERVER%
pause
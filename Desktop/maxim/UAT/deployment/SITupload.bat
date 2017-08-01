call loadConfig "config.ini" GENERAL
call loadConfig "config.ini" SIT

SET FTP_FILE_SCRIPT=_upload.ftp

echo cd %WAR_UPLOAD_PATH%>  %FTP_FILE_SCRIPT%
echo put %WAR_FILENAME%>>  %FTP_FILE_SCRIPT%
echo put %REST_FILENAME%>>  %FTP_FILE_SCRIPT%
echo chmod 775 %WAR_FILENAME%>>  %FTP_FILE_SCRIPT%
echo quit>>  %FTP_FILE_SCRIPT% 

%PUTTY_PATH%\psftp.exe  -b _upload.ftp -pw %SSH_PASSWORD% %SSH_USER%@%SSH_SERVER%
pause

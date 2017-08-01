package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.util.EncryptionUtil;
import com.maxim.util.FtpUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;

@Service("ftpService")
public class FtpServiceImpl implements FtpService {

    private static final String FILE_SUFFIX_IGNORE_CASE = "*.%s";

    @Autowired
    private ApplicationSettingService applicationSettingService;

    private static final String encryptKey = "90206f7a4fc149b592a14b7629caad5e";
    
    @Override
    public List<File> downloadFile(BranchInfo branchInfo) {
        Assert.notNull(branchInfo, "'branchInfo' can't be null.");
        return downloadFile(branchInfo, branchInfo.getClientDB());
    }

    @Override
    public List<File> downloadFile(BranchInfo branchInfo, String destinationPath) {
        check(branchInfo, destinationPath);

        FtpUtils ftpUtils = FtpUtils.newInstance();
        try {
        	String password = EncryptionUtil.aesDecrypt(branchInfo.getPassword(), encryptKey);
	        boolean isSuccess = ftpUtils.connect(branchInfo.getClientHost(), branchInfo.getClientPort(), branchInfo.getUser(), password, destinationPath);
	        Assert.isTrue(isSuccess, "Ftp connect the destination path failure.");
		} catch (Exception e) {
			LogUtils.printException("aecDecrypt error in downloadFile method", e);
		}
        ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("FTP_DOWNLOAD_LOCAL_PATH");
        if (applicationSetting == null || StringUtils.isBlank(applicationSetting.getCodeValue())) {
            throw new RuntimeException("Can't get the value of 'FTP_DOWNLOAD_LOCAL_PATH' from application settings, please contact the administrator.");
        }
        String extension = branchInfo.getClientType().toString();
        if(branchInfo.getClientType().equals(ClientType.CSV)){
        	extension = "txt";
        }
        
        return ftpUtils.downloadFile(applicationSetting.getCodeValue(), String.format(FILE_SUFFIX_IGNORE_CASE, extension));
    }

    @Override
    public boolean uploadFile(BranchInfo branchInfo, String destinationPath, File file) {
        Assert.notNull(file, "'file' can't be null.");
        check(branchInfo, destinationPath);
        
        FtpUtils ftpUtils = FtpUtils.newInstance();
        
		try {
			String password = EncryptionUtil.aesDecrypt(branchInfo.getPassword(), encryptKey);
	        boolean isSuccess = ftpUtils.connect(branchInfo.getClientHost(), branchInfo.getClientPort(), branchInfo.getUser(), password, destinationPath);
	        Assert.isTrue(isSuccess, "Ftp connect the destination path failure.");
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in uploadFile method", e);
		}
        return ftpUtils.uploadFile(file);
    }

    private void check(BranchInfo branchInfo, String destinationPath) {
        Assert.notNull(branchInfo, "'branchInfo' can't be null.");
        Assert.hasText(destinationPath, "'destinationPath' can't be null.");
        if (branchInfo.getClientPort() == null) {
            throw new RuntimeException("The port can't be null.");
        }
        ClientType clientType = branchInfo.getClientType();
        if (!(clientType == ClientType.DBF || clientType == ClientType.CSV || clientType == ClientType.TEXT)) {
            throw new RuntimeException("Unsupported Client Type: " + clientType);
        }
    }

}

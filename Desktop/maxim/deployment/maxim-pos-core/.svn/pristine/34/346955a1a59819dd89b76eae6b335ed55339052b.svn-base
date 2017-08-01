package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.util.EncryptionUtil;
import com.maxim.util.NetShareUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;

@Transactional
@Service("networkShareService")
public class NetworkShareServiceImpl implements NetworkShareService {

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
        Assert.notNull(branchInfo, "'branchInfo' can't be null.");
        Assert.hasText(destinationPath, "'destinationPath' can't be null.");
        ClientType clientType = branchInfo.getClientType();
        if (!(clientType == ClientType.DBF || clientType == ClientType.CSV || clientType == ClientType.TEXT)) {
            throw new RuntimeException("Unsupported Client Type: " + clientType);
        }
        ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("NET_SHARE_DOWNLOAD_LOCAL_PATH");
        if (applicationSetting == null || StringUtils.isBlank(applicationSetting.getCodeValue())) {
            throw new RuntimeException("Can't get the value of 'NET_SHARE_DOWNLOAD_LOCAL_PATH' from application settings, please contact the administrator.");
        }

        String password = "";
		try {
			password = EncryptionUtil.aesDecrypt(branchInfo.getPassword(), encryptKey);
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in NetworkShareServiceImpl's downloadFile method ", e);
		}
        return NetShareUtil.downloadFile(
                branchInfo.getUser(),
                password,
                branchInfo.getClientHost(),
                destinationPath,
                applicationSetting.getCodeValue(),
                String.format(FILE_SUFFIX_IGNORE_CASE, branchInfo.getClientType().toString())
        );
    }

    @Override
    public boolean uploadFile(BranchInfo branchInfo, String destinationPath, File file) {
        Assert.notNull(branchInfo, "'branchInfo' can't be null.");
        Assert.hasText(destinationPath, "'destinationPath' can't be null.");
        Assert.notNull(file, "'file' can't be null.");
        String password = "";
		try {
			password = EncryptionUtil.aesDecrypt(branchInfo.getPassword(), encryptKey);
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in NetworkShareServiceImpl's uploadFile method ", e);
		}
        return NetShareUtil.uploadFile(
                branchInfo.getUser(),
                password,
                branchInfo.getClientHost(),
                destinationPath,
                file
        );
    }

}

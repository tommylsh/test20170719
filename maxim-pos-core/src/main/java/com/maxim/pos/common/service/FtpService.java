package com.maxim.pos.common.service;

import java.io.File;
import java.util.List;

import com.maxim.pos.common.entity.BranchInfo;

public interface FtpService {

    /**
     * @param branchInfo
     * @return
     */
    public List<File> downloadFile(BranchInfo branchInfo);

    public List<File> downloadFile(BranchInfo branchInfo, String destinationPath);

    /**
     * @param branchInfo
     * @param destinationPath save file path
     * @param file
     * @return
     */
    public boolean uploadFile(BranchInfo branchInfo, String destinationPath, File file);

}

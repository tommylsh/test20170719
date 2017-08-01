package com.maxim.pos.common.service;

import java.io.File;
import java.util.List;

import com.maxim.pos.common.entity.BranchInfo;

public interface NetworkShareService {

    public List<File> downloadFile(BranchInfo branchInfo);

    public List<File> downloadFile(BranchInfo branchInfo, String destinationPath);

    public boolean uploadFile(BranchInfo branchInfo, String destinationPath, File file);

}

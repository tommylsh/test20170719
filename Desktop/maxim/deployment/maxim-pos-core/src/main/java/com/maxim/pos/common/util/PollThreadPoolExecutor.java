package com.maxim.pos.common.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.service.BranchSchemeExecutor;
import com.maxim.pos.common.service.ProcessStgToEdwService;
import com.maxim.pos.common.service.ProcessStgToPosService;

public class PollThreadPoolExecutor extends ThreadPoolExecutor {

    public PollThreadPoolExecutor(int i, int j, int k, TimeUnit seconds,
                                  LinkedBlockingQueue<Runnable> arrayBlockingQueue) {
        super(i, j, k, seconds, arrayBlockingQueue);
    }


    private void printLog(BranchScheme branchScheme) {
        try {
            if (branchScheme == null) {
                LogUtils.printLog("branchScheme is null");
                return;
            } else {
                LogUtils.printLog("Poll task  execute start ...");
            }
            LogUtils.printObject(null,branchScheme);
//            LogUtils.printLog(
//                    "Poll task  execute start... BranchSchemeID={},PollSchemeType={},Direction={}"
//                            + "ClientInf={}:{}",
//
//                    branchScheme.getId(),
//                    branchScheme.getPollSchemeType(),
//                    branchScheme.getDirection(),
//                    branchScheme.getBranchInfo().getClientHost(),
//                    branchScheme.getBranchInfo().getClientDB());
        } catch (Exception e) {
            LogUtils.printException("", e);
        }
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (r instanceof BranchSchemeExecutor) {
            BranchSchemeExecutor branchSchemeExecutor = (BranchSchemeExecutor) r;
            if (branchSchemeExecutor.getLogger() != null) {
                LogUtils.setCurrentThreadLogger(branchSchemeExecutor.getLogger());
            }

            printLog(branchSchemeExecutor.getBranchScheme());


        } else if (r instanceof ProcessStgToEdwService) {
            ProcessStgToEdwService processStgToEdwService = (ProcessStgToEdwService) r;
            if (processStgToEdwService.getLogger() != null) {
                LogUtils.setCurrentThreadLogger(processStgToEdwService.getLogger());
            }
            printLog(processStgToEdwService.getBranchScheme());

        } else if (r instanceof ProcessStgToPosService) {

            {
                ProcessStgToPosService processStgToPosService = (ProcessStgToPosService) r;
                if (processStgToPosService.getLogger() != null) {
                    LogUtils.setCurrentThreadLogger(processStgToPosService.getLogger());
                }
                printLog(processStgToPosService.getBranchScheme());
            }

        }
    }

    protected void afterExecute(Runnable r, Throwable t) {
        LogUtils.setCurrentThreadLogger(null);
    }

}
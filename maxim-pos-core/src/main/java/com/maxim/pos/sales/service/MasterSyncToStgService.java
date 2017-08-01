package com.maxim.pos.sales.service;

import com.maxim.pos.common.data.MasterSyncType;

public interface MasterSyncToStgService {

    void processMasterDataToStg(MasterSyncType masterSyncType);

}

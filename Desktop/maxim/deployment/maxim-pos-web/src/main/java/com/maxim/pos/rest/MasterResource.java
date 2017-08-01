package com.maxim.pos.rest;

import com.maxim.pos.common.data.MasterSyncType;
import com.maxim.pos.sales.service.MasterSyncToStgService;
import com.maxim.rest.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/master")
@Produces(MediaType.APPLICATION_JSON)
public class MasterResource {

    @Autowired
    private MasterSyncToStgService masterSyncToStgService;

    /**
     * {"type":"BRANCH_CODE","value":"8888"}
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processMasterDataToStg(MasterSyncType masterSyncType) {
        masterSyncToStgService.processMasterDataToStg(masterSyncType);
        return Response.status(Response.Status.CREATED)
                .entity(new ResponseData())
                .build();
    }

}

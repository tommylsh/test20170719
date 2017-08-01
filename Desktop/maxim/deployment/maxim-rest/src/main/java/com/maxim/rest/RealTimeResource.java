package com.maxim.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxim.api.model.RealTimeSalesData;
import com.maxim.api.model.ResponseData;
import com.maxim.api.service.RealTimeService;
import com.maxim.common.util.JsonUtils;
import com.maxim.core.service.RealTimeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/realTime")
@Produces(MediaType.APPLICATION_JSON)
public class RealTimeResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeResource.class);

    @Resource(name = "realTimeService")
    private RealTimeService realTimeService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveRealTimeSalesData(RealTimeSalesData realTimeSalesData) {
        LOGGER.info("realTime ws start..."+ JsonUtils.bean2Json(realTimeSalesData));
        int rows = realTimeService.add(realTimeSalesData);
        return Response.status(Response.Status.CREATED)
                .entity(new ResponseData().setData(String.valueOf(rows)))
                .build();
    }

}

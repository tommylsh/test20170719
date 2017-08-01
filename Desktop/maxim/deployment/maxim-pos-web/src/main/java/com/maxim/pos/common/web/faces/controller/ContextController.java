package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lotic on 2017-05-25.
 */
@Controller
public class ContextController {

    public List getClientTypes(){
        return Arrays.asList(ClientType.values());
    }

    public List getPollSchemeType(){
        return Arrays.asList(PollSchemeType.values());
    }

    public List getDirection(){
        return Arrays.asList(Direction.values());
    }
}

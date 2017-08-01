package com.maxim.pos.security.web.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maxim.pos.security.entity.User;
import com.maxim.pos.security.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @RequestMapping("/get-users")
    public String getUsers(Model model) {
        
        List<User> users = userService.findUsers();
        model.addAttribute("users", users);
        return "/pos/security/user-list";
    }
    
}

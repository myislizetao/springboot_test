package com.cloud.controller;

import com.cloud.manager.ConfigPropertiesManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestController {

    @Resource
    private ConfigPropertiesManager configPropertiesManager;

    @GetMapping(value = "/success")
    public String success(){
        return configPropertiesManager.toString();
    }
}

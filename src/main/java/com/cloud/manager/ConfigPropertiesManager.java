package com.cloud.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value = {"classpath:/config.properties"})
@Component
public class ConfigPropertiesManager {

    @Value("${nickName}")
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "ConfigPropertiesManager{" +
                "nickName='" + nickName + '\'' +
                '}';
    }
}

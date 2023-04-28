package com.trans.migration.marc;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component("Message")
public class Message {

    @Configuration
    public static class MessageConfig {
        @Bean("MessageConfig")
        public MessageSource messageSource () {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("message");
            return messageSource;
        }
    }

    /** 스프링 관련 Resource 객체 */
    @Resource(name = "MessageConfig")
    private ResourceBundleMessageSource messageSource;

    /**
     * Returns [파일:message_ko.properties]key에 해당하는 Value
     * @param  key message_ko.properties파일의 키값
     * @return key에 해당하는 Value
     */
    public String getMessage(String key){
        String temp = null;
        try{
            temp = messageSource.getMessage( key, null, null, Locale.KOREAN);
            temp = temp.trim();
        }catch(NullPointerException e){
            log.debug("[value를 찾을 수 없습니다. key : " + key+"]");
            temp = "";
        }catch(Exception e){
            log.debug("[value를 찾을 수 없습니다. key : " + key+"]");
            temp = "";
        }
        return temp;
    }
}

package com.lj.asistenlj.service.event;

import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.lj.asistenlj.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@LineMessageHandler
public class UnfollowEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnfollowEventHandler.class);

    @Autowired
    private ChatService chatService;

    @EventMapping
    public void unfollowEvent(UnfollowEvent unfollowEvent){
        LOGGER.info("Ada unfollow event");
    }

}

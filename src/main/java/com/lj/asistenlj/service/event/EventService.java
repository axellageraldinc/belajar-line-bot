package com.lj.asistenlj.service.event;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.*;
import com.linecorp.bot.model.event.message.*;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.lj.asistenlj.helper.Helper;
import com.lj.asistenlj.model.GroupMember;
import com.lj.asistenlj.repository.GroupRepository;
import com.lj.asistenlj.service.GroupMemberService;
import com.lj.asistenlj.service.GroupService;
import com.lj.asistenlj.service.fitur.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
public class EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    @Autowired
    LineMessagingClient lineMessagingClient;
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private TextMessage joinGroupMessage;
    @Autowired
    private TemplateMessage listFiturTemplateMessage;
    @Autowired
    private TemplateMessage hiburanTemplateMessage1;
    @Autowired
    private TemplateMessage hiburanTemplateMessage2;
    @Autowired
    private TemplateMessage templateAbout1;
    @Autowired
    private TemplateMessage templateAbout2;
    @Autowired
    private TemplateMessage templateAbout3;
    @Autowired
    private TemplateMessage templateAbout4;
    @Autowired
    private StickerMessage newFollowerStickerMessage;
    @Autowired
    private TextMessage newFollowerMessage;
    @Autowired
    private TextMessage sourceCodeMessage;
    @Autowired
    private TextMessage sponsorMessage;
    @Autowired
    private ApakahService apakahService;
    @Autowired
    private SiapakahService siapakahService;
    @Autowired
    private LoveService loveService;
    @Autowired
    private DosaService dosaService;
    @Autowired
    private FaceDetectService faceDetectService;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private TextMessage leaveMessageFail1;
    @Autowired
    private TextMessage caraPakaiSiapakah;
    @Autowired
    private TextMessage caraPakaiWajah;
    @Autowired
    private TextMessage caraPakaiCinta;
    @Autowired
    private TextMessage caraPakaiInstagram;
    @Autowired
    private TextMessage caraPakaiDosa;
    @Autowired
    private TextMessage caraPakaiKapankah;
    @Autowired
    private TextMessage caraPakaiIslami;
    @Autowired
    private TextMessage caraPakaiDimanakah;
    @Autowired
    private Helper helper;
    @Autowired
    private GroupRepository groupRepository;

    @EventMapping
    public void textEvent(MessageEvent<TextMessageContent> messageEvent){
        String pesan = messageEvent.getMessage().getText().toUpperCase();
        String[] pesanSplit = pesan.split(" ");

        List<Message> messageList = new ArrayList<>();

        GroupMember groupMember = groupMemberService.buildGroupMember(messageEvent.getSource());
        groupService.saveGroupToDatabase(groupService.buildGroup(messageEvent.getSource()));
        groupMemberService.saveGroupMemberToDatabase(groupMember);

        String replyToken = messageEvent.getReplyToken();
        Source source = messageEvent.getSource();

        if ("APAKAH".equals(pesanSplit[0])) {
            sendResponseMessage(replyToken, apakahService.getApakahReplyFromRandomInt());
        } else if("SIAPAKAH".equals(pesanSplit[0])){
            sendResponseMessage(replyToken, siapakahService.getResult(pesan, source));
        } else if ("/FITUR".equals(pesanSplit[0])) {
            sendResponseMessage(replyToken, listFiturTemplateMessage);
        } else if ("/HIBURAN".equals(pesanSplit[0])) {
            messageList.add(hiburanTemplateMessage1);
            messageList.add(hiburanTemplateMessage2);
            sendResponseMessage(replyToken, messageList);
        } else if ("/SOURCE-CODE".equals(pesanSplit[0])) {
            sendResponseMessage(replyToken, sourceCodeMessage);
        } else if ("/SPONSOR".equals(pesanSplit[0])) {
            sendResponseMessage(replyToken, sponsorMessage);
        } else if ("/ABOUT".equals(pesanSplit[0])) {
            messageList.add(templateAbout1);
            messageList.add(templateAbout2);
            messageList.add(templateAbout3);
            messageList.add(templateAbout4);
            sendResponseMessage(replyToken, messageList);
        } else if("/LOVE".equals(pesanSplit[0])){
            sendResponseMessage(replyToken, loveService.getLoveCalculatorResult(pesan));
        } else if("/DOSA".equals(pesanSplit[0])){
            sendResponseMessage(replyToken, dosaService.getDosaResult(pesan));
        } else if("/FACE-DETECT".equals(pesanSplit[0])){
            sendResponseMessage(replyToken, faceDetectService.responseMessage(source, "face-detect"));
        } else if("/STOP".equals(pesanSplit[0])){
            sendResponseMessage(replyToken, faceDetectService.responseMessage(source, "stop"));
        } else if("SAYA PERCAYA BAHWA SAYA ADALAH ORANG YANG JELEK".equals(pesan)){
            messageList = leaveService.getReplyMessages(source);
            sendResponseMessage(replyToken, messageList);
            if(!messageList.contains(leaveMessageFail1)) {
                leaveService.leave(source);
            }
        }
    }
    @EventMapping
    public void newFollowerEvent(FollowEvent followEvent){
        String replytoken = followEvent.getReplyToken();
        List<Message> messageList = new ArrayList<>();
        messageList.add(newFollowerStickerMessage);
        messageList.add(newFollowerMessage);
        sendResponseMessage(replytoken, messageList);
    }
    @EventMapping
    public void joinGroupEvent(JoinEvent joinEvent){
        sendResponseMessage(joinEvent.getReplyToken(), joinGroupMessage);
    }
    @EventMapping
    public void stickerEvent(MessageEvent<StickerMessageContent> messageEvent){
        LOGGER.info("Ada message sticker");
    }
    @EventMapping
    public void imageEvent(MessageEvent<ImageMessageContent> imageEvent){
        String replyToken = imageEvent.getReplyToken();
        String groupId = helper.getId(imageEvent.getSource());
        boolean imageDetectStatus = groupRepository.findImageDetectStatusByGroupId(groupId);
        if(imageDetectStatus)
            sendResponseMessage(replyToken, faceDetectService.getResult(imageEvent.getSource(), imageEvent.getMessage().getId()));
    }
    @EventMapping
    public void audioEvent(MessageEvent<AudioMessageContent> audioEvent){
        LOGGER.info("Ada audio message");
    }
    @EventMapping
    public void videoEvent(MessageEvent<VideoMessageContent> videoEvent){
        LOGGER.info("Ada video message");
    }
    @EventMapping
    public void fileEvent(MessageEvent<FileMessageContent> fileEvent){
        LOGGER.info("Ada file message");
    }
    @EventMapping
    public void locationEvent(MessageEvent<LocationMessageContent> locationEvent){
        LOGGER.info("Ada location message");
    }
    @EventMapping
    public void postbackEvent(PostbackEvent postbackEvent){
        String replyToken = postbackEvent.getReplyToken();
        String postbackMessage = postbackEvent.getPostbackContent().getData();
        switch (postbackMessage){
            case "/CARA-PAKAI-SIAPAKAH":
                sendResponseMessage(replyToken, caraPakaiSiapakah);
                break;
            case "/CARA-PAKAI-WAJAH":
                sendResponseMessage(replyToken, caraPakaiWajah);
                break;
            case "/CARA-PAKAI-CINTA":
                sendResponseMessage(replyToken, caraPakaiCinta);
                break;
            case "/CARA-PAKAI-INSTAGRAM":
                sendResponseMessage(replyToken, caraPakaiInstagram);
                break;
            case "/CARA-PAKAI-DOSA":
                sendResponseMessage(replyToken, caraPakaiDosa);
                break;
            case "/CARA-PAKAI-KAPANKAH":
                sendResponseMessage(replyToken, caraPakaiKapankah);
                break;
            case "/CARA-PAKAI-ISLAMI":
                sendResponseMessage(replyToken, caraPakaiIslami);
                break;
            case "/CARA-PAKAI-DIMANAKAH":
                sendResponseMessage(replyToken, caraPakaiDimanakah);
                break;
        }
    }
    @EventMapping
    public void unfollowEvent(UnfollowEvent unfollowEvent){
        LOGGER.info("Ada unfollow event");
    }
    @EventMapping
    public void beaconEvent(BeaconEvent beaconEvent){
        LOGGER.info("Ada beacon event");
    }
    @EventMapping
    public void handleLeave(LeaveEvent leaveEvent){
        LOGGER.info("Ada leave event");
    }

    private void sendResponseMessage(String replyToken, List<Message> messageList){
        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messageList))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error Send Message : " + e.getMessage());
        }
    }
    private void sendResponseMessage(String replyToken, Message message){
        sendResponseMessage(replyToken, Collections.singletonList(message));
    }


}

package com.lj.asistenlj.service.fitur;

import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.TextMessage;

public interface FaceDetectService {
    TextMessage getResult(Source source, String id);
    TextMessage responseMessage(Source source, String message);
}

package net.mengkang.service;

import net.mengkang.entity.Client;
import org.json.JSONObject;

public class MessageService {

    public static String createMessage(int errorCode,String errorMess,long clientId, String message) {
        JSONObject msg = new JSONObject();
        msg.put("errorCode",errorCode);
        msg.put("errorMess",errorMess);
        msg.put("clientId",clientId);
        msg.put("data",message);
        msg.put("time",System.currentTimeMillis());
        return msg.toString();
    }
}

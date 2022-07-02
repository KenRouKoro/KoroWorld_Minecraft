package cn.korostudio.koroworld.chatsyn.processor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworld.chatsyn.ChatSyn;
import cn.korostudio.koroworld.chatsyn.data.ChatDataPack;
import cn.korostudio.koroworld.connect.api.DataPackListener;
import cn.korostudio.koroworld.core.util.MessageTool;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ChatDataPackProcessor extends DataPackListener {
    @Override
    public void run(JSONObject datapack) {
        ChatDataPack chatDataPack = JSONUtil.toBean(datapack,ChatDataPack.class);
        if(!Objects.equals(chatDataPack.getGroup(), ChatSyn.getGroup())){
            return;
        }
        Map<String,String> values = MessageTool.ProcessValues(MessageTool.getStaticTemplateValues(),null);
        values.put("ServerName",chatDataPack.getServerName());
        values.put("player",chatDataPack.getPlayerName());
        MessageTool.Chat(chatDataPack.getPlayerName(),chatDataPack.getData(), UUID.fromString(chatDataPack.getPlayerUUID()),values);
    }

    @Override
    public String getType() {
        return "ChatDataPack";
    }
}

package cn.korostudio.koroworld.chatsyn;

import cn.hutool.core.util.StrUtil;
import cn.korostudio.koroworld.chatsyn.data.ChatDataPack;
import cn.korostudio.koroworld.chatsyn.processor.ChatDataPackProcessor;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.connect.api.DataAPI;
import cn.korostudio.koroworld.core.KoroWorldCore;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.event.interfaces.PlayerChatEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.util.ActionResult;

@Slf4j
public class ChatSyn implements DedicatedServerModInitializer {
    @Getter
    protected static String Group;

    @Override
    public void onInitializeServer() {
        log.info("KoroWorld-ChatSyn is Loading!");
        Init();
    }
    protected static void Init(){
        Group = Data.KoroWorldConfig.getStr("Group","chatsyn","KoroWorld");
        PlayerChatEvent.EVENT.register((player, text) -> {
            ChatDataPack chatDataPack = new ChatDataPack();
            chatDataPack.setUUID(StrUtil.uuid());
            chatDataPack.setTarget("ALL");
            chatDataPack.setData(text);
            chatDataPack.setPlayerName(player.getName().asString());
            chatDataPack.setGroup(Group);
            chatDataPack.setFrom(KoroWorldConnect.SUID);
            chatDataPack.setPlayerUUID(player.getUuid().toString());
            chatDataPack.setServerName(KoroWorldCore.getServerName());
            DataAPI.sendToWS(chatDataPack);
            return ActionResult.PASS ;
        });
        DataAPI.listen(new ChatDataPackProcessor());
    }
}

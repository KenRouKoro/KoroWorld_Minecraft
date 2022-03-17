package cn.korostudio.koroworldcore.event;

import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.PlayerPOSData;
import cn.korostudio.koroworldcore.util.MessageTool;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class DeathEvent {

    public static ActionResult onDeath(ServerPlayerEntity player){
        PlayerPOSData.deathMap.put(player.getUuidAsString(),new PlayerPOSData(player.getX(),player.getY(),player.getZ(),player.getUuidAsString(),player.getWorld()));
        MessageTool.sendSystemMessage(player,"建议使用 /backdeath 指令返回死亡地点。\n（如果死岩浆、方块、虚空里就算了XD）", KoroworldCore.getServerName());
        return ActionResult.PASS;
    }

}

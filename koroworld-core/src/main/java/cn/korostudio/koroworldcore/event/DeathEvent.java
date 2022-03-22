package cn.korostudio.koroworldcore.event;

import cn.korostudio.koroworldcore.KoroworldCore;
import cn.korostudio.koroworldcore.data.PlayerPOSData;
import cn.korostudio.koroworldcore.util.MessageTool;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class DeathEvent {

    public static boolean saveDeath = true;

    public static ActionResult onDeath(ServerPlayerEntity player){
        if(!saveDeath){
            MessageTool.sendSystemMessage(player, "         蔡");
            return ActionResult.PASS;
        }else {
            PlayerPOSData.teleportMap.put(player.getUuidAsString(), new PlayerPOSData(player.getX(), player.getY(), player.getZ(), player.getUuidAsString(), player.getWorld()));
            MessageTool.sendSystemMessage(player, "建议使用 /back 指令返回死亡地点。（如果死岩浆、方块、虚空里就算了XD）");
            return ActionResult.PASS;
        }
    }

}

package cn.korostudio.koroworld.command.command;

import cn.hutool.core.util.IdUtil;
import cn.korostudio.koroworld.command.data.TPData;
import cn.korostudio.koroworld.core.util.MessageTool;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Util;

@Slf4j(topic = "KoroWorld-Command")
public class TPACommand {
    public static int TPHere(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            TPData tpData = new TPData();
            ServerPlayerEntity target;
            try {
                target = EntityArgumentType.getPlayer(server, "player");
            } catch (CommandSyntaxException ignored) {
                MessageTool.Say(player,new TranslatableText("koroworld.tpa.playernameerror"));
                return 1;
            }
            String uuid = IdUtil.simpleUUID();
            tpData.setMode(true);
            tpData.setTarget(target);
            tpData.setFrom(player);
            tpData.setTime(System.currentTimeMillis() / 1000L);
            tpData.setUuid(uuid);
            TPData.getTPData().put(uuid, tpData);

            MutableText sLine1 = new TranslatableText("koroworld.tpa.sendertext", target.getName());
            MessageTool.Say(player,sLine1);

            MutableText line1 = new TranslatableText("koroworld.tpa.tphtext", player.getName());
            MutableText line2 = new TranslatableText("koroworld.tpa.agree").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa process " + uuid)).withColor(TextColor.parse("#00FFFF")))
                    .append(new LiteralText("    "))
                    .append(new TranslatableText("koroworld.tpa.no").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa unprocess " + uuid)).withColor(TextColor.parse("#00FFFF"))));
            target.sendMessage(line1, MessageType.SYSTEM, Util.NIL_UUID);
            target.sendMessage(line2, MessageType.SYSTEM, Util.NIL_UUID);
        } catch (CommandSyntaxException ignored) {
            log.info("别在控制台执行这个指令！");
        }

        return 1;
    }

    public static int TPTo(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            TPData tpData = new TPData();
            ServerPlayerEntity target;
            try {
                target = EntityArgumentType.getPlayer(server, "player");
            } catch (CommandSyntaxException ignored) {
                MessageTool.Say(player ,new TranslatableText("koroworld.tpa.playernameerror"));
                return 1;
            }
            String uuid = IdUtil.simpleUUID();
            tpData.setMode(false);
            tpData.setTarget(target);
            tpData.setFrom(player);
            tpData.setTime(System.currentTimeMillis() / 1000L);
            tpData.setUuid(uuid);
            TPData.getTPData().put(uuid, tpData);

            MutableText sLine1 = new TranslatableText("koroworld.tpa.sendertext", target.getName());
            MessageTool.Say(player,sLine1);

            MutableText line1 = new TranslatableText("koroworld.tpa.tpttext", player.getName());
            MutableText line2 = new TranslatableText("koroworld.tpa.agree").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa process " + uuid)).withColor(TextColor.parse("#00FFFF")))
                    .append(new LiteralText("    "))
                    .append(new TranslatableText("koroworld.tpa.no").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa unprocess " + uuid)).withColor(TextColor.parse("#00FFFF"))));
            target.sendMessage(line1, MessageType.SYSTEM, Util.NIL_UUID);
            target.sendMessage(line2, MessageType.SYSTEM, Util.NIL_UUID);
        } catch (CommandSyntaxException ignored) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }

    public static int TPProcess(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            String text;
            text = StringArgumentType.getString(server, "UUID");
            String uuid = text.trim();
            TPData tpData = TPData.getTPData().get(uuid);

            if (tpData == null) {
                MutableText nullText = new TranslatableText("koroworld.tpa.nulltpdataerror");
                player.sendMessage(nullText, MessageType.SYSTEM, Util.NIL_UUID);
                return 1;
            }
            if (tpData.getTime() + 120 < (System.currentTimeMillis() / 1000L)) {
                MutableText timeoutText = new TranslatableText("koroworld.tpa.timeouttpdataerror");
                TPData.getTPData().remove(tpData.getUuid());
                return 1;
            }
            TPData.getTPData().remove(tpData.getUuid());

            MutableText targetProcessText = new TranslatableText("koroworld.tpa.targetprocesstext", tpData.getFrom().getName());
            MessageTool.Say(player, targetProcessText);

            MutableText senderProcessText = new TranslatableText("koroworld.tpa.senderprocesstext", player.getName());
            MessageTool.Say(tpData.getFrom(), senderProcessText);

            if (tpData.isMode()) {
                ServerPlayerEntity from = tpData.getFrom();
                tpData.getTarget().teleport(from.getWorld(), from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
            } else {
                ServerPlayerEntity target = tpData.getFrom();
                tpData.getFrom().teleport(target.getWorld(), target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch());
            }
            MutableText tpSuccessText = new TranslatableText("koroworld.tpa.tpsuccesstext");
            MessageTool.Say(player, tpSuccessText);
            MessageTool.Say(tpData.getFrom(), tpSuccessText);
            log.debug("请求UUID:" + tpData.getUuid() + " 已被同意。");
        } catch (CommandSyntaxException ignored) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }

    public static int TPUnProcess(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            String text;
            text = StringArgumentType.getString(server, "UUID");
            String uuid = text.trim();
            TPData tpData = TPData.getTPData().get(uuid);

            if (tpData == null) {
                MutableText nullText = new TranslatableText("koroworld.tpa.nulltpdataerror");
                player.sendMessage(nullText, MessageType.SYSTEM, Util.NIL_UUID);
                return 1;
            }
            TPData.getTPData().remove(tpData.getUuid());

            MutableText targetUnProcessText = new TranslatableText("koroworld.tpa.targetunprocesstext", tpData.getFrom().getName());
            MessageTool.Say(player, targetUnProcessText);

            MutableText senderUnProcessText = new TranslatableText("koroworld.tpa.senderunprocesstext", player.getName());
            MessageTool.Say(tpData.getFrom(), senderUnProcessText);

            log.debug("请求UUID：" + tpData.getUuid() + " 已被拒绝。");

        } catch (CommandSyntaxException ignored) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }
}

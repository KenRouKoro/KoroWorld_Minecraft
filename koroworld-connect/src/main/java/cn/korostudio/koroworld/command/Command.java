package cn.korostudio.koroworld.command;

import cn.hutool.core.thread.ThreadUtil;
import cn.korostudio.koroworld.connect.KoroWorldConnect;
import cn.korostudio.koroworld.connect.api.DataAPI;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.util.MessageTool;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    /**
     * 示例代码
     * @param server dispatcher
     * @param b dedicated
     */
    public static void register(CommandDispatcher<ServerCommandSource> server, boolean b) {
        /*
        sServer.register(literal("savestr").then(argument("tags", StringArgumentType.word()).then(argument("message", StringArgumentType.string()).executes(server->{
            ServerPlayerEntity player = server.getSource().getPlayer();
            String tags = StringArgumentType.getString(server, "tags");
            String message = StringArgumentType.getString(server, "message");
            MessageTool.Say(player,new LiteralText("正在发送信息。。。"));

            ThreadUtil.execute(()->{
                String uuid = DataAPI.saveData(tags,message,false);

                MessageTool.Say(player,new LiteralText("已发送信息，UUID："+uuid));

            });
            return 1;
        }))));
        sServer.register(literal("getStr").then(argument("tags", StringArgumentType.word()).executes(server->{
            ServerPlayerEntity player = server.getSource().getPlayer();
            String tags = StringArgumentType.getString(server, "tags");
            MessageTool.Say(player,new LiteralText("正在拉取信息。。。"));

            ThreadUtil.execute(()->{
                String s = DataAPI.getData(tags,false);
                if (s==null){
                    MessageTool.Say(player,new LiteralText("没有信息"));
                }else {
                    MessageTool.Say(player,new LiteralText("信息："+s));
                }
            });
            return 1;
        })));

         */
    }

}

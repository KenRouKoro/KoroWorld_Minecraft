package cn.korostudio.koroworld.command.command;

import cn.korostudio.koroworld.core.data.Data;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class Command {

    public static void register(CommandDispatcher<ServerCommandSource> server, boolean b) {
        if (Data.commandEn.getBool("backEN", true)) server.register(literal("back").executes(BaseCommand::back));
        if (Data.commandEn.getBool("spawnEN", true)) server.register(literal("spawn").executes(BaseCommand::spawn));

        if (Data.commandEn.getBool("tpaEN", true)) {
            server.register(literal("tpa").then((CommandManager.literal("here")).then((CommandManager.argument("player", EntityArgumentType.player())).executes(TPACommand::TPHere))));
            server.register(literal("tpa").then((CommandManager.literal("to")).then((CommandManager.argument("player", EntityArgumentType.player())).executes(TPACommand::TPTo))));
            server.register(literal("tpa").then((CommandManager.literal("process")).then((CommandManager.argument("UUID", StringArgumentType.string())).executes(TPACommand::TPProcess))));
            server.register(literal("tpa").then((CommandManager.literal("unprocess")).then((CommandManager.argument("UUID", StringArgumentType.string())).executes(TPACommand::TPUnProcess))));
        }

        if (Data.commandEn.getBool("homeEN", true)) {
            server.register(literal("home").executes(HomeCommand::home).then(CommandManager.argument("name", StringArgumentType.word()).executes(HomeCommand::homeWithName)));
            server.register(literal("sethome").then(CommandManager.argument("name", StringArgumentType.word()).executes(HomeCommand::setHome)));
            server.register(literal("removehome").then(CommandManager.argument("name", StringArgumentType.word()).executes(HomeCommand::removeHome)));
            server.register(literal("setdefhome").then(CommandManager.argument("name", StringArgumentType.word()).executes(HomeCommand::setDefHome)));
            server.register(literal("homelist").executes(HomeCommand::homeList));
        }
    }


}

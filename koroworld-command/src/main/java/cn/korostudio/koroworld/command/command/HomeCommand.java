package cn.korostudio.koroworld.command.command;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.korostudio.koroworld.command.data.HomeData;
import cn.korostudio.koroworld.core.data.Data;
import cn.korostudio.koroworld.core.util.MessageTool;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.List;
import java.util.Objects;

@Slf4j(topic = "KoroWorld-Command")
public class HomeCommand {

    public static int home(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            HomeData homeData = HomeData.HOMEDATA.get(player.getUuidAsString());
            if (homeData == null) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
            List<HomeData.HomePOSData> posData = homeData.getHomes();
            try {
                HomeData.HomePOSData homePOSData = posData.get(0);
                ServerWorld world = Data.server.getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(homePOSData.getWorld())));
                player.teleport(world, homePOSData.getX(), homePOSData.getY(), homePOSData.getZ(), homePOSData.getYaw(), homePOSData.getPitch());
                MutableText backHomeSuccessText = new TranslatableText("koroworld.home.backhomesuccesstext", homePOSData.getName());
                MessageTool.Say(player, backHomeSuccessText);
            } catch (IndexOutOfBoundsException e) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
        } catch (CommandSyntaxException e) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }

    public static int homeWithName(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            String homeNameStr = StringArgumentType.getString(server, "name");

            HomeData homeData = HomeData.HOMEDATA.get(player.getUuidAsString());
            if (homeData == null) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
            List<HomeData.HomePOSData> posData = homeData.getHomes();
            try {
                HomeData.HomePOSData homePOSData = null;

                for (HomeData.HomePOSData homePOSData1 : posData) {
                    if (Objects.equals(homePOSData1.getName(), homeNameStr)) {
                        homePOSData = homePOSData1;
                        break;
                    }
                }

                if (homePOSData == null) {
                    MutableText homeNotFoundText = new TranslatableText("koroworld.home.homenotfoundtext", homeNameStr);
                    MessageTool.Say(player, homeNotFoundText);
                    return 1;
                }

                ServerWorld world = Data.server.getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(homePOSData.getWorld())));
                player.teleport(world, homePOSData.getX(), homePOSData.getY(), homePOSData.getZ(), homePOSData.getYaw(), homePOSData.getPitch());
                MutableText backHomeSuccessText = new TranslatableText("koroworld.home.backhomesuccesstext", homePOSData.getName());
                MessageTool.Say(player, backHomeSuccessText);
            } catch (IndexOutOfBoundsException e) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
        } catch (CommandSyntaxException e) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }

    public static int setHome(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            String homeNameStr = StringArgumentType.getString(server, "name");

            HomeData homeData = HomeData.HOMEDATA.get(player.getUuidAsString());
            if (homeData == null) {
                homeData = new HomeData(player.getUuidAsString());
                HomeData.HOMEDATA.put(player.getUuidAsString(), homeData);
            }
            if (homeData.getHomes().size() >= HomeData.getMaxHomes()) {
                MutableText maxHomeText = new TranslatableText("koroworld.home.maxhometext");
                MessageTool.Say(player, maxHomeText);
                return 1;
            }

            HomeData.HomePOSData homePOSData = new HomeData.HomePOSData(homeNameStr, player.getWorld().getRegistryKey().getValue().getNamespace() + ":" + player.getWorld().getRegistryKey().getValue().getPath(),
                    player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            homeData.getHomes().add(homePOSData);

            ThreadUtil.execute(HomeData::saveHomes);
            MutableText setHomeSuccessText = new TranslatableText("koroworld.home.sethomesuccesstext", homeNameStr);
            MessageTool.Say(player, setHomeSuccessText);


        } catch (CommandSyntaxException e) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }

    public static int removeHome(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            String homeNameStr = StringArgumentType.getString(server, "name");
            HomeData homeData = HomeData.HOMEDATA.get(player.getUuidAsString());
            if (homeData == null) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
            List<HomeData.HomePOSData> posData = homeData.getHomes();
            HomeData.HomePOSData homePOSData = null;
            for (HomeData.HomePOSData homePOSData1 : posData) {
                if (Objects.equals(homePOSData1.getName(), homeNameStr)) {
                    homePOSData = homePOSData1;
                    break;
                }
            }
            if (homePOSData == null) {
                MutableText homeNotFoundText = new TranslatableText("koroworld.home.homenotfoundtext", homeNameStr);
                MessageTool.Say(player, homeNotFoundText);
                return 1;
            }
            posData.remove(homePOSData);
            if (posData.size() == 0) {
                HomeData.HOMEDATA.remove(player.getUuidAsString());
            }
            ThreadUtil.execute(HomeData::saveHomes);

            MutableText setHomeSuccessText = new TranslatableText("koroworld.home.removehomesuccesstext", homeNameStr);
            MessageTool.Say(player, setHomeSuccessText);
        } catch (CommandSyntaxException e) {
            log.info("别在控制台执行这个指令！");
        }

        return 1;
    }

    public static int homeList(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            HomeData homeData = HomeData.HOMEDATA.get(player.getUuidAsString());
            if (homeData == null) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
            List<HomeData.HomePOSData> posData = homeData.getHomes();
            if (posData.size() == 0) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
            MutableText homeListTitle = new TranslatableText("koroworld.home.homelisttitle");
            MessageTool.Say(player, homeListTitle);
            for (int i = 0; i < posData.size(); i++) {
                String outStr = (i + 1) + ": " + posData.get(i).getName();
                MutableText lineText = new LiteralText(outStr.length() > 16 ? outStr.substring(0, 13) + "...  " : StrUtil.fillAfter(outStr, ' ', 18));
                lineText.append(new TranslatableText("koroworld.home.tp").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + posData.get(i).getName())).withColor(TextColor.parse("#00FFFF"))));
                lineText.append(new LiteralText("  "));
                lineText.append(new TranslatableText("koroworld.home.delete").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/removehome " + posData.get(i).getName())).withColor(TextColor.parse("#00FFFF"))));
                MessageTool.Say(player, lineText);
            }
        } catch (CommandSyntaxException e) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }

    public static int setDefHome(CommandContext<ServerCommandSource> server) {
        try {
            ServerPlayerEntity player = server.getSource().getPlayer();
            String homeNameStr = StringArgumentType.getString(server, "name");
            HomeData homeData = HomeData.HOMEDATA.get(player.getUuidAsString());
            if (homeData == null) {
                MutableText notSetHomeText = new TranslatableText("koroworld.home.notsethometext");
                MessageTool.Say(player, notSetHomeText);
                return 1;
            }
            List<HomeData.HomePOSData> posData = homeData.getHomes();
            HomeData.HomePOSData homePOSData = null;
            for (HomeData.HomePOSData homePOSData1 : posData) {
                if (Objects.equals(homePOSData1.getName(), homeNameStr)) {
                    homePOSData = homePOSData1;
                    break;
                }
            }
            if (homePOSData == null) {
                MutableText homeNotFoundText = new TranslatableText("koroworld.home.homenotfoundtext", homeNameStr);
                MessageTool.Say(player, homeNotFoundText);
                return 1;
            }
            if(posData.size()!=1){
                int index = posData.indexOf(homePOSData);
                posData.set(index,posData.get(0));
                posData.set(0,homePOSData);
                HomeData.saveHomes();
            }

            MutableText setDefHomeSuccessText = new TranslatableText("koroworld.home.setdefhomesuccesstext",homeNameStr);
            MessageTool.Say(player,setDefHomeSuccessText);

        } catch (CommandSyntaxException e) {
            log.info("别在控制台执行这个指令！");
        }
        return 1;
    }


}

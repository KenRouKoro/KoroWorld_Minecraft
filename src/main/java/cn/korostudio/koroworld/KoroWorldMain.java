package cn.korostudio.koroworld;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.korostudio.koroworld.block.ServerCaseBlock;
import cn.korostudio.koroworld.block.ServerCaseBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class KoroWorldMain implements ModInitializer {

    public static Setting setting ;

    public static MinecraftServer server;

    public static final ServerCaseBlock SERVER_CASE_BLOCK = new ServerCaseBlock(FabricBlockSettings.of(Material.METAL).hardness(4.0f).resistance(2400f));
    public static final ItemGroup KOROWORLD_GROUP = FabricItemGroupBuilder.build(
            new Identifier("koroworld", "grouplist"),
            () -> new ItemStack(SERVER_CASE_BLOCK));
    public static final Item SERVER_CASE_ITEM = new BlockItem(SERVER_CASE_BLOCK, new Item.Settings().group(KOROWORLD_GROUP));
    public static BlockEntityType<ServerCaseBlockEntity> SEVER_CASE_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        setting = new Setting(FileUtil.touch(System.getProperty("user.dir")+"/koroworld/config/koroworld.setting"), CharsetUtil.CHARSET_UTF_8,true);
        register();
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            server = minecraftServer;
        });
    }

    public void register(){
        Registry.register(Registry.BLOCK, new Identifier("koroworld", "server_case"), SERVER_CASE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("koroworld", "server_case"), SERVER_CASE_ITEM);

        SEVER_CASE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "koroworld:server_case_block_entity", FabricBlockEntityTypeBuilder.create(ServerCaseBlockEntity::new, SERVER_CASE_BLOCK).build(null));
    }
}

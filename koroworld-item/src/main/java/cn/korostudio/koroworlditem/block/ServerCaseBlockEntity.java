package cn.korostudio.koroworlditem.block;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.koroworlditem.KoroworldItem;
import cn.korostudio.koroworlditem.data.ItemSystemData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerCaseBlockEntity extends BlockEntity implements ImplementedInventory {

    protected static Logger logger = LoggerFactory.getLogger(ServerCaseBlockEntity.class);

    @Getter
    @Setter
    protected int mode=0;

    @Getter
    @Setter
    protected String UUID="null";

    @Getter
    @Setter
    protected int index = 0;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);

    public ServerCaseBlockEntity( BlockPos pos, BlockState state) {
        super(KoroworldItem.SEVER_CASE_BLOCK_ENTITY, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, ServerCaseBlockEntity ei) {
        ServerCaseBlockEntity serverCaseBlockEntity = (ServerCaseBlockEntity) world.getBlockEntity(pos);
        if(!ItemSystemData.serverCaseEnable){
            return;
        }
        if (serverCaseBlockEntity==null){
            return;
        }
        if(serverCaseBlockEntity.mode==0){
            return;
        }
        serverCaseBlockEntity.index++;
        if(serverCaseBlockEntity.index<=40){
            return;
        }
        serverCaseBlockEntity.index=0;
        if(serverCaseBlockEntity.mode<=12){
            send(world,pos,state,ei,serverCaseBlockEntity);
        }else{
            download(world,pos,state,ei,serverCaseBlockEntity);
        }
    }

    public static void send(World world, BlockPos pos, BlockState state, ServerCaseBlockEntity ei,ServerCaseBlockEntity serverCaseBlockEntity){
        ThreadUtil.execute(()->{
            boolean back =true;
            for(ItemStack stack:serverCaseBlockEntity.items){
                if(!stack.isEmpty()){
                    back=false;
                    break;
                }
            }
            if (back){
                return;
            }

            String use=null;
            try{
                HashMap<String,Object> keyPostMap = new HashMap<>();
                keyPostMap.put("UUID",serverCaseBlockEntity.UUID);
                keyPostMap.put("id",serverCaseBlockEntity.mode+"");
                keyPostMap.put("group",ItemSystemData.itemGroup);
                use = HttpUtil.post(ItemSystemData.httpServer + "/servercase/use", keyPostMap);
            }catch (HttpException ignored){}

            if(use==null){
                return;
            }else if(use.equals("false")){
                return;
            }

            String lock;
            try{
                HashMap<String,Object> keyPostMap = new HashMap<>();
                keyPostMap.put("key","set");
                keyPostMap.put("UUID",serverCaseBlockEntity.UUID);
                keyPostMap.put("group",ItemSystemData.itemGroup);
                keyPostMap.put("value","true");
                keyPostMap.put("id",serverCaseBlockEntity.mode+"");
                lock = HttpUtil.post(ItemSystemData.httpServer + "/servercase/lock", keyPostMap);
            }catch (HttpException ignored){
            }finally {
                logger.debug("Lock ServerData Finish.");
            }

            String ItemDataStr;

            ArrayList<String> itemArrayList = new ArrayList<>();

            for(ItemStack itemStack : serverCaseBlockEntity.items){
                itemArrayList.add(itemStack.writeNbt(new NbtCompound()).asString());
            }



            JSONArray itemJSON = JSONUtil.parseArray(itemArrayList);

            JSONObject jsonObject = JSONUtil.createObj();

            HashMap<String, JSONArray> ItemDataMap = new HashMap<>();

            ItemDataMap.put("item",itemJSON);

            jsonObject.putAll(ItemDataMap);

            ItemDataStr = jsonObject.toString();

            HashMap<String, Object> paramMap = new HashMap<>();

            paramMap.put("SNBT",ItemDataStr);
            paramMap.put("group",ItemSystemData.itemGroup);
            paramMap.put("UUID",serverCaseBlockEntity.UUID);
            paramMap.put("id",serverCaseBlockEntity.mode+"");

            String result = null;
            try {
                result = HttpUtil.post(ItemSystemData.httpServer + "/servercase/upload", paramMap);
            } catch (HttpException e) {
                e.printStackTrace();
            } finally {
                if (result == null || !result.equals("get"))
                    logger.info("Server Connect Fail.");
                else {
                    serverCaseBlockEntity.items.clear();
                    logger.info("ServerCase UUID is:" + serverCaseBlockEntity.UUID + " ID is:" + serverCaseBlockEntity.mode + " Data is uploading to ServerCore Finish.");
                }
                try{
                    HashMap<String,Object> keyPostMap = new HashMap<>();
                    keyPostMap.put("key","set");
                    keyPostMap.put("group",ItemSystemData.itemGroup);
                    keyPostMap.put("UUID",serverCaseBlockEntity.UUID);
                    keyPostMap.put("value","false");
                    keyPostMap.put("id",serverCaseBlockEntity.mode+"");
                    lock = HttpUtil.post(ItemSystemData.httpServer + "/servercase/lock", keyPostMap);
                }catch (HttpException ignored){
                }finally {
                    logger.info("Unlock ServerData Finish.");
                }
            }

        });
    }
    public static void download(World world, BlockPos pos, BlockState state, ServerCaseBlockEntity ei,ServerCaseBlockEntity serverCaseBlockEntity){
        int downloadID = serverCaseBlockEntity.mode-12;
        ThreadUtil.execute(()->{
            boolean back =false;
            for(ItemStack stack:serverCaseBlockEntity.items){
                if(!stack.isEmpty()){
                    back=true;
                    break;
                }
            }
            if (back){
                return;
            }

            String use=null;
            try{
                HashMap<String,Object> keyPostMap = new HashMap<>();
                keyPostMap.put("UUID",serverCaseBlockEntity.UUID);
                keyPostMap.put("id",downloadID+"");
                use = HttpUtil.post(ItemSystemData.httpServer + "/servercase/use", keyPostMap);
            }catch (HttpException ignored){}
            if(use==null){
                return;
            }else if(use.equals("true")){
                return;
            }

            int index=0;
            while (index<10){
                String lock="error";
                try {
                    HashMap<String, Object> keyPostMap = new HashMap<>();
                    keyPostMap.put("key", "get");
                    keyPostMap.put("group",ItemSystemData.itemGroup);
                    keyPostMap.put("UUID", serverCaseBlockEntity.UUID);
                    keyPostMap.put("id",downloadID+"");
                    lock = HttpUtil.post(ItemSystemData.httpServer + "/servercase/lock", keyPostMap);
                } catch (HttpException e) {
                    lock = "error";
                }

                if(lock.equals("false")){
                    break;
                }else if(lock.equals("error")){
                    return;
                }else if(lock.equals("null")){
                    return;
                }else if(lock.equals("true")){
                    logger.debug("ServerCase UUID is:"+serverCaseBlockEntity.UUID+" id is:"+(serverCaseBlockEntity.mode-12)+" is lock,Try again in 0.2 seconds.");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {}
                }
                index++;
            }
            if(index>=30){
                return;
            }
            logger.debug("ServerCase UUID is:" + serverCaseBlockEntity.UUID + " ID is:" + (serverCaseBlockEntity.mode-12) + " Data is downloading from ServerCore.");

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("group",ItemSystemData.itemGroup);
            paramMap.put("UUID", serverCaseBlockEntity.UUID);
            paramMap.put("id",downloadID+"");
            String result = null;
            try {
                result = HttpUtil.post(ItemSystemData.httpServer + "/servercase/download", paramMap);
            } catch (HttpException e) {
                e.printStackTrace();
                return;
            }
            if (result.equals("non")) {
                return;
            }
            JSONObject ItemDataJSON = JSONUtil.parseObj(result);

            JSONArray itemJSON = ItemDataJSON.getJSONArray("item");

            List<String> itemArraylist = JSONUtil.toList(itemJSON, String.class);

            for (int i = 0; i < itemArraylist.size(); i++) {
                try {
                    serverCaseBlockEntity.items.set(i, ItemStack.fromNbt(StringNbtReader.parse(itemArraylist.get(i))));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }

            logger.info("ServerCase ID is "+ (serverCaseBlockEntity.mode-12)+" UUID is"+ serverCaseBlockEntity.UUID+" Data Download Finished.");

        });

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        UUID = nbt.getString("UUID");
        mode = nbt.getInt("mode");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        nbt.putString("UUID",UUID);
        nbt.putInt("mode",mode);
        super.writeNbt(nbt);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

}

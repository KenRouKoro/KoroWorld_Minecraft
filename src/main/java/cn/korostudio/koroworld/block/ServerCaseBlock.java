package cn.korostudio.koroworld.block;

import cn.korostudio.koroworld.KoroWorldMain;
import cn.korostudio.koroworld.KoroWorldServer;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ServerCaseBlock extends BlockWithEntity implements BlockEntityProvider {
    public ServerCaseBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient){
            return super.onUse(state, world, pos, player, hand, hit);
        }
        if(!player.getStackInHand(hand).isEmpty()){
            if(hand == player.getActiveHand()){
                ServerCaseBlockEntity serverCaseBlockEntity = (ServerCaseBlockEntity) world.getBlockEntity(pos);
                String backStr;
                if(serverCaseBlockEntity.getMode()==0){
                    backStr = "状态：关闭";
                }else if(serverCaseBlockEntity.getMode()<13){
                    backStr = "状态：发送 信道："+serverCaseBlockEntity.getMode();
                }else{
                    backStr = "状态：接收 信道："+(serverCaseBlockEntity.getMode()-12);
                }
                player.sendMessage(new LiteralText(backStr),true);
            }
            return super.onUse(state, world, pos, player, hand, hit);
        }
        if(hand != player.getActiveHand()){
            return super.onUse(state, world, pos, player, hand, hit);
        }

        ServerCaseBlockEntity serverCaseBlockEntity = (ServerCaseBlockEntity) world.getBlockEntity(pos);
        String backStr;

        serverCaseBlockEntity.setMode((serverCaseBlockEntity.getMode()+1)>24?0: (serverCaseBlockEntity.getMode()+1));
        serverCaseBlockEntity.setUUID(player.getUuidAsString());

        serverCaseBlockEntity.index=0;

        if(serverCaseBlockEntity.getMode()==0){
            backStr = "状态：关闭";
        }else if(serverCaseBlockEntity.getMode()<13){
            backStr = "状态：发送 信道："+serverCaseBlockEntity.getMode();
        }else{
            backStr = "状态：接收 信道："+(serverCaseBlockEntity.getMode()-12);
        }

        player.sendMessage(new LiteralText(backStr),true);

        return super.onUse(state, world, pos, player, hand, hit);

    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ServerCaseBlockEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, KoroWorldMain.SEVER_CASE_BLOCK_ENTITY, ServerCaseBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

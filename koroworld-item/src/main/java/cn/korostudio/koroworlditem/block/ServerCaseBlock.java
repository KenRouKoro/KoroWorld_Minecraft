package cn.korostudio.koroworlditem.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
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

public class ServerCaseBlock extends Block  implements BlockEntityProvider {

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
        return (level1, pos, state1, tile) -> {
            if (tile instanceof ServerCaseBlockEntity generator) {
                ServerCaseBlockEntity.tick(level1,pos,state1,generator);
            }
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

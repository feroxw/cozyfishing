package net.mazee.cozyfishing.block.custom;

import net.mazee.cozyfishing.block.entity.ModBlockEntities;
import net.mazee.cozyfishing.block.entity.SDVFishingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SDVFishingBlock extends BaseEntityBlock {
    //public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SDVFishingBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    private static final VoxelShape SHAPE =
            //Block.box(2, 0, 2, 2, 3, 2);
            Block.box(2, 0, 2, 14, 14, 14);

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

//    @Override
//    public BlockState getStateForPlacement() {
//        return this.defaultBlockState();
//    }

//    @Override
//    public BlockState rotate(BlockState pState, Rotation pRotation) {
//        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
//    }
//
//    @Override
//    public BlockState mirror(BlockState pState, Mirror pMirror) {
//        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
//    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SDVFishingEntity) {
                ((SDVFishingEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            if(pPlayer.getItemInHand(pHand).getItem() == Items.MILK_BUCKET){
                pLevel.playSound(null, pPos, SoundEvents.AXOLOTL_IDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);

                ItemStack bucketStack = new ItemStack(Items.BUCKET);
                pPlayer.setItemInHand(pHand, bucketStack);

                BlockEntity entity = pLevel.getBlockEntity(pPos);
                if(entity instanceof SDVFishingEntity) {
                   //((SDVFishingEntity) entity).setMilkRemaining(Math.min( ((SDVFishingEntity) entity).getMilkRemaining() + 300 , 900));
                    //System.out.println(((SDVFishingBlockEntity) entity).getMilkRemaining());
                }

            }else {
                BlockEntity entity = pLevel.getBlockEntity(pPos);
                if(entity instanceof SDVFishingEntity) {
                    NetworkHooks.openScreen(((ServerPlayer)pPlayer), (SDVFishingEntity)entity, pPos);
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }



        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SDVFishingEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.SDV_FISHING_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1, pBlockEntity));
    }
}

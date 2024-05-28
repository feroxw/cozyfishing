package net.mazee.cozyfishing.block.entity;

import net.mazee.cozyfishing.block.custom.SDVFishingBlock;
import net.mazee.cozyfishing.item.ModItems;
import net.mazee.cozyfishing.screen.SDVFishingMenu;
import net.mazee.cozyfishing.utils.KeyBinding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SDVFishingEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(10){
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 7);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2, 3, 4, 5 -> true;

                case 7 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    public ItemStack getRenderStack() {
        ItemStack stack;

        if(!itemHandler.getStackInSlot(7).isEmpty()) {
            stack = itemHandler.getStackInSlot(7);
            //System.out.println(stack);
        } else {
            stack = itemHandler.getStackInSlot(8);
        }

        return stack;
    }

    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private float progress = 80;
    private float maxProgress = 400;
    private float bobberOffset = 50;
    private int bobberOffsetMax = 149 - 42;
    private float bobberVelocity = 0;
    private float bobberAcceleration = 1;
    private int bobberSize = 42;
    private float fishOffset = 70;
    private int fishOffsetMax = 149 - 15;
    private float fishVelocity = 0;
    private float fishAcceleration = 0.1F;
    private int fishSize = 42;
    private float fishVelocityMax = 2;




    public SDVFishingEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SDV_FISHING_ENTITY.get(), pPos, pBlockState);
    }

    public float getProgress(){return this.progress;}
    public float getProgressMax(){return this.maxProgress;}
    public float getBobberOffset(){return this.bobberOffset;}
    public float getFishOffset(){return this.fishOffset;}
    public float getFishVelocity(){return this.fishVelocity;}
    public float getFishAcceleration(){return this.fishAcceleration;}


//    @Nullable
//    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
//        return new SDVFishingMenu(pContainerId, pPlayerInventory, this, this.data);
//    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
//        nbt.putInt("sdv_fishing_block.progress", this.progress);
//        nbt.putInt("sdv_fishing_block.fuelRemaining", this.bobberOffset);
//        nbt.putInt("sdv_fishing_block.milkRemaining", this.milkRemaining);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
//        progress = nbt.getInt("sdv_fishing_block.progress");
//        bobberOffset = nbt.getInt("sdv_fishing_block.fuelRemaining");
//        milkRemaining = nbt.getInt("sdv_fishing_block.milkRemaining");
    }

    public void drops() {

    }

    public boolean isReeling(){
        //System.out.println(KeyBinding.REEL_IN.isPressed());
        //return KeyBinding.REEL_IN.isPressed();
        return KeyBinding.shiftDown();
        //return false;
    }

    public boolean isTrackingFish(){

        return this.bobberOffset <= this.fishOffset && (this.bobberOffset + this.bobberSize) >= this.fishOffset;
    }


    public void tick(Level level, BlockPos pos, BlockState state, SDVFishingEntity pEntity) {
        if(level.isClientSide()) {
            return;
        }
        //System.out.println(pEntity.fishOffset);
        if(3 > 2) {
            if(pEntity.isTrackingFish()){
//                if(pEntity.itemHandler.getStackInSlot(6).getCount() > 0){
//                    pEntity.itemHandler.extractItem(6, 1, false);
//                    //pEntity.bobberOffset = 450;
//                }
                pEntity.progress = Math.min(pEntity.progress+1, pEntity.maxProgress);

            }else if(pEntity.getProgress() > 0){
                pEntity.progress -= 1;

            }else{
                //close menu
            }


            if(pEntity.isReeling()){
                pEntity.bobberOffset = Math.max(pEntity.bobberOffset-2, 0);

            }else if(pEntity.bobberOffset < pEntity.bobberOffsetMax){
                //System.out.println("falling");
                pEntity.bobberOffset = Math.min(pEntity.bobberOffset+2, pEntity.bobberOffsetMax);

            }else{
                //close menu
            }


            if(Math.random()*100 < 10){
                pEntity.fishAcceleration = (float)(Math.random()*1/5 - 0.1);
//                System.out.println("Fish Accel");
//                System.out.println(pEntity.fishAcceleration);
            }
            pEntity.fishVelocity += pEntity.fishAcceleration;
            pEntity.fishVelocity = Math.min(pEntity.fishVelocityMax, Math.max(pEntity.fishAcceleration + pEntity.fishVelocity, -pEntity.fishVelocityMax));

            pEntity.fishOffset = Math.min(pEntity.fishOffsetMax, Math.max(pEntity.fishOffset + pEntity.fishVelocity, 0));
//            System.out.println("Fish Velocity");
//            System.out.println(pEntity.fishVelocity);
//            System.out.println("Fish Offset");
//            System.out.println(pEntity.fishOffset);

            setChanged(level, pos, state);

        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }

    }


    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem(SDVFishingEntity pEntity) {
        Level level = pEntity.level;

        if(!level.isClientSide()){
            level.playSound(null, pEntity.getBlockPos(), SoundEvents.NOTE_BLOCK_XYLOPHONE.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
        }

        pEntity.itemHandler.extractItem(0, 1, false);
        pEntity.itemHandler.extractItem(1, 1, false);
        pEntity.itemHandler.extractItem(2, 1, false);
        pEntity.itemHandler.extractItem(3, 1, false);
        pEntity.itemHandler.extractItem(4, 1, false);
        pEntity.itemHandler.extractItem(5, 1, false);
        pEntity.itemHandler.setStackInSlot(7, new ItemStack(Items.ALLIUM,
                pEntity.itemHandler.getStackInSlot(7).getCount() + 1));

        pEntity.resetProgress();

    }


}

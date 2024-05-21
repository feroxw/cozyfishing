package net.mazee.cozyfishing.screen;

import net.mazee.cozyfishing.block.ModBlocks;
import net.mazee.cozyfishing.block.custom.SDVFishingBlock;
import net.mazee.cozyfishing.block.entity.SDVFishingEntity;
import net.mazee.cozyfishing.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

public class SDVFishingMenu extends AbstractContainerMenu {
    public final SDVFishingEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public SDVFishingMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(12));
    }

    public SDVFishingMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.SDV_FISHING_MENU.get(), id);
        checkContainerSize(inv, 8);
        blockEntity = (SDVFishingEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        //this.fluidStack = blockEntity.getFluidStack();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

//        if(blockEntity.getBlockState().getValue(SDVFishingBlock.LEVEL) == 0){
//
//        }

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 15, 22));//Ingredient Slot
            this.addSlot(new SlotItemHandler(handler, 1, 33, 22));//Ingredient Slot
            this.addSlot(new SlotItemHandler(handler, 2, 51, 22));//Ingredient Slot
            this.addSlot(new SlotItemHandler(handler, 3, 15, 40));//Ingredient Slot
            this.addSlot(new SlotItemHandler(handler, 4, 33, 40));//Ingredient Slot
            this.addSlot(new SlotItemHandler(handler, 5, 51, 40));//Ingredient Slot
            this.addSlot(new SlotItemHandler(handler, 6, 147, 15));//Spoons
            this.addSlot(new SlotItemHandler(handler, 7, 117, 30));//Completed
            //this.addSlot(new SlotItemHandler(handler, 8, 138, 60));//Glass Bottles
            //this.addSlot(new SlotItemHandler(handler, 9, 119, 60));//Final Output
        });

        this.addDataSlots(data);
    }

    public SDVFishingEntity getBlockEntity() {
        return this.blockEntity;
    }


    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 149; // This is the height in pixels of the bar
        //System.out.println(progress);
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getBobberOffset() {
        int bobberOffset = this.data.get(2);
        int bobberOffsetMax = this.data.get(3);
        int bobberOffsetBarSize = this.data.get(6);
        //System.out.println(bobberOffset);
        //return bobberOffsetMax != 0 && bobberOffset != 0 ? bobberOffset * bobberOffsetBarSize / bobberOffsetMax : 0;
        return bobberOffset;
    }

    public int getFishOffset() {
        return this.data.get(7);
    }




    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 0;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 0;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 0;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 8;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexIngredientsStart = TE_INVENTORY_FIRST_SLOT_INDEX;
        int indexIngredientsEnd = indexIngredientsStart + 5;
        int indexFuel = indexIngredientsEnd + 1;
        int indexDoneNotGrabbable = indexFuel + 1;
        int indexVessel = indexDoneNotGrabbable + 1;
        int indexResult = indexVessel + 1;

        //playerIn.sendSystemMessage(Component.literal("Your Index is" + index));

        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < indexIngredientsStart) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            //if(sourceStack.getItem() == Items.GLASS_BOTTLE){
                //if (!moveItemStackTo(sourceStack, indexVessel, indexVessel + 1, false)) {
                    //return ItemStack.EMPTY;  // EMPTY_ITEM
                //}
            //}else
            if(sourceStack.getItem() == Items.ACACIA_PLANKS){
                if (!moveItemStackTo(sourceStack, indexFuel, indexFuel + 1, false)) {
                    return ItemStack.EMPTY;  // EMPTY_ITEM
                }
            }else {
                if (!moveItemStackTo(sourceStack, indexIngredientsStart, indexIngredientsEnd, false)) {
                    return ItemStack.EMPTY;  // EMPTY_ITEM
                }
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack down into the player's inventory
            if(index == indexDoneNotGrabbable){
                //If this is the crafted item, try to put it in the player's hotbar (Reverse direction true)
                if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            } else{
                if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.SDV_FISHING_BLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                //this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            //this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}

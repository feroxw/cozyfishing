package net.mazee.cozyfishing.network;

import net.mazee.cozyfishing.data.Fish;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Map;

public class FishingMinigameResultPacket {

    public Player player;
    public int fishIndex;
    //public Vec3 hookPos;

    public FishingMinigameResultPacket(Player player) {this.player = player;}
    public FishingMinigameResultPacket() {}
    public FishingMinigameResultPacket(int fishIndex) {this.fishIndex = fishIndex;}
    public FishingMinigameResultPacket(Vec3 hookPos) {}
    public FishingMinigameResultPacket(FriendlyByteBuf buffer) {}
    public void encode(FriendlyByteBuf buffer) {}

//    public void handle(NetworkEvent.ClientCustomPayloadLoginEvent.Context context) {
//        ServerPlayer player = context.getSender();
//
//    }

    public void handle(NetworkEvent.Context ctx, int fishIndex) {

        ServerPlayer player = ctx.getSender();

        System.out.println("RESULT PACKET SENT");

        if(fishIndex >= 0){
            ServerLevel level = (ServerLevel) player.level();
            if(level instanceof ServerLevel){
                System.out.print("SERVERLEVEL");
            }
            Vec3 hookPos = player.fishing.position();

            Map<String, ItemLike> test = (Map<String, ItemLike>) Fish.listOfFish.get(fishIndex).get("registry");

            ItemStack caughtFish = new ItemStack((ItemLike) test.get("fishRegistry"));

            ItemStack itemstack = caughtFish;

            if(fishIndex < 0){
                itemstack = (ItemStack) new ItemStack(Items.EMERALD);
            }
            ItemEntity itementity = new ItemEntity(level, hookPos.x(), hookPos.y(), hookPos.z(), itemstack);
            double d0 = player.getX() - hookPos.x();
            double d1 = player.getY() - hookPos.y();
            double d2 = player.getZ() - hookPos.z();
            double d3 = 0.1;
            itementity.setDeltaMovement(d0 * 0.1, d1 * 0.1 + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08, d2 * 0.1);
            level.addFreshEntity(itementity);
        }



        player.fishing.discard();

    }

//    public Vec3 hookPos() {
//        return this.hookPos;
//    }
}

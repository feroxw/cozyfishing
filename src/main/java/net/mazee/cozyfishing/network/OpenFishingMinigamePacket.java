package net.mazee.cozyfishing.network;

import net.mazee.cozyfishing.screen.SDVFishingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class OpenFishingMinigamePacket {
    public OpenFishingMinigamePacket() {}
    public OpenFishingMinigamePacket(FriendlyByteBuf buffer) {}
    public void encode(FriendlyByteBuf buffer) {}

    public void handle(NetworkEvent.ClientCustomPayloadLoginEvent.Context context) {
        ServerPlayer player = context.getSender();

    }

    public void handle() {
        System.out.println("PACKET SENT");
        Minecraft.getInstance().setScreen(new SDVFishingScreen(Component.literal("FISHING"), Minecraft.getInstance().player));
    }
}

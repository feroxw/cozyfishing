package net.mazee.cozyfishing.network;

import net.mazee.cozyfishing.CozyFishing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {
    public static SimpleChannel INSTANCE;
    private static final String PROTOCOL_VERSION = "3.2.1";

    public static void register() {
        SimpleChannel net = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(CozyFishing.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);
//        INSTANCE.messageBuilder(OpenFishingMinigamePacket.class, 15, NetworkDirection.PLAY_TO_SERVER)
//                .encoder(OpenFishingMinigamePacket::encode)
//                .decoder(OpenFishingMinigamePacket::new)
//                .consumerMainThread(OpenFishingMinigamePacket::handle)
//                .add();

        INSTANCE = net;

        INSTANCE.registerMessage(
                15,
                OpenFishingMinigamePacket.class,
                (msg, pb) -> {
//                    pb.writeFloat(msg.catchChance());
//                    pb.writeDouble(msg.bobberPos().x());
//                    pb.writeDouble(msg.bobberPos().y());
//                    pb.writeDouble(msg.bobberPos().z());
                },
                pb -> new OpenFishingMinigamePacket(),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(msg::handle);
                    ctx.get().setPacketHandled(true);
                });

        INSTANCE.registerMessage(
                16,
                FishingMinigameResultPacket.class,
                (msg, pb) -> {
                    pb.writeInt(msg.fishIndex);
//                    pb.writeDouble(msg.hookPos.x());
//                    pb.writeDouble(msg.hookPos.y());
//                    pb.writeDouble(msg.hookPos.z());
                },
                pb -> new FishingMinigameResultPacket(pb.readInt()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> msg.handle(ctx.get(), msg.fishIndex));
                    ctx.get().setPacketHandled(true);
                });
    }

//    public static <MSG> void sendToServer(MSG message) {
//        INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
//    }
//
//    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
//        INSTANCE.send(PacketDistributor.PLAYER.with((Supplier<ServerPlayer>) player), message);
//    }

//    public static void sendToServer(Object msg) {
//        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
//    }
//
//    public static void sendToPlayer(Object msg) {
//        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
//    }
//
//    public static void sendToAllClients(Object msg) {
//        INSTANCE.send(msg, PacketDistributor.ALL.noArg());
//    }
}

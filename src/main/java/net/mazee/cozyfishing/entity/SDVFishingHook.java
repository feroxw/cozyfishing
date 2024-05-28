package net.mazee.cozyfishing.entity;

import com.mojang.logging.LogUtils;
import net.mazee.cozyfishing.block.custom.SDVFishingBlock;
import net.mazee.cozyfishing.item.ModItems;
import net.mazee.cozyfishing.network.OpenFishingMinigamePacket;
import net.mazee.cozyfishing.screen.SDVFishingScreen;
import net.mazee.cozyfishing.sound.ModSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import net.mazee.cozyfishing.network.PacketHandler;
import net.mazee.cozyfishing.network.OpenFishingMinigamePacket;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class SDVFishingHook extends FishingHook implements IEntityAdditionalSpawnData {

    public Item FISH_CAUGHT = ModItems.CATFISH.get();

    public SDVFishingHook(Player player, Level world, int luck, int lureSpeed) {
        super(player, world, luck, lureSpeed);
        player.fishing = this;
        //System.out.println("New Hook real constructor");
    }

    public SDVFishingHook(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(level.getPlayerByUUID(spawnEntity.getAdditionalData().readUUID()), level, 0, 0);
        //System.out.println("New Hook weird constructor");
    }

    public Item getFishCaught(){
        return this.FISH_CAUGHT;
    }

    @Override
    public int retrieve(ItemStack pStack) {
        Player player = this.getPlayerOwner();
        Boolean playingMinigame = false;
        if (!this.level().isClientSide && player != null && !this.shouldStopFishing(player)) {
            int i = 0;
            ItemFishedEvent event = null;
            if (this.hookedIn != null) {
                this.pullEntity(this.hookedIn);
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, pStack, this, Collections.emptyList());
                this.level().broadcastEntityEvent(this, (byte)31);
                i = this.hookedIn instanceof ItemEntity ? 3 : 5;
            } else if (this.nibble > 0) {
                LootParams lootparams = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, pStack).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.KILLER_ENTITY, this.getOwner()).withParameter(LootContextParams.THIS_ENTITY, this).withLuck((float)0 + player.getLuck()).create(LootContextParamSets.FISHING);
                LootTable loottable = this.level().getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
                List<ItemStack> list = loottable.getRandomItems(lootparams);
                event = new ItemFishedEvent(list, this.onGround() ? 2 : 1, this);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    this.discard();
                    return event.getRodDamage();
                }

                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, pStack, this, list);
                Iterator var8 = list.iterator();


                if(player instanceof ServerPlayer){
                    System.out.println("SENT TO CLIENT");
                    this.playSound(ModSounds.SOUND_FISH_HIT.get(), 0.5F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    playingMinigame = true;
                    PacketHandler.INSTANCE.sendTo(new OpenFishingMinigamePacket(), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }


                i = 1;
            }


            if (this.onGround()) {
                i = 2;
            }
            if(!playingMinigame){
                this.discard();
            }

            return event == null ? i : event.getRodDamage();
        } else {
            return 0;
        }
    }

    @Override
    protected void catchingFish(BlockPos pPos) {
        ServerLevel serverlevel = (ServerLevel)this.level();
        int i = 1;
        BlockPos blockpos = pPos.above();
        if (this.random.nextFloat() < 0.25F && this.level().isRainingAt(blockpos)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(blockpos)) {
            --i;
        }

        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(DATA_BITING, false);
            }
        } else {
            float f5;
            float f6;
            float f7;
            double d4;
            double d5;
            double d6;
            BlockState blockstate1;
            if (this.timeUntilHooked > 0) {
                this.timeUntilHooked -= i;
                if (this.timeUntilHooked > 0) {
                    this.fishAngle += (float)this.random.triangle(0.0, 9.188);
                    f5 = this.fishAngle * 0.017453292F;
                    f6 = Mth.sin(f5);
                    f7 = Mth.cos(f5);
                    d4 = this.getX() + (double)(f6 * (float)this.timeUntilHooked * 0.1F);
                    d5 = (double)((float)Mth.floor(this.getY()) + 1.0F);
                    d6 = this.getZ() + (double)(f7 * (float)this.timeUntilHooked * 0.1F);
                    blockstate1 = serverlevel.getBlockState(BlockPos.containing(d4, d5 - 1.0, d6));
                    if (blockstate1.is(Blocks.WATER)) {
                        if (this.random.nextFloat() < 0.15F) {
                            serverlevel.sendParticles(ParticleTypes.BUBBLE, d4, d5 - 0.10000000149011612, d6, 1, (double)f6, 0.1, (double)f7, 0.0);
                        }

                        float f3 = f6 * 0.04F;
                        float f4 = f7 * 0.04F;
                        serverlevel.sendParticles(ParticleTypes.FISHING, d4, d5, d6, 0, (double)f4, 0.01, (double)(-f3), 1.0);
                        serverlevel.sendParticles(ParticleTypes.FISHING, d4, d5, d6, 0, (double)(-f4), 0.01, (double)f3, 1.0);
                    }
                } else {
                    this.playSound(ModSounds.SOUND_FISH_BITE.get(), 0.5F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.getY() + 0.5;
                    serverlevel.sendParticles(ParticleTypes.BUBBLE, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0, (double)this.getBbWidth(), 0.20000000298023224);
                    serverlevel.sendParticles(ParticleTypes.FISHING, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0, (double)this.getBbWidth(), 0.20000000298023224);
                    this.nibble = Mth.nextInt(this.random, 20, 40);
                    this.getEntityData().set(DATA_BITING, true);
                }
            } else if (this.timeUntilLured > 0) {
                this.timeUntilLured -= i;
                f5 = 0.15F;
                if (this.timeUntilLured < 20) {
                    f5 += (float)(20 - this.timeUntilLured) * 0.05F;
                } else if (this.timeUntilLured < 40) {
                    f5 += (float)(40 - this.timeUntilLured) * 0.02F;
                } else if (this.timeUntilLured < 60) {
                    f5 += (float)(60 - this.timeUntilLured) * 0.01F;
                }

                if (this.random.nextFloat() < f5) {
                    f6 = Mth.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f7 = Mth.nextFloat(this.random, 25.0F, 60.0F);
                    d4 = this.getX() + (double)(Mth.sin(f6) * f7) * 0.1;
                    d5 = (double)((float)Mth.floor(this.getY()) + 1.0F);
                    d6 = this.getZ() + (double)(Mth.cos(f6) * f7) * 0.1;
                    blockstate1 = serverlevel.getBlockState(BlockPos.containing(d4, d5 - 1.0, d6));
                    if (blockstate1.is(Blocks.WATER)) {
                        serverlevel.sendParticles(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
                    }
                }

                if (this.timeUntilLured <= 0) {
                    this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
                    this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
                }
            } else {
                this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
                this.timeUntilLured -= this.lureSpeed * 20 * 5;
            }
        }

    }

    @Override
    @Nonnull
    public EntityType<?> getType() {
        return ModEntities.SDV_FISHING_HOOK.get();
    }

    @Override
    @Nonnull
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            buffer.writeUUID(player.getUUID());
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {

    }
}
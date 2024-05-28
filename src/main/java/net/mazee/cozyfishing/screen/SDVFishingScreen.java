package net.mazee.cozyfishing.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mazee.cozyfishing.CozyFishing;
import net.mazee.cozyfishing.block.entity.SDVFishingEntity;
import net.mazee.cozyfishing.data.Fish;
import net.mazee.cozyfishing.network.FishingMinigameResultPacket;
import net.mazee.cozyfishing.network.OpenFishingMinigamePacket;
import net.mazee.cozyfishing.network.PacketHandler;
import net.mazee.cozyfishing.sound.ModSounds;
import net.mazee.cozyfishing.utils.KeyBinding;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkDirection;
import org.joml.Matrix4f;

import java.util.*;

public class SDVFishingScreen extends Screen {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CozyFishing.MODID,"textures/gui/fishing_gui.png");

    private float progress = 80;
    private float maxProgress = 149;
    private final float trackSize = 149;
    private float bobberOffset = 50;
    private float bobberSize = 32;
    private final float bobberOffsetMax = this.trackSize - this.bobberSize;
    private float bobberVelocity = 0;
    private float bobberAcceleration = 1;
    private float fishOffset = 70;
    private float fishSize = 15;
    private float fishOffsetMax = this.trackSize - this.fishSize;
    private float fishVelocity = 0;
    private float fishAcceleration = 0.1F;
    private float fishTargetOffset = -1F;
    private float fishVelocityMax = 2;
    private float floaterSinkerAcceleration = 0;
    private int difficulty = 100;
    private String motionType = "mixed";
    private String fishName = "Cod";
    private int fishIndex = 0;

    private int winningTicks = 10;
    private boolean wonGame = false;
    private int soundTicksSlowReel = 0;
    private int soundTicksFastReel = 0;

    private Player player;

    public SDVFishingScreen(Component pTitle, Player pPlayer) {
        super(pTitle);
        this.player = pPlayer;
    }

    @Override
    protected void init() {
        super.init();
        fishOffset = fishOffsetMax - 20;
        bobberOffset = bobberOffsetMax;
        progress = 10;
        difficulty = 50;
        motionType = "smooth";

        //System.out.println(player.level().isRaining());
        //System.out.println(Fish.listOfFish.size());
        //System.out.println(player.level().isRainingAt(player.fishing.getOnPos()));
        //System.out.println(player.level().getBiome(player.fishing.getOnPos()));

        //Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        Holder<Biome> biome = player.level().getBiome(player.fishing.getOnPos());

        List<Map<String, Object>> filteredFish = new ArrayList<>();

        for (Map<String, Object> element : Fish.listOfFish) {
            boolean isRightWeather = true;
            if(player.level().isRaining() == (boolean) element.get("raining")){
                isRightWeather = true;
            }
            boolean isRightTime = false;
            long currentTime = (player.level().getDayTime())%24000;

//            System.out.println((long)(element.get("minTime")));
//            System.out.println(currentTime);
//            System.out.println((long)(element.get("maxTime")));
            if((long)(element.get("minTime")) <= currentTime && (long)(element.get("maxTime")) >= currentTime){
                //System.out.println("right time");
                isRightTime = true;
            }

            boolean isRightBiome = false;
            ArrayList<TagKey<Biome>> biomeList = (ArrayList<TagKey<Biome>>) element.get("biome_tags");

            for (TagKey<Biome> tag : biomeList) {
//                System.out.println(tag);
//                System.out.println(biome);
                if(biome.is(tag)){
                    //System.out.println("right biome");
                    isRightBiome = true;
                }
            }

            if(isRightBiome && isRightWeather && isRightTime){
                System.out.println(element.get("name"));
                filteredFish.add(element);
            }
        }
        System.out.println(filteredFish.size());

        Random random = new Random();
        int r = random.nextInt(filteredFish.size());
        this.fishIndex = (int) filteredFish.get(r).get("id");
        Map<String, Object> fish = Fish.listOfFish.get(this.fishIndex);
        System.out.println(r);
        System.out.println(this.fishIndex);
        this.fishName = (String) fish.get("name");
        this.difficulty = (int) fish.get("difficulty");
        this.motionType = (String) fish.get("motionType");


    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - 15) / 2 + 40;
        int y = (height - 168) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, 115, 168);
        //System.out.println(Minecraft.getInstance().getWindow().getGuiScale());
        renderProgressBar(guiGraphics, (float)x, (float)y);
        renderBobber(guiGraphics, (float)x, (float)y);
        renderFish(guiGraphics, (float)x, (float)y);

        guiGraphics.drawString(this.font, String.valueOf(getScaledProgress()), 10, 10, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(getBobberOffset()), 10, 30, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(getFishOffset()), 10, 50, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(this.fishVelocity), 10, 70, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(this.fishAcceleration), 10, 90, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(this.fishTargetOffset), 70, 50, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(this.fishName), 10, 110, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(this.difficulty), 10, 130, 0, false);
        guiGraphics.drawString(this.font, String.valueOf(this.motionType), 10, 150, 0, false);

    }

    private void renderProgressBar(GuiGraphics guiGraphics, float x, float y) {
            customBlit(guiGraphics, TEXTURE, x + 72, y + 9 + (149 - getScaledProgress()), 129, 149 - getScaledProgress(), 5, getScaledProgress());
    }

    private void renderBobber(GuiGraphics guiGraphics, float x, float y) {
        if(isTrackingFish()){
            customBlit(guiGraphics, TEXTURE, x + 41, y + 9 + this.bobberOffset, 115, 76, 11, bobberSize);
        }else{
            customBlit(guiGraphics, TEXTURE, x + 41, y + 9 + this.bobberOffset, 115, 0, 11, bobberSize);
        }
    }

    private void renderFish(GuiGraphics guiGraphics, float x, float y) {
        customBlit(guiGraphics, TEXTURE, x + 39, y + 9 + getFishOffset(), 138, 0, 16, 16);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    public void customBlit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, float pX, float pY, float pUOffset, float pVOffset, float pUWidth, float pVHeight) {
        this.blit(guiGraphics, pAtlasLocation, pX, pY, 0, (float)pUOffset, (float)pVOffset, pUWidth, pVHeight, 256, 256);
    }

    public void blit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, float pX, float pY, float pBlitOffset, float pUOffset, float pVOffset, float pUWidth, float pVHeight, float pTextureWidth, float pTextureHeight) {
        this.blit(guiGraphics, pAtlasLocation, pX, pX + pUWidth, pY, pY + pVHeight, pBlitOffset, pUWidth, pVHeight, pUOffset, pVOffset, pTextureWidth, pTextureHeight);
    }
    void blit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pUWidth, float pVHeight, float pUOffset, float pVOffset, float pTextureWidth, float pTextureHeight) {
        this.floatedBlit(guiGraphics, pAtlasLocation, pX1, pX2, pY1, pY2, pBlitOffset, (pUOffset + 0.0F) / (float)pTextureWidth, (pUOffset + (float)pUWidth) / (float)pTextureWidth, (pVOffset + 0.0F) / (float)pTextureHeight, (pVOffset + (float)pVHeight) / (float)pTextureHeight);
    }
    void floatedBlit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, float pX1, float pX2, float pY1, float pY2, float pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        RenderSystem.setShaderTexture(0, pAtlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, (float)pX1, (float)pY1, (float)pBlitOffset).uv(pMinU, pMinV).endVertex();
        bufferbuilder.vertex(matrix4f, (float)pX1, (float)pY2, (float)pBlitOffset).uv(pMinU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float)pX2, (float)pY2, (float)pBlitOffset).uv(pMaxU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float)pX2, (float)pY1, (float)pBlitOffset).uv(pMaxU, pMinV).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }


    public float getScaledProgress() {
        float progress = this.progress;
        float maxProgress = this.maxProgress;  // Max Progress
        float progressArrowSize = 149;

        return  maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public float getBobberOffset(){
        return this.bobberOffset;
    }

    public float getFishOffset(){
        return this.fishOffset;
    }

    public boolean isReeling(){
        //System.out.println(KeyBinding.REEL_IN.isPressed());
        //return KeyBinding.REEL_IN.isPressed();
        //return KeyBinding.shiftDown() || KeyBinding.isKeyDown(32);
        return KeyBinding.isKeyDown(32);
    }

    public boolean isTrackingFish(){

        return this.bobberOffset <= (this.fishOffset + this.fishSize) && (this.bobberOffset + this.bobberSize) >= this.fishOffset;
    }

    @Override
    public void tick() {

        this.soundTicksSlowReel--;
        this.soundTicksFastReel--;

        //Always clientside

        if(this.winningTicks < 1){
            System.out.println(this.fishIndex);
            PacketHandler.INSTANCE.sendToServer(new FishingMinigameResultPacket(this.fishIndex));

            this.onClose();
        }

        if(this.wonGame){
            this.winningTicks--;
            return;
        }

        if(this.progress >= this.maxProgress){
            this.player.level().playLocalSound(this.player.getOnPos(), ModSounds.SOUND_JINGLE.get(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
            this.wonGame = true;
        }
        else if(this.isTrackingFish()){
            if(this.soundTicksFastReel <= 0){
                this.player.level().playLocalSound(this.player.getOnPos(), ModSounds.SOUND_FAST_REEL.get(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
                this.soundTicksFastReel = 20;
            }
            //boolean test = Minecraft.getInstance().getSoundManager(). isActive(ModSounds.SOUND_FAST_REEL.get());

            this.progress = Math.min(this.progress+1, this.maxProgress);

        }else if(this.progress > 0){

            if(soundTicksSlowReel <= 0){
                this.player.level().playLocalSound(this.player.getOnPos(), ModSounds.SOUND_SLOW_REEL.get(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
                this.soundTicksSlowReel = 20;
            }
            if(this.soundTicksFastReel > 0){
                Minecraft.getInstance().getSoundManager().stop(ModSounds.SOUND_FAST_REEL.getId(), null);
                this.soundTicksFastReel = 0;
            }


            this.progress -= 1;

        }else{
            Minecraft.getInstance().getSoundManager().stop(ModSounds.SOUND_SLOW_REEL.getId(), null);

            PacketHandler.INSTANCE.sendToServer(new FishingMinigameResultPacket(-1));

            this.onClose();
        }



        if (Math.random() < (float)(difficulty * (float)((!motionType.equals("smooth")) ? 1 : 20) / 4000f) && (!motionType.equals("smooth") || fishTargetOffset == -1f))
        {
            float spaceBelow = fishOffsetMax - fishOffset;
            float spaceAbove = fishOffset;
            float percent = Math.min(99f, difficulty + (float)randomBetween(10, 45)) / 100f;
            fishTargetOffset = fishOffset + (float)randomBetween(-(int)spaceAbove, (int)spaceBelow) * percent;
        }
        if (motionType.equals("floater"))
        {
            floaterSinkerAcceleration = Math.max(floaterSinkerAcceleration - 0.01f, -1.5f);
        }
        else if (motionType.equals("sinker"))
        {
            floaterSinkerAcceleration = Math.min(floaterSinkerAcceleration + 0.01f, 1.5f);
        }
        if (Math.abs(fishOffset - fishTargetOffset) > 3f && fishTargetOffset != -1f)
        {
            fishAcceleration = (fishTargetOffset - fishOffset) / ((float)randomBetween(10, 30) + (100f - Math.min(100f, difficulty)));
            fishVelocity += (fishAcceleration - fishVelocity) / 5f;
        }
        else if (!motionType.equals("smooth") && Math.random() < (float)(difficulty / 2000f))
        {
            fishTargetOffset = fishOffset + (float)((Math.random() < 0.5) ? randomBetween(-100, -51) : randomBetween(50, 101));
        }
        else
        {
            fishTargetOffset = -1f;
        }
        if (motionType.equals("dart") && Math.random() < (float)(difficulty / 1000f))
        {
            fishTargetOffset = fishOffset + (float)((Math.random() < 0.5) ? randomBetween(-100 - (int)difficulty * 2, -51) : randomBetween(50, 101 + (int)difficulty * 2));
        }
        fishTargetOffset = Math.max(-1f, Math.min(fishTargetOffset, (fishOffsetMax + fishSize/2)));
        fishOffset += fishVelocity + floaterSinkerAcceleration;
        if (fishOffset > fishOffsetMax)
        {
            fishOffset = fishOffsetMax;
        }
        else if (fishOffset < 0f)
        {
            fishOffset = 0f;
        }
        boolean fishInBobber = isTrackingFish();
// if (fishOffset >= (float)(548 - bobberSize) && bobberOffset >= (float)(568 - bobberSize - 4))
// {
// 	fishInBobber = true;
// }
//        boolean num = buttonPressed;
        boolean buttonPressed =  isReeling();
//        if (!num && buttonPressed)
//        {
//            //Game1.playSound("fishingRodBend");
//        }
        float gravity = buttonPressed ? (-0.25f) : 0.25f;
        if (buttonPressed && gravity < 0f && (bobberOffset == 0f || bobberOffset == bobberOffsetMax))
        {
            bobberVelocity = 0f;
        }

        int whichBobber = 1;
        if (fishInBobber)
        {
            gravity *= ((whichBobber == 691) ? 0.3f : 0.6f);
            if (whichBobber == 691)
            {
                if (fishOffset + 16f < bobberOffset + (float)(bobberSize / 2))
                {
                    bobberVelocity -= 0.2f;
                }
                else
                {
                    bobberVelocity += 0.2f;
                }
            }
        }
        float oldPos = bobberOffset;
        bobberVelocity += gravity;
        bobberOffset += bobberVelocity;
        if (bobberOffset + (float)bobberSize > trackSize)
        {
            bobberOffset = bobberOffsetMax;
            bobberVelocity = (0f - bobberVelocity) * 2f / 3f * ((whichBobber == 692) ? 0.1f : 1f);
            if (oldPos + (float)bobberSize < trackSize)
            {
                //Game1.playSound("shiny4");
            }
        }
        else if (bobberOffset < 0f)
        {
            bobberOffset = 0f;
            bobberVelocity = (0f - bobberVelocity) * 2f / 3f;
            if (oldPos > 0f)
            {
                //Game1.playSound("shiny4");
            }
        }
    }

    private float randomBetween(float min, float max){
        return (float)(Math.random() * (max - min) + min);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256 && this.shouldCloseOnEsc()) {
            PacketHandler.INSTANCE.sendToServer(new FishingMinigameResultPacket(-1));

            this.onClose();
            return true;
        } else if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        } else {
            Object object;
            switch (pKeyCode) {
                case 32:
                    //System.out.println("SPACE");
                    break;

            }
            return false;
        }
    }


    @Override
    public void handleDelayedNarration() {

    }

}

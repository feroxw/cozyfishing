package net.mazee.cozyfishing.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mazee.cozyfishing.CozyFishing;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;

public class SDVFishingScreen extends AbstractContainerScreen<SDVFishingMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CozyFishing.MODID,"textures/gui/fishing_gui.png");

    public SDVFishingScreen(SDVFishingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

    }

    //this.minecraft.setScreen((Screen)null);

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, 115, 168);

        renderProgressBar(guiGraphics, x, y);
        renderBobber(guiGraphics, x, y);
        renderFish(guiGraphics, (float)x, (float)y);


        guiGraphics.drawString(this.font, String.valueOf(menu.getScaledProgress()), 10, 10, 4210752, false);
        guiGraphics.drawString(this.font, String.valueOf(menu.getBobberOffset()), 10, 30, 4210752, false);
        guiGraphics.drawString(this.font, String.valueOf(menu.getFishOffset()), 10, 50, 4210752, false);

    }

    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(TEXTURE, x + 72, y + 9 + (149 - menu.getScaledProgress()), 129, 149 - menu.getScaledProgress(), 5, menu.getScaledProgress());
    }

    private void renderBobber(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(TEXTURE, x + 41, y + 9 + menu.getBobberOffset(), 115, 0, 11, 42);
    }

    private void renderFish(GuiGraphics guiGraphics, float x, float y) {
        //guiGraphics.blit(TEXTURE, x + 41, y + 9 + menu.getFishOffset(), 138, 0, 16, 16);
        blit(guiGraphics, TEXTURE, x + 39, y + 9 + menu.getFishOffset(), 138, 0, 16, 16);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void blit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, float pX, float pY, float pUOffset, float pVOffset, float pUWidth, float pVHeight) {
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

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        //pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        //pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}

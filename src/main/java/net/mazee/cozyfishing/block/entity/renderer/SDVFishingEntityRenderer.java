package net.mazee.cozyfishing.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mazee.cozyfishing.block.custom.SDVFishingBlock;
import net.mazee.cozyfishing.block.entity.SDVFishingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;


public class SDVFishingEntityRenderer implements BlockEntityRenderer<SDVFishingEntity> {
    public SDVFishingEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(SDVFishingEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = pBlockEntity.getRenderStack();
        //ItemStack itemStack = pBlockEntity.getDisplayItem();
        pPoseStack.pushPose();


        pPoseStack.translate(0.5f, 0.36f, 0.14f);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(0));


        pPoseStack.scale(0.40f, 0.40f, 0.40f);
        //System.out.println(pBlockEntity.itemHandler.getStackInSlot(7));
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
                pPackedOverlay, pPoseStack, pBufferSource, pBlockEntity.getLevel(), 1);
        pPoseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}

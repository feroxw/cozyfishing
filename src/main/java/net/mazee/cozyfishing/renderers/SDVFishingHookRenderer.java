package net.mazee.cozyfishing.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mazee.cozyfishing.entity.SDVFishingHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SDVFishingHookRenderer extends EntityRenderer<SDVFishingHook> {

    public SDVFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
        System.out.println("constructed");
    }

    @Override
    public ResourceLocation getTextureLocation(SDVFishingHook fishingHook) {
        System.out.println("Called me");
        return null;
    }


//    private static void renderPosTexture(VertexConsumer builder, Matrix4f matrix4f, Matrix3f matrix3f, int i, float x, int y, int u, int v) {
//        builder.vertex(matrix4f, x - 0.5F, (float) y - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float) u, (float) v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
//    }

//    private static void vertex(VertexConsumer builder, Matrix4f matrix4f, Matrix3f matrix3f, int i, float x, int y, int u, int v, float r, float g, float b) {
//        builder.vertex(matrix4f, x - 0.5F, (float) y - 0.5F, 0.0F).color(r, g, b, 1.0F).uv((float) u, (float) v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(i).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
//    }

    private static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float f1, float f2, float r, float g, float b) {
        float var7 = x * f1;
        float var8 = y * (f1 * f1 + f1) * 0.5F + 0.25F;
        float var9 = z * f1;
        float var10 = x * f2 - var7;
        float var11 = y * (f2 * f2 + f2) * 0.5F + 0.25F - var8;
        float var12 = z * f2 - var9;
        float var13 = Mth.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
        var10 /= var13;
        var11 /= var13;
        var12 /= var13;
        vertexConsumer.vertex(pose.pose(), var7, var8, var9).color(r, g, b, 1.0F).normal(pose.normal(), var10, var11, var12).endVertex();
    }

    private static float fraction(int value1, int value2) {
        return (float) value1 / (float) value2;
    }

}

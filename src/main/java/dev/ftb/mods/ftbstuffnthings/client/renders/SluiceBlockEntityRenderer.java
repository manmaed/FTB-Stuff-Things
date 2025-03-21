package dev.ftb.mods.ftbstuffnthings.client.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.ftb.mods.ftbstuffnthings.blocks.sluice.SluiceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class SluiceBlockEntityRenderer implements BlockEntityRenderer<SluiceBlockEntity> {
    public SluiceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SluiceBlockEntity sluice, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int otherlight) {
        FluidStack fluid = sluice.getFluidTank().getFluid();
        if (!fluid.isEmpty()) {
            this.renderFluid(sluice, fluid, matrix, renderer, light, otherlight);
        }

        if (sluice.getProcessingTime() == 0) {
            return;
        }
        ItemStack resource = sluice.getDisplayedItem();
        if (resource.isEmpty()) {
            return;
        }

        int progress = (sluice.getProcessingProgress() * 100) / Math.max(1, sluice.getProcessingTime());
        float offset = sluice.getProcessingProgress() < 0 ? 0 : progress;

        float v = sluice.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        matrix.pushPose();
        matrix.translate(.5F, .85F - (offset / 250F), .5F);
        matrix.scale(1.4F, 1.4F, 1.4F);
        matrix.mulPose(Axis.YN.rotationDegrees(45 + v));

        Minecraft.getInstance().getItemRenderer().renderStatic(
                resource, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, light, otherlight, matrix,renderer, Minecraft.getInstance().level, 0
        );

        matrix.popPose();
    }

    // Lats code from jars (simpler this way)
    private void renderFluid(SluiceBlockEntity te, FluidStack fluid, PoseStack matrix, MultiBufferSource renderer, int light, int otherlight) {
        VertexConsumer builder = renderer.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));

        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation texture = renderProps.getFlowingTexture(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

        int[] cols = decomposeColor(renderProps.getTintColor(fluid));
        float r = cols[1] / 255F;
        float g = cols[2] / 255F;
        float b = cols[3] / 255F;
        float a = cols[0] / 255F;

        float y1 = .5F;

        float u0top = sprite.getU(3F / 16F);
        float v0top = sprite.getV(3F / 16F);
        float u1top = sprite.getU(13F / 16F);
        float v1top = sprite.getV(13F / 16F);

        Direction facing = te.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        matrix.pushPose();
        matrix.translate(.5, 0, .5);
        matrix.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        PoseStack.Pose last = matrix.last();

        Matrix4f matrix4 = last.pose();
        builder.addVertex(matrix4, -.38F, y1, -.39F).setColor(r, g, b, a).setUv(u0top, v0top).setOverlay(otherlight).setLight(light).setNormal(0F, 1F, 0F);
        builder.addVertex(matrix4, -.38F, .13F, .45F).setColor(r, g, b, a).setUv(u0top, v1top).setOverlay(otherlight).setLight(light).setNormal(0F, 1F, 0F);
        builder.addVertex(matrix4, .38F, .13F, .45F).setColor(r, g, b, a).setUv(u1top, v1top).setOverlay(otherlight).setLight(light).setNormal(0F, 1F, 0F);
        builder.addVertex(matrix4, .38F, y1, -.39F).setColor(r, g, b, a).setUv(u1top, v0top).setOverlay(otherlight).setLight(light).setNormal(0F, 1F, 0F);
        matrix.popPose();

        // Second block fluid
        matrix.pushPose();
        matrix.translate(0, -.87F, 0);
//        matrix.translate(facing.getStepX(), 0, facing.getStepZ());
        matrix.translate((facing.getAxisDirection() == Direction.AxisDirection.POSITIVE && facing.getAxis() == Direction.Axis.X) || (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE && facing.getAxis() == Direction.Axis.Z)
                ? 1F : 0, 0, facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1F : 0);

        if (facing.getAxis() == Direction.Axis.Z) {
            matrix.mulPose(Axis.YP.rotationDegrees(facing.getAxisDirection() != Direction.AxisDirection.POSITIVE ? 180 : 0));
        } else {
            matrix.mulPose(Axis.YP.rotationDegrees(facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 90 : -90));
        }

        last = matrix.last();
        matrix4 = last.pose();
        builder.addVertex(matrix4, .1F, 1F, 0F).setColor(r, g, b, a).setUv(u0top, v0top).setOverlay(otherlight).setLight(light).setNormal(last, 0F, 1F, 0F);
        builder.addVertex(matrix4, .1F, 1F, 1F).setColor(r, g, b, a).setUv(u0top, v1top).setOverlay(otherlight).setLight(light).setNormal(last, 0F, 1F, 0F);
        builder.addVertex(matrix4, .9F, 1F, 1F).setColor(r, g, b, a).setUv(u1top, v1top).setOverlay(otherlight).setLight(light).setNormal(last, 0F, 1F, 0F);
        builder.addVertex(matrix4, .9F, 1F, 0F).setColor(r, g, b, a).setUv(u1top, v0top).setOverlay(otherlight).setLight(light).setNormal(last, 0F, 1F, 0F);
        matrix.popPose();
    }

    public static int[] decomposeColor(int color) {
        int[] res = new int[4];
        res[0] = color >> 24 & 0xff;
        res[1] = color >> 16 & 0xff;
        res[2] = color >> 8  & 0xff;
        res[3] = color       & 0xff;
        return res;
    }

    @Override
    public AABB getRenderBoundingBox(SluiceBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1);
    }
}

package MVlate.mbelt.client.render;

import MVlate.mbelt.MBeltConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class BeltRenderer implements ICurioRenderer {

    private final EntityModel<LivingEntity> model;
    private final ResourceLocation texture;

    public BeltRenderer(EntityModel<LivingEntity> model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrixStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
            int light, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {

        if (model instanceof Leather_Belt) {
            ((Leather_Belt<?>) model).bag_bone_small.visible = false;
            ((Leather_Belt<?>) model).bag_bone_medium.visible = false;
            ((Leather_Belt<?>) model).bag_bone_large.visible = false;
        } else if (model instanceof String_Belt) {
            ((String_Belt<?>) model).bag_bone_small.visible = false;
            ((String_Belt<?>) model).bag_bone_medium.visible = false;
        } else if (model instanceof Hardened_Belt) {
            ((Hardened_Belt<?>) model).bag_bone_small.visible = false;
            ((Hardened_Belt<?>) model).bag_bone_medium.visible = false;
            ((Hardened_Belt<?>) model).bag_bone_large.visible = false;
        }

        if (stack.hasTag() && stack.getTag().getInt(MBeltConstants.NBT_BAG_SIZE) > 0) {
            int size = stack.getTag().getInt(MBeltConstants.NBT_BAG_SIZE);

            switch (size) {
                case 4:
                    if (model instanceof Leather_Belt) {
                        ((Leather_Belt<?>) model).bag_bone_small.visible = true;
                    } else if (model instanceof String_Belt) {
                        ((String_Belt<?>) model).bag_bone_small.visible = true;
                    }else if (model instanceof Hardened_Belt) {
                        ((Hardened_Belt<?>) model).bag_bone_small.visible = true;
                    }
                    break;

                case 8:
                    if (model instanceof Leather_Belt) {
                        ((Leather_Belt<?>) model).bag_bone_medium.visible = true;
                    } else if (model instanceof String_Belt) {
                        ((String_Belt<?>) model).bag_bone_medium.visible = true;
                    }   else if (model instanceof Hardened_Belt) {
                        ((Hardened_Belt<?>) model).bag_bone_medium.visible = true;
                    }
                    break;

                case 12:
                    if (model instanceof Leather_Belt) {
                        ((Leather_Belt<?>) model).bag_bone_large.visible = true;
                    }else if (model instanceof Hardened_Belt) {
                        ((Hardened_Belt<?>) model).bag_bone_large.visible = true;
                    }
                    break;
            }
        }
        LivingEntity entity = slotContext.entity();
        matrixStack.pushPose();

        ICurioRenderer.translateIfSneaking(matrixStack, entity);
        ICurioRenderer.rotateIfSneaking(matrixStack, entity);

        VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(this.texture));

        this.model.renderToBuffer(matrixStack, vertexConsumer, light,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }
}
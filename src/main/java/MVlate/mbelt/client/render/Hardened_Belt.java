package MVlate.mbelt.client.render;// Made with Blockbench 5.1.4


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class Hardened_Belt<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public final ModelPart body;
	public final ModelPart bag_bone_large;
	public final ModelPart bag_bone_medium;
	public final ModelPart bag_bone_small;

	public Hardened_Belt(ModelPart root) {
		this.body = root.getChild("body");
		this.bag_bone_large = root.getChild("bag_bone_large");
		this.bag_bone_medium = root.getChild("bag_bone_medium");
		this.bag_bone_small = root.getChild("bag_bone_small");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, -0.5F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(26, 16).addBox(-3.25F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(10, 20).addBox(-2.75F, -1.0F, -4.5F, 1.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(14, 27).addBox(-1.75F, -1.0F, -4.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 21).addBox(2.25F, -1.0F, -4.5F, 1.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(14, 27).addBox(-1.75F, -1.0F, 3.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -0.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition bag_bone_large = partdefinition.addOrReplaceChild("bag_bone_large", CubeListBuilder.create().texOffs(2, 14).addBox(-4.0F, -0.75F, -1.25F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.3F))
		.texOffs(2, 14).addBox(-4.0F, -1.0F, -0.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 8).addBox(-2.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(1.75F, 11.0F, 4.0F));

		PartDefinition bag_bone_medium = partdefinition.addOrReplaceChild("bag_bone_medium", CubeListBuilder.create().texOffs(4, 14).addBox(-1.5F, -0.6667F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.3F))
		.texOffs(4, 14).addBox(-1.5F, -0.9167F, -0.25F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 8).addBox(-0.5F, -0.9167F, -0.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(0.217F, 10.9167F, 3.7251F));

		PartDefinition bag_bone_small = partdefinition.addOrReplaceChild("bag_bone_small", CubeListBuilder.create().texOffs(5, 14).addBox(-0.5F, -0.6667F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.3F))
		.texOffs(5, 14).addBox(-0.5F, -0.9167F, -0.25F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 9).addBox(0.0F, -0.9167F, -0.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(1.217F, 10.9167F, 3.7251F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bag_bone_large.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bag_bone_medium.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bag_bone_small.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
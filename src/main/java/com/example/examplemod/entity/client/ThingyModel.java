package com.example.examplemod.entity.client;

import com.example.examplemod.entity.animations.ModAnimationDefinitions;
import com.example.examplemod.entity.custom.ThingyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ThingyModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart thingy;
	private final ModelPart body;
	private final ModelPart r_arm;
	private final ModelPart l_arm;
	private final ModelPart r_leg;
	private final ModelPart l_leg;
	private final ModelPart head;
	private final ModelPart tail;

	public ThingyModel(ModelPart root) {
		this.thingy = root.getChild("thingy");
		this.body = this.thingy.getChild("body");
		this.r_arm = this.body.getChild("r_arm");
		this.l_arm = this.body.getChild("l_arm");
		this.r_leg = this.body.getChild("r_leg");
		this.l_leg = this.body.getChild("l_leg");
		this.head = this.body.getChild("head");
		this.tail = this.head.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition thingy = partdefinition.addOrReplaceChild("thingy", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = thingy.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition r_arm = body.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -11.0F, 0.0F));

		PartDefinition l_arm = body.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(8, 16).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -11.0F, 0.0F));

		PartDefinition r_leg = body.addOrReplaceChild("r_leg", CubeListBuilder.create().texOffs(16, 16).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -6.0F, 0.0F));

		PartDefinition l_leg = body.addOrReplaceChild("l_leg", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -6.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));

		PartDefinition l_ear_r1 = head.addOrReplaceChild("l_ear_r1", CubeListBuilder.create().texOffs(1, 1).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition r_ear_r1 = head.addOrReplaceChild("r_ear_r1", CubeListBuilder.create().texOffs(1, 1).mirror().addBox(-1.0F, -5.0F, 0.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition tail = head.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

		PartDefinition cube_r1 = tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(26, 29).mirror().addBox(1.25F, -1.3721F, -1.2205F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(16, 33).addBox(-1.0F, 0.8779F, -1.2205F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(23, 2).addBox(-1.0F, -1.6221F, -1.2205F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(10, 27).addBox(-1.25F, -1.3721F, -1.2205F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 27).addBox(-1.0F, -1.3721F, -1.2205F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 4.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition cube_r2 = tail.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(13, 21).mirror().addBox(1.25F, -1.0F, -1.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(14, 25).addBox(-1.0F, 1.25F, -1.0F, 2.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(22, 25).addBox(-1.0F, -1.25F, -1.0F, 2.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(7, 23).addBox(-1.25F, -1.0F, -1.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(18, 25).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

		this.animateWalk(ModAnimationDefinitions.THINGY_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(((ThingyEntity) entity).idleAnimationState, ModAnimationDefinitions.THINGY_IDLE, ageInTicks, 1f);
	}

	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {

		pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
		pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

		this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		thingy.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return thingy;
	}
}
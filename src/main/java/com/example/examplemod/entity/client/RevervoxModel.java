package com.example.examplemod.entity.client;


import com.example.examplemod.entity.animations.ModAnimationDefinitions;
import com.example.examplemod.entity.custom.RevervoxEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class RevervoxModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart revervox;
	private final ModelPart body;
	private final ModelPart torso;
	private final ModelPart arms;
	private final ModelPart r_arm;
	private final ModelPart r_upper;
	private final ModelPart r_lower;
	private final ModelPart r_hand;
	private final ModelPart hand_claw_r;
	private final ModelPart l_arm;
	private final ModelPart l_upper;
	private final ModelPart l_lower;
	private final ModelPart l_hand;
	private final ModelPart hand_claw_l;
	private final ModelPart chest;
	private final ModelPart hump;
	private final ModelPart head;
	private final ModelPart lower_jaw;
	private final ModelPart tongue;
	private final ModelPart upper_teeth_l;
	private final ModelPart legs;
	private final ModelPart r_leg;
	private final ModelPart r_lower_leg;
	private final ModelPart r_feet;
	private final ModelPart feet_claw_r;
	private final ModelPart r_upper_leg;
	private final ModelPart l_leg;
	private final ModelPart l_upper_leg;
	private final ModelPart l_lower_leg;
	private final ModelPart l_feet;
	private final ModelPart feet_claw_l;

	public RevervoxModel(ModelPart root) {
		this.revervox = root.getChild("revervox");
		this.body = this.revervox.getChild("body");
		this.torso = this.body.getChild("torso");
		this.arms = this.torso.getChild("arms");
		this.r_arm = this.arms.getChild("r_arm");
		this.r_upper = this.r_arm.getChild("r_upper");
		this.r_lower = this.r_arm.getChild("r_lower");
		this.r_hand = this.r_lower.getChild("r_hand");
		this.hand_claw_r = this.r_hand.getChild("hand_claw_r");
		this.l_arm = this.arms.getChild("l_arm");
		this.l_upper = this.l_arm.getChild("l_upper");
		this.l_lower = this.l_arm.getChild("l_lower");
		this.l_hand = this.l_lower.getChild("l_hand");
		this.hand_claw_l = this.l_hand.getChild("hand_claw_l");
		this.chest = this.torso.getChild("chest");
		this.hump = this.torso.getChild("hump");
		this.head = this.torso.getChild("head");
		this.lower_jaw = this.head.getChild("lower_jaw");
		this.tongue = this.lower_jaw.getChild("tongue");
		this.upper_teeth_l = this.head.getChild("upper_teeth_l");
		this.legs = this.body.getChild("legs");
		this.r_leg = this.legs.getChild("r_leg");
		this.r_lower_leg = this.r_leg.getChild("r_lower_leg");
		this.r_feet = this.r_lower_leg.getChild("r_feet");
		this.feet_claw_r = this.r_feet.getChild("feet_claw_r");
		this.r_upper_leg = this.r_leg.getChild("r_upper_leg");
		this.l_leg = this.legs.getChild("l_leg");
		this.l_upper_leg = this.l_leg.getChild("l_upper_leg");
		this.l_lower_leg = this.l_leg.getChild("l_lower_leg");
		this.l_feet = this.l_lower_leg.getChild("l_feet");
		this.feet_claw_l = this.l_feet.getChild("feet_claw_l");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition revervox = partdefinition.addOrReplaceChild("revervox", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = revervox.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -41.0F, 0.0F));

		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition arms = torso.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 43.0F, 0.0F));

		PartDefinition r_arm = arms.addOrReplaceChild("r_arm", CubeListBuilder.create(), PartPose.offset(7.6F, -63.0F, 0.0F));

		PartDefinition r_upper = r_arm.addOrReplaceChild("r_upper", CubeListBuilder.create().texOffs(32, 117).addBox(-2.1F, -2.3F, -1.9F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.4F, 1.68F, -0.42F));

		PartDefinition r_lower = r_arm.addOrReplaceChild("r_lower", CubeListBuilder.create(), PartPose.offset(0.4F, 18.06F, -0.42F));

		PartDefinition cube_r1 = r_lower.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, -6).addBox(1.38F, -24.06F, 1.32F, 0.0F, 48.0F, 29.0F, new CubeDeformation(0.0F))
		.texOffs(116, 0).addBox(-1.62F, -2.06F, -1.68F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition r_hand = r_lower.addOrReplaceChild("r_hand", CubeListBuilder.create().texOffs(32, 113).addBox(1.1136F, 2.7057F, -0.946F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(74, 104).addBox(-0.3334F, 2.653F, -0.946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 107).addBox(-1.8666F, 2.653F, -0.946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.2F, 22.94F, -5.58F));

		PartDefinition cube_r2 = r_hand.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 115).addBox(-0.3527F, -0.309F, -0.5964F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5139F, 4.7723F, -0.3198F, 0.3054F, 0.0F, 0.0F));

		PartDefinition cube_r3 = r_hand.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 90).addBox(-0.9F, 0.2838F, -0.8946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5666F, 4.0566F, -0.2005F, 0.3054F, 0.0F, 0.0F));

		PartDefinition cube_r4 = r_hand.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(70, 107).addBox(-0.2014F, 1.796F, -0.7455F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2103F, -0.2673F, 0.1275F, -3.1389F, -0.0392F, 3.1355F));

		PartDefinition cube_r5 = r_hand.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(74, 110).addBox(-1.0F, -1.1054F, -0.7946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1136F, 4.5039F, 0.0977F, 0.3054F, 0.0F, 0.0F));

		PartDefinition cube_r6 = r_hand.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(78, 97).addBox(-1.0243F, -0.9793F, -0.7455F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2103F, -0.2673F, 0.1275F, 3.1274F, -0.0367F, -2.711F));

		PartDefinition cube_r7 = r_hand.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(22, 133).addBox(-1.9126F, -1.3162F, -0.2982F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0262F, 0.2396F, -0.4092F, -0.0873F, 0.0F, 0.0F));

		PartDefinition hand_claw_r = r_hand.addOrReplaceChild("hand_claw_r", CubeListBuilder.create().texOffs(163, 82).addBox(-0.05F, -0.3103F, -0.4313F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.4864F, 3.2039F, 0.0977F));

		PartDefinition hand_claw_r_3_r1 = hand_claw_r.addOrReplaceChild("hand_claw_r_3_r1", CubeListBuilder.create().texOffs(163, 82).addBox(-3.0F, 0.1446F, -1.2946F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(163, 82).addBox(-1.5F, 0.8946F, -1.2946F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 150).addBox(0.0F, 0.1446F, -1.0446F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, 1.3F, 0.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition l_arm = arms.addOrReplaceChild("l_arm", CubeListBuilder.create(), PartPose.offset(-7.6F, -63.0F, 0.0F));

		PartDefinition l_upper = l_arm.addOrReplaceChild("l_upper", CubeListBuilder.create().texOffs(122, 84).addBox(-1.9F, -2.3F, -1.9F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.4F, 1.68F, -0.42F));

		PartDefinition l_lower = l_arm.addOrReplaceChild("l_lower", CubeListBuilder.create(), PartPose.offset(-0.4F, 18.06F, -0.42F));

		PartDefinition cube_r8 = l_lower.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(116, 29).addBox(-1.38F, -2.06F, -1.68F, 3.0F, 26.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, -6).mirror().addBox(-1.38F, -24.06F, 1.32F, 0.0F, 48.0F, 29.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition l_hand = l_lower.addOrReplaceChild("l_hand", CubeListBuilder.create().texOffs(32, 110).addBox(0.9666F, 2.653F, -0.946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(70, 110).addBox(-0.5666F, 2.653F, -0.946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(66, 117).addBox(-2.0136F, 2.7057F, -0.946F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.1F, 22.94F, -5.58F));

		PartDefinition cube_r9 = l_hand.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(74, 107).addBox(0.0F, -1.1054F, -0.7946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0136F, 4.5039F, 0.0977F, 0.3054F, 0.0F, 0.0F));

		PartDefinition cube_r10 = l_hand.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(48, 136).addBox(-2.0874F, -1.3162F, -0.2982F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0738F, 0.2396F, -0.4092F, -0.0873F, 0.0F, 0.0F));

		PartDefinition cube_r11 = l_hand.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(36, 90).addBox(-0.1F, 0.2838F, -0.8946F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4666F, 4.0566F, -0.2005F, 0.3054F, 0.0F, 0.0F));

		PartDefinition cube_r12 = l_hand.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(70, 104).addBox(-0.7986F, 1.796F, -0.7455F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3103F, -0.2673F, 0.1275F, -3.1389F, 0.0392F, -3.1355F));

		PartDefinition cube_r13 = l_hand.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(78, 93).addBox(0.0243F, -0.9793F, -0.7455F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3103F, -0.2673F, 0.1275F, 3.1274F, 0.0367F, 2.711F));

		PartDefinition cube_r14 = l_hand.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(78, 101).addBox(-0.6473F, -0.309F, -0.5964F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6139F, 4.7723F, -0.3198F, 0.3054F, 0.0F, 0.0F));

		PartDefinition hand_claw_l = l_hand.addOrReplaceChild("hand_claw_l", CubeListBuilder.create().texOffs(163, 82).addBox(5.2F, -0.3103F, -0.1813F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.6364F, 3.2039F, -0.1523F));

		PartDefinition hand_claw_l_3_r1 = hand_claw_l.addOrReplaceChild("hand_claw_l_3_r1", CubeListBuilder.create().texOffs(163, 82).addBox(-3.0F, 0.1446F, -0.7946F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(163, 82).addBox(-1.5F, 0.8946F, -1.0446F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(81, 141).addBox(0.0F, 0.1446F, -1.0446F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1F, 1.3F, 0.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition chest = torso.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(36, 104).addBox(-5.24F, -1.0F, -6.72F, 11.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(46, 71).addBox(-5.82F, -15.84F, -7.56F, 12.0F, 15.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.3F, -5.3F, 3.36F));

		PartDefinition hump = torso.addOrReplaceChild("hump", CubeListBuilder.create(), PartPose.offset(0.2F, 43.0F, 1.2F));

		PartDefinition cube_r15 = hump.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(48, 117).addBox(6.3F, -4.67F, 3.0626F, 0.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 90).addBox(-4.72F, -4.67F, -3.9374F, 11.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(128, 30).addBox(0.78F, -4.67F, 3.2026F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(66, 124).addBox(-4.74F, -4.67F, 3.0626F, 0.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.98F, -71.694F, 0.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r16 = hump.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(84, 124).addBox(7.14F, 1.5646F, 2.915F, 0.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(100, 127).addBox(-3.9F, 1.5646F, 2.915F, 0.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.82F, -71.694F, 3.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition cube_r17 = hump.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(102, 114).addBox(-5.28F, 1.7446F, 3.335F, 11.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.42F, -62.034F, -0.9F, -0.2618F, 0.0F, 0.0F));

		PartDefinition cube_r18 = hump.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(147, 0).addBox(-5.28F, 1.5386F, -3.125F, 11.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.42F, -71.694F, 0.04F, -0.0873F, 0.0F, 0.0F));

		PartDefinition cube_r19 = hump.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(0, 133).addBox(-0.22F, 1.5646F, 4.735F, 0.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.02F, -71.694F, 1.26F, -0.0873F, 0.0F, 0.0F));

		PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -22.0F, -1.0F));

		PartDefinition lower_jaw = head.addOrReplaceChild("lower_jaw", CubeListBuilder.create(), PartPose.offset(-0.84F, 0.026F, -1.1F));

		PartDefinition lower_jaw_r1 = lower_jaw.addOrReplaceChild("lower_jaw_r1", CubeListBuilder.create().texOffs(79, 104).addBox(-3.54F, -0.8792F, -9.5217F, 9.0F, 0.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(84, 71).addBox(-5.28F, -0.8372F, -9.9417F, 12.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(130, 123).addBox(-3.53F, -2.8372F, -9.5217F, 9.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition tongue = lower_jaw.addOrReplaceChild("tongue", CubeListBuilder.create(), PartPose.offset(1.0F, -0.126F, 0.0F));

		PartDefinition tongue_r1 = tongue.addOrReplaceChild("tongue_r1", CubeListBuilder.create().texOffs(71, 115).addBox(-2.02F, -0.8792F, -8.2617F, 6.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition upper_teeth_l = head.addOrReplaceChild("upper_teeth_l", CubeListBuilder.create(), PartPose.offset(0.84F, 0.246F, -1.1F));

		PartDefinition l_ear_r1 = upper_teeth_l.addOrReplaceChild("l_ear_r1", CubeListBuilder.create().texOffs(116, 58).mirror().addBox(-9.2306F, -8.7115F, -1.7516F, 11.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.88F, -3.58F, -4.2F, 0.5582F, 0.9917F, 0.4666F));

		PartDefinition r_ear_r1 = upper_teeth_l.addOrReplaceChild("r_ear_r1", CubeListBuilder.create().texOffs(116, 58).addBox(-1.7694F, -8.7115F, -1.7516F, 11.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2F, -3.58F, -4.2F, 0.5582F, -0.9917F, -0.4666F));

		PartDefinition upper_teeth_m_r1 = upper_teeth_l.addOrReplaceChild("upper_teeth_m_r1", CubeListBuilder.create().texOffs(130, 121).addBox(-4.16F, 2.814F, -10.72F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(60, 18).addBox(-5.28F, -5.186F, -11.42F, 12.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 71).addBox(-5.28F, -5.186F, -10.92F, 12.0F, 8.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.68F, -4.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition upper_teeth_back_r1 = upper_teeth_l.addOrReplaceChild("upper_teeth_back_r1", CubeListBuilder.create().texOffs(37, 94).addBox(-4.16F, 2.814F, -10.72F, 10.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.68F, -3.916F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition legs = body.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 41.0F, 0.0F));

		PartDefinition r_leg = legs.addOrReplaceChild("r_leg", CubeListBuilder.create(), PartPose.offset(3.0F, -41.0F, -1.0F));

		PartDefinition r_lower_leg = r_leg.addOrReplaceChild("r_lower_leg", CubeListBuilder.create().texOffs(130, 70).addBox(-1.74F, 7.34F, 4.305F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.465F, 20.0F, -3.62F));

		PartDefinition cube_r20 = r_lower_leg.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(128, 0).addBox(-1.74F, -1.92F, -1.26F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition r_feet = r_lower_leg.addOrReplaceChild("r_feet", CubeListBuilder.create().texOffs(118, 105).addBox(-1.5117F, 1.9334F, -6.4014F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.2133F, 17.2608F, 5.811F));

		PartDefinition cube_r21 = r_feet.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(8, 133).addBox(-1.32F, -0.32F, -2.1F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1917F, 0.7834F, -1.3314F, 0.8727F, 0.0F, 0.0F));

		PartDefinition feet_claw_r = r_feet.addOrReplaceChild("feet_claw_r", CubeListBuilder.create(), PartPose.offset(-1.6917F, 3.0534F, -5.9014F));

		PartDefinition feet_claw_r_3_r1 = feet_claw_r.addOrReplaceChild("feet_claw_r_3_r1", CubeListBuilder.create().texOffs(161, 94).addBox(0.68F, -1.32F, -1.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(158, 70).addBox(1.68F, -1.32F, -1.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition feet_claw_r_1_r1 = feet_claw_r.addOrReplaceChild("feet_claw_r_1_r1", CubeListBuilder.create().texOffs(150, 29).addBox(0.68F, -0.42F, -1.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -0.9F, -0.5F, 0.3927F, 0.0F, 0.0F));

		PartDefinition r_upper_leg = r_leg.addOrReplaceChild("r_upper_leg", CubeListBuilder.create(), PartPose.offset(0.465F, 18.32F, -3.62F));

		PartDefinition cube_r22 = r_upper_leg.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(16, 107).addBox(-2.11F, -20.53F, -1.89F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition l_leg = legs.addOrReplaceChild("l_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, -41.0F, -1.0F));

		PartDefinition l_upper_leg = l_leg.addOrReplaceChild("l_upper_leg", CubeListBuilder.create(), PartPose.offset(0.4413F, 18.0212F, -3.3212F));

		PartDefinition cube_r23 = l_upper_leg.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(0, 107).addBox(-1.89F, -20.53F, -1.89F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition l_lower_leg = l_leg.addOrReplaceChild("l_lower_leg", CubeListBuilder.create().texOffs(128, 45).addBox(-1.26F, 7.34F, 4.305F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.4413F, 19.7012F, -3.3212F));

		PartDefinition cube_r24 = l_lower_leg.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(128, 15).addBox(-1.26F, -1.92F, -1.26F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition l_feet = l_lower_leg.addOrReplaceChild("l_feet", CubeListBuilder.create().texOffs(130, 113).addBox(-1.465F, 2.1851F, -6.2847F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.19F, 16.8149F, 5.6247F));

		PartDefinition cube_r25 = l_feet.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(134, 105).addBox(-1.68F, -0.32F, -2.1F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.215F, 1.0351F, -1.2147F, 0.8727F, 0.0F, 0.0F));

		PartDefinition feet_claw_l = l_feet.addOrReplaceChild("feet_claw_l", CubeListBuilder.create(), PartPose.offset(-1.665F, 3.3051F, -5.7847F));

		PartDefinition feet_claw_l_3_r1 = feet_claw_l.addOrReplaceChild("feet_claw_l_3_r1", CubeListBuilder.create().texOffs(161, 94).addBox(0.68F, -1.32F, -1.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(161, 94).addBox(1.68F, -1.32F, -1.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(161, 94).addBox(2.68F, -1.32F, -1.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

		this.animate(((RevervoxEntity) entity).idleAnimationState, ModAnimationDefinitions.REVERVOX_IDLE, ageInTicks, 1f);

	}

	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {

		pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
		pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

		this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		revervox.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return revervox;
	}
}
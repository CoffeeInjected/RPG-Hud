package net.spellcraftgaming.rpghud.gui.hud.element.vanilla;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.spellcraftgaming.rpghud.gui.hud.element.HudElement;
import net.spellcraftgaming.rpghud.gui.hud.element.HudElementType;
import net.spellcraftgaming.rpghud.settings.Settings;

public class HudElementEntityInspectVanilla extends HudElement {

	protected static final ResourceLocation DAMAGE_INDICATOR = new ResourceLocation("rpghud:textures/entityinspect.png");

	@Override
	public boolean checkConditions() {
		return !this.mc.gameSettings.hideGUI && this.settings.getBoolValue(Settings.enable_entity_inspect);
	}

	public HudElementEntityInspectVanilla() {
		super(HudElementType.ENTITY_INSPECT, 0, 0, 0, 0, true);
	}

	@Override
	public void drawElement(Gui gui, float zLevel, float partialTicks, int scaledWidth, int scaledHeight) {
		EntityLiving focused = getFocusedEntity(mc.player);
		if (focused != null) {
			int posX = (scaledWidth / 2) + this.settings.getPositionValue(Settings.inspector_position)[0];
			int posY = this.settings.getPositionValue(Settings.inspector_position)[1];
			this.mc.getTextureManager().bindTexture(DAMAGE_INDICATOR);
			gui.drawTexturedModalRect(posX - 62, 20 + posY, 0, 0, 128, 36);
			drawCustomBar(posX - 25, 34 + posY, 89, 8, (double) focused.getHealth() / (double) focused.getMaxHealth() * 100D, this.settings.getIntValue(Settings.color_health), offsetColorPercent(this.settings.getIntValue(Settings.color_health), OFFSET_PERCENT));
			String stringHealth = ((double) Math.round(focused.getHealth() * 10)) / 10 + "/" + ((double) Math.round(focused.getMaxHealth() * 10)) / 10;
			GlStateManager.scaled(0.5, 0.5, 0.5);
			gui.drawCenteredString(mc.fontRenderer, stringHealth, (posX - 27 + 44) * 2, (36 + posY) * 2, -1);
			GlStateManager.scaled(2.0, 2.0, 2.0);

			int x = (posX - 29 + 44 - mc.fontRenderer.getStringWidth(focused.getName().getString()) / 2);
			int y = 25 + posY;
			mc.fontRenderer.drawString(focused.getName().getString(), x + 1, y, 0);
			mc.fontRenderer.drawString(focused.getName().getString(), x - 1, y, 0);
			mc.fontRenderer.drawString(focused.getName().getString(), x, y + 1, 0);
			mc.fontRenderer.drawString(focused.getName().getString(), x, y - 1, 0);
			mc.fontRenderer.drawString(focused.getName().getString(), x, y, -1);

			drawEntityOnScreen(posX - 60 + 16, 22 + 27 + posY, focused);
		}
	}

	public static void drawEntityOnScreen(int posX, int posY, EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		int scale = 1;
		int s = (int) (22 / ent.height);
		int s2 = (int) (22 / ent.width);
		if(s < s2) scale = s;
		else scale = s2;
		
		int offset = 0;
		if(ent instanceof EntitySquid) {
			scale = 11;
			offset = -13;
		}
		posY += offset;
		GlStateManager.translatef((float) posX, (float) posY, 50.0F);
		GlStateManager.scaled((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);

		GlStateManager.rotatef(-((float) Math.atan((double) (0 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float) Math.atan((double) (100 / 40.0F)) * 20.0F;
		ent.rotationYaw = (float) Math.atan((double) (25 / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (0 / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translatef(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getInstance().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
		GlStateManager.disableTexture2D();
		GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
	}
	
	public static EntityLiving getFocusedEntity(Entity watcher) {
		EntityLiving focusedEntity = null;
		double maxDistance = 64;
		Vec3d vec = new Vec3d(watcher.posX, watcher.posY, watcher.posZ);
		Vec3d posVec = watcher.getPositionVector();
		if (watcher instanceof EntityPlayer) {
			vec = vec.add(0D, watcher.getEyeHeight(), 0D);
			posVec = posVec.add(0D, watcher.getEyeHeight(), 0D);
		}
		Vec3d lookVec = watcher.getLookVec();
		Vec3d vec2 = vec.add(lookVec.normalize().scale(maxDistance));
		RayTraceResult ray = watcher.world.rayTraceBlocks(vec, vec2);

		double distance = maxDistance;
		if (ray != null) {
			distance = ray.hitVec.distanceTo(posVec);
		}
		Vec3d reachVector = posVec.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

		double currentDistance = distance;

		List<Entity> entitiesWithinMaxDistance = watcher.world.getEntitiesWithinAABBExcludingEntity(watcher, watcher.getBoundingBox().grow(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance).expand(1, 1, 1));
		for (Entity entity : entitiesWithinMaxDistance) {
			if (entity instanceof EntityLiving) {
				float collisionBorderSize = entity.getCollisionBorderSize();
				AxisAlignedBB hitBox = entity.getBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
				RayTraceResult intercept = hitBox.calculateIntercept(posVec, reachVector);
				if (hitBox.contains(posVec)) {
					if (currentDistance <= 0D) {
						currentDistance = 0;
						focusedEntity = (EntityLiving) entity;
					}
				} else if (intercept != null) {
					double distanceToEntity = posVec.distanceTo(intercept.hitVec);
					if (distanceToEntity <= currentDistance) {
						currentDistance = distanceToEntity;
						focusedEntity = (EntityLiving) entity;
					}
				}
			}
		}
		return focusedEntity;
	}
}

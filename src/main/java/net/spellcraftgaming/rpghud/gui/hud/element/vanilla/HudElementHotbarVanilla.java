package net.spellcraftgaming.rpghud.gui.hud.element.vanilla;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.spellcraftgaming.rpghud.gui.hud.element.HudElement;
import net.spellcraftgaming.rpghud.gui.hud.element.HudElementType;
import net.spellcraftgaming.rpghud.gui.override.GuiIngameRPGHud;

public class HudElementHotbarVanilla extends HudElement {

	protected static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");

	public HudElementHotbarVanilla() {
		super(HudElementType.HOTBAR, 0, 0, 0, 0, true);
	}

	@Override
	public void drawElement(Gui gui, float zLevel, float partialTicks, int scaledWidth, int scaledHeight) {
        if (mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
            ((GuiIngameRPGHud)mc.ingameGUI).getSpectatorGui().renderTooltip(partialTicks);
		} else if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	         this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
	         ItemStack itemstack = mc.player.getHeldItemOffhand();
	         EnumHandSide enumhandside = mc.player.getPrimaryHand().opposite();
	         int i = scaledWidth / 2;
	         float f = zLevel;
	         zLevel = -90.0F;
	         gui.drawTexturedModalRect(i - 91, scaledHeight - 22, 0, 0, 182, 22);
	         gui.drawTexturedModalRect(i - 91 - 1 + mc.player.inventory.currentItem * 20, scaledHeight - 22 - 1, 0, 22, 24, 22);
	         if (!itemstack.isEmpty()) {
	            if (enumhandside == EnumHandSide.LEFT) {
	               gui.drawTexturedModalRect(i - 91 - 29, scaledHeight - 23, 24, 22, 29, 24);
	            } else {
	               gui.drawTexturedModalRect(i + 91, scaledHeight - 23, 53, 22, 29, 24);
	            }
	         }

	         zLevel = f;
	         GlStateManager.enableRescaleNormal();
	         GlStateManager.enableBlend();
	         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	         RenderHelper.enableGUIStandardItemLighting();

	         for(int l = 0; l < 9; ++l) {
	            int i1 = i - 90 + l * 20 + 2;
	            int j1 = scaledHeight - 16 - 3;
	            this.renderHotbarItem(i1, j1, partialTicks, mc.player, mc.player.inventory.mainInventory.get(l));
	         }

	         if (!itemstack.isEmpty()) {
	            int l1 = scaledHeight - 16 - 3;
	            if (enumhandside == EnumHandSide.LEFT) {
	               this.renderHotbarItem(i - 91 - 26, l1, partialTicks, mc.player, itemstack);
	            } else {
	               this.renderHotbarItem(i + 91 + 10, l1, partialTicks, mc.player, itemstack);
	            }
	         }

	         if (this.mc.gameSettings.attackIndicator == 2) {
	            float f1 = this.mc.player.getCooledAttackStrength(0.0F);
	            if (f1 < 1.0F) {
	               int i2 = scaledHeight - 20;
	               int j2 = i + 91 + 6;
	               if (enumhandside == EnumHandSide.RIGHT) {
	                  j2 = i - 91 - 22;
	               }

	               this.mc.getTextureManager().bindTexture(Gui.ICONS);
	               int k1 = (int)(f1 * 19.0F);
	               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	               gui.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
	               gui.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
	            }
	         }

	         RenderHelper.disableStandardItemLighting();
	         GlStateManager.disableRescaleNormal();
	         GlStateManager.disableBlend();
		}
	}

	/**
	 * Renders an item on the screen
	 * 
	 * @param xPos
	 *            the x position on the screen
	 * @param yPos
	 *            the y position on the screen
	 * @param partialTicks
	 *            the partial ticks (used for animation)
	 * @param player
	 *            the player who should get the item rendered
	 * @param item
	 *            the item (via ItemStack)
	 */
	protected void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack item) {
	      if (!item.isEmpty()) {
	          float f = (float)item.getAnimationsToGo() - partialTicks;
	          if (f > 0.0F) {
	             GlStateManager.pushMatrix();
	             float f1 = 1.0F + f / 5.0F;
	             GlStateManager.translatef((float)(x + 8), (float)(y + 12), 0.0F);
	             GlStateManager.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
	             GlStateManager.translatef((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
	          }

	          mc.getItemRenderer().renderItemAndEffectIntoGUI(player, item, x, y);
	          if (f > 0.0F) {
	             GlStateManager.popMatrix();
	          }

	          mc.getItemRenderer().renderItemOverlays(this.mc.fontRenderer, item, x, y);
	       }
	}

}

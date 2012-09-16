package shedar.mods.ic2.nuclearcontrol;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;

@SideOnly(Side.CLIENT)
public class CompactButton extends GuiButton
{

    public CompactButton(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/img/GUIThermalMonitor.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var6 = this.getHoverState(var5);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 64 + var6 * 12, this.width / 2 + width % 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2 + width % 2, this.yPosition, 200 - this.width / 2, 64 + var6 * 12, this.width / 2, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            var4.drawString(displayString, xPosition+(width - var4.getStringWidth(displayString)) / 2, yPosition+2, 0x303030);
        }
    }
    
}

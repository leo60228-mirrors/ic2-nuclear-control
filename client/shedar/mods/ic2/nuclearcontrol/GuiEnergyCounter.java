package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.Container;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.StatCollector;
import ic2.api.NetworkHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import shedar.mods.ic2.nuclearcontrol.ContainerEnergyCounter;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;

@SideOnly(Side.CLIENT)
public class GuiEnergyCounter extends GuiContainer
{
    private String name;
    private ContainerEnergyCounter container;

    public GuiEnergyCounter(Container container)
    {
        super(container);
        this.container = (ContainerEnergyCounter)container; 
        name = StatCollector.translateToLocal("tile.blockEnergyCounter.name");
    }
    
    @SuppressWarnings("unchecked")
    private void initControls()
    {
        controlList.clear();
        controlList.add(new GuiButton(0, guiLeft+35, guiTop+42, 127, 20, StatCollector.translateToLocal("msg.nc.Reset")));
    }
    
    @Override
    public void initGui() 
    {
        super.initGui();
        initControls();
    };

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        String value = StringUtils.getFormatted("", container.energyCounter.counter, false);
        fontRenderer.drawString(value, (xSize - fontRenderer.getStringWidth(value)) / 2, 22, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIEnergyCounter.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        initControls();
    }
    
    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        if (guiButton.id == 0)
        {
            NetworkHelper.initiateClientTileEntityEvent(container.energyCounter, 0);
        }
    }
}

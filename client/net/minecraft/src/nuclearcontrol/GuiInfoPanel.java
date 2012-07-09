package net.minecraft.src.nuclearcontrol;

import java.util.List;

import net.minecraft.src.Container;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StatCollector;
import net.minecraft.src.nuclearcontrol.panel.IPanelDataSource;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;

import org.lwjgl.opengl.GL11;

public class GuiInfoPanel extends GuiContainer
{
    private String name;
    private ContainerInfoPanel container;
    private ItemStack prevCard;

    public GuiInfoPanel(Container container)
    {
        super(container);
        this.container = (ContainerInfoPanel)container; 
        name = StatCollector.translateToLocal("tile.blockInfoPanel.name");
    }
    
    private void initControls()
    {
        ItemStack card = container.getSlot(TileEntityInfoPanel.SLOT_CARD).getStack();
        if((card == null && prevCard == null) || (card!=null  && card.equals(prevCard)))
            return;
        int h = fontRenderer.FONT_HEIGHT + 1;
        controlList.clear();
        prevCard = card;
        if(card!=null && card.getItem() instanceof IPanelDataSource)
        {
            List<PanelSetting> settingsList = ((IPanelDataSource)card.getItem()).getSettingsList();
            int row = 0;
            for (PanelSetting panelSetting : settingsList)
            {
                controlList.add(new GuiInfoPanelCheckBox(0, guiLeft + 32, guiTop + 16 + h*row, panelSetting, container.panel, fontRenderer));
                row++;
            }
        }
    }
    
    @Override
    public void initGui() 
    {
        super.initGui();
        initControls();
    };

    @Override
    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIInfoPanel.png");
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
}

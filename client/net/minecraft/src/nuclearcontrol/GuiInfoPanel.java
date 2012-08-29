package net.minecraft.src.nuclearcontrol;

import java.util.List;

import net.minecraft.src.Container;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StatCollector;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.nuclearcontrol.panel.IPanelDataSource;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;

import org.lwjgl.opengl.GL11;

public class GuiInfoPanel extends GuiContainer
{
    private String name;
    private ContainerInfoPanel container;
    private ItemStack prevCard;
    private GuiTextField textboxTitle;
    private boolean modified;

    public GuiInfoPanel(Container container)
    {
        super(container);
        ySize = 190;
        this.container = (ContainerInfoPanel)container; 
        name = StatCollector.translateToLocal("tile.blockInfoPanel.name");
        modified = false;
    }
    
    private void initControls()
    {
        ItemStack card = container.getSlot(TileEntityInfoPanel.SLOT_CARD).getStack();
        if((card == null && prevCard == null) || (card!=null  && card.equals(prevCard)))
            return;
        int h = fontRenderer.FONT_HEIGHT + 1;
        controlList.clear();
        prevCard = card;
        controlList.add(new GuiInfoPanelShowLabels(0, guiLeft + 7, guiTop + 80, container.panel));
        if(card!=null && card.getItem() instanceof IPanelDataSource)
        {
            IPanelDataSource source = (IPanelDataSource)card.getItem();
            int row = 0;
            List<PanelSetting> settingsList = source.getSettingsList();
            if(settingsList!=null)
            for (PanelSetting panelSetting : settingsList)
            {
                controlList.add(new GuiInfoPanelCheckBox(0, guiLeft + 32, guiTop + 40 + h*row, panelSetting, container.panel, fontRenderer));
                row++;
            }
            if(!modified)
            {
                textboxTitle = new GuiTextField(fontRenderer, 7, 16, 162, 18);
                textboxTitle.setFocused(true);
                textboxTitle.setText(source.getTitle(card));
            }
        }
        else
        {
            modified = false;
            textboxTitle = null;
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
        if(textboxTitle != null)
            textboxTitle.drawTextBox();
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
        if(textboxTitle!=null)
            textboxTitle.updateCursorCounter();
        initControls();
    }
    
    private void updateTitle()
    {
        if(textboxTitle == null)
            return;
        if(container.panel.worldObj.isRemote)
        {
            mod_IC2NuclearControl.setNewAlarmSound(container.panel.xCoord, container.panel.yCoord, container.panel.zCoord, textboxTitle.getText());
        }
        ItemStack card = container.getSlot(TileEntityInfoPanel.SLOT_CARD).getStack();
        if(card!=null && card.getItem() instanceof IPanelDataSource)
        {
            IPanelDataSource source = (IPanelDataSource)card.getItem();
            source.setTitle(card, textboxTitle.getText());
        }
    }
    
    @Override
    public void onGuiClosed()
    {
        updateTitle();
        super.onGuiClosed();
    }    

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            mc.thePlayer.closeScreen();
        }
        else if(par1 == 13)
        {
            updateTitle();
        }
        else if (textboxTitle!=null &&  textboxTitle.getIsFocused())
        {
            modified = true;
            textboxTitle.textboxKeyTyped(par1, par2);
        }
    }

}

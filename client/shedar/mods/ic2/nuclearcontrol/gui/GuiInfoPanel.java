package shedar.mods.ic2.nuclearcontrol.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import shedar.mods.ic2.nuclearcontrol.api.IAdvancedCardSettings;
import shedar.mods.ic2.nuclearcontrol.api.ICardGui;
import shedar.mods.ic2.nuclearcontrol.api.ICardSettingsWrapper;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.api.IPanelMultiCard;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerInfoPanel;
import shedar.mods.ic2.nuclearcontrol.gui.controls.CompactButton;
import shedar.mods.ic2.nuclearcontrol.gui.controls.GuiInfoPanelCheckBox;
import shedar.mods.ic2.nuclearcontrol.gui.controls.GuiInfoPanelShowLabels;
import shedar.mods.ic2.nuclearcontrol.panel.CardSettingsWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.utils.NuclearNetworkHelper;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInfoPanel extends GuiContainer
{
    private static final String TEXTURE_FILE = "/mods/nuclearControl/textures/gui/GUIInfoPanel.png";
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(TEXTURE_FILE);

    protected String name;
    protected ContainerInfoPanel container;
    public ItemStack prevCard;
    protected GuiTextField textboxTitle;
    protected boolean modified;
    public boolean isColored;

    public GuiInfoPanel(Container container)
    {
        super(container);
        ySize = 190;
        this.container = (ContainerInfoPanel)container; 
        name = StatCollector.translateToLocal("tile.blockInfoPanel.name");
        modified = false;
        //inverted value on start to force initControls
        isColored = !this.container.panel.getColored(); 
    }
    
    @SuppressWarnings("unchecked")
    protected void initControls()
    {
        ItemStack card = container.panel.getCards().get(0); 
        if(((card == null && prevCard == null) || (card!=null  && card.equals(prevCard))) && this.container.panel.getColored() == isColored)
            return;
        int h = fontRenderer.FONT_HEIGHT + 1;
        buttonList.clear();
        prevCard = card;
        isColored = this.container.panel.getColored();
        buttonList.add(new GuiInfoPanelShowLabels(0, guiLeft + xSize - 25, guiTop + 42, container.panel));
        int delta = 0;
        if(isColored)
        {
            buttonList.add(new CompactButton(112, guiLeft + xSize - 25, guiTop + 55, 18, 12, "T"));
            delta = 15; 
        }
        if(card!=null && card.getItem() instanceof IPanelDataSource)
        {
            byte slot = container.panel.getIndexOfCard(card);
            IPanelDataSource source = (IPanelDataSource)card.getItem();
            if(source instanceof IAdvancedCardSettings)
            {
                buttonList.add(new CompactButton(111, guiLeft + xSize - 25, guiTop + 55 + delta, 18, 12, "..."));
            }
            int row = 0;
            List<PanelSetting> settingsList = null;
            if(card.getItem() instanceof IPanelMultiCard)
            {
                settingsList = ((IPanelMultiCard)source).getSettingsList(new CardWrapperImpl(card, (byte)0));
            }
            else
            {
                settingsList = source.getSettingsList();
            }
            
            if(settingsList!=null)
            for (PanelSetting panelSetting : settingsList)
            {
                buttonList.add(new GuiInfoPanelCheckBox(0, guiLeft + 32, guiTop + 40 + h*row, panelSetting, container.panel, slot, fontRenderer));
                row++;
            }
            if(!modified)
            {
                textboxTitle = new GuiTextField(fontRenderer, 7, 16, 162, 18);
                textboxTitle.setFocused(true);
                textboxTitle.setText(new CardWrapperImpl(card, 0).getTitle());
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
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        if(textboxTitle != null)
            textboxTitle.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.func_110577_a/*bindTExture*/(TEXTURE_LOCATION);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);
        if(textboxTitle!=null)
            textboxTitle.mouseClicked(x-guiLeft, y-guiTop, par3);
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
    
    protected ItemStack getActiveCard()
    {
        return container.panel.getCards().get(0);
    }
    
    protected void updateTitle()
    {
        if(textboxTitle == null)
            return;
        ItemStack card = getActiveCard();
        if(container.panel.worldObj.isRemote)
        {
            NuclearNetworkHelper.setNewAlarmSound(container.panel.xCoord, container.panel.yCoord, container.panel.zCoord, container.panel.getIndexOfCard(card), textboxTitle.getText());
        }
        if(card!=null && card.getItem() instanceof IPanelDataSource)
        {
            new CardWrapperImpl(card, 0).setTitle(textboxTitle.getText());
        }
    }
    
    @Override
    public void onGuiClosed()
    {
        updateTitle();
        super.onGuiClosed();
    }    
    
    @Override
    protected void actionPerformed(GuiButton button) 
    {
        if(button.id == 112) // color upgrade
        {
            GuiScreen colorGui = new GuiScreenColor(this, container.panel);
            mc.displayGuiScreen(colorGui);
        }
        else if(button.id == 111)
        {
            ItemStack card = getActiveCard();
            if(card == null)
                return;
            if(card != null && card.getItem() instanceof IAdvancedCardSettings)
            {
                ICardWrapper helper = new CardWrapperImpl(card, 0);
                Object guiObject = ((IAdvancedCardSettings)card.getItem()).getSettingsScreen(helper);
                if(!(guiObject instanceof GuiScreen))
                {
                    FMLLog.warning("Invalid card, getSettingsScreen method should return GuiScreen object");
                    return;
                }
                GuiScreen gui = (GuiScreen)guiObject;
                ICardSettingsWrapper wrapper = new CardSettingsWrapperImpl(card, container.panel, this, 0);
                ((ICardGui)gui).setCardSettingsHelper(wrapper);
                mc.displayGuiScreen(gui);
            }        
        }
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (textboxTitle!=null &&  textboxTitle.isFocused())
        {
            if (par2 == 1)
            {
                mc.thePlayer.closeScreen();
            }
            else if(par1 == 13)
            {
                updateTitle();
            }
            else
            {
                modified = true;
                textboxTitle.textboxKeyTyped(par1, par2);
            }
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

}

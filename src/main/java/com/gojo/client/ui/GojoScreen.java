package com.gojo.client.ui;
import com.gojo.client.config.ConfigManager; import com.gojo.client.module.*; import net.minecraft.client.gui.GuiGraphics; import net.minecraft.client.gui.components.Button; import net.minecraft.client.gui.screens.Screen; import net.minecraft.network.chat.Component; import java.util.*;
public final class GojoScreen extends Screen {
 private Category selected=Category.MOVEMENT; private String search=""; private int panelX,panelY,panelW=430,panelH=300;
 private static final int BG=0xF20B0D16,PANEL=0xFF111827,BLUE=0xFF399BFF,PURPLE=0xFF8B5CF6;
 public GojoScreen(){super(Component.literal("GOJO CLIENT"));}
 @Override protected void init(){panelX=(width-panelW)/2;panelY=(height-panelH)/2;int y=panelY+58;
 for(Category c:Category.values()){final Category target=c;addRenderableWidget(Button.builder(Component.literal(c.name()),b->{selected=target;rebuildWidgets();}).bounds(panelX+12,y,92,22).build());y+=27;}
 int index=0;for(Module m:Modules.in(selected)){if(!m.name().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)))continue;final Module target=m;int rowY=panelY+62+index*31;if(rowY>panelY+panelH-48)break;addRenderableWidget(Button.builder(Component.literal(m.name()+(m.enabled()?"  [ON]":"  [OFF]")),b->{target.toggle();ConfigManager.save();rebuildWidgets();}).bounds(panelX+122,rowY,190,24).build());index++;}
 addRenderableWidget(Button.builder(Component.literal("SAVE"),b->ConfigManager.save()).bounds(panelX+panelW-92,panelY+panelH-34,78,22).build());
 addRenderableWidget(Button.builder(Component.literal("CLOSE"),b->onClose()).bounds(panelX+panelW-178,panelY+panelH-34,78,22).build());}
 @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){renderBackground(g,mouseX,mouseY,delta);g.fill(panelX,panelY,panelX+panelW,panelY+panelH,BG);g.fill(panelX,panelY,panelX+panelW,panelY+3,BLUE);g.fill(panelX,panelY+3,panelX+panelW,panelY+48,PANEL);g.drawString(font,"GOJO CLIENT",panelX+16,panelY+15,0xFFFFFFFF,false);g.drawString(font,"1.21.11 | FABRIC | RIGHT SHIFT",panelX+16,panelY+32,0xFF9CA3AF,false);g.fill(panelX+111,panelY+54,panelX+112,panelY+panelH-42,0xFF293244);g.drawString(font,"MODULES / "+selected.name(),panelX+122,panelY+49,PURPLE,false);g.drawString(font,"Ative os módulos clicando nos botões.",panelX+122,panelY+panelH-53,0xFF9CA3AF,false);super.render(g,mouseX,mouseY,delta);}
 @Override public boolean isPauseScreen(){return false;} @Override public void onClose(){ConfigManager.save();super.onClose();}
}
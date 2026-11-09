package com.gojo.client.ui;

import com.gojo.client.config.ConfigManager;
import com.gojo.client.module.Category;
import com.gojo.client.module.Module;
import com.gojo.client.module.Modules;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import java.util.*;

public final class GojoScreen extends Screen {
    private Category selected = Category.COMBAT;
    private String search = "";
    private int x, y, w, h, sidebar, right;
    private boolean searchFocused;
    private int page=0;
    private static final int BG=0xF0080A12, PANEL=0xFF10121D, CARD=0xFF171A28;
    private static final int PURPLE=0xFF8B35FF, PURPLE_DARK=0xFF4B16A8;
    private static final int TEXT=0xFFF5F3FF, MUTED=0xFFAAA5C2, LINE=0xFF29243D;

    public GojoScreen(){ super(Component.literal("GOJO CLIENT")); }

    @Override protected void init() {
        w=Math.min(1160,width-32); h=Math.min(610,height-32);
        x=(width-w)/2; y=(height-h)/2; sidebar=184; right=220;
    }

    private List<Module> visibleModules(){
        return Modules.in(selected).stream()
            .filter(m -> m.name().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))
                || m.description().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))).toList();
    }

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
        renderBackground(g,mouseX,mouseY,delta);
        g.fill(x-3,y-3,x+w+3,y+h+3,0xB34A1C91);
        g.fill(x,y,x+w,y+h,BG);
        g.fill(x,y,x+sidebar,y+h,0xFF090B14);
        g.fill(x+sidebar,y,x+sidebar+1,y+h,LINE);
        g.fill(x+w-right,y,x+w-right+1,y+h,LINE);

        // Brand and navigation
        g.drawString(font,"♛",x+22,y+20,PURPLE,false);
        g.drawString(font,"GOJO",x+50,y+17,TEXT,false);
        g.drawString(font,"CLIENT",x+50,y+34,PURPLE,false);
        g.drawString(font,"MINECRAFT 1.21.11",x+20,y+65,MUTED,false);
        String[] names={"COMBAT","MOVEMENT","RENDER","PLAYER","WORLD","HUD"};
        String[] symbols={"⚔","➤","◈","●","◎","▥"};
        int navY=y+96;
        for(int i=0;i<names.length;i++){
            Category c=Category.values()[i]; boolean active=selected==c;
            int ny=navY+i*39;
            if(active){g.fill(x+10,ny-7,x+sidebar-10,ny+24,0xFF381078);g.fill(x+10,ny-7,x+13,ny+24,PURPLE);}
            g.drawString(font,symbols[i],x+22,ny+2,active?0xFFFFFFFF:PURPLE,false);
            g.drawString(font,names[i],x+45,ny+2,active?TEXT:MUTED,false);
        }
        g.drawString(font,"CONFIGURAÇÕES",x+20,y+h-58,MUTED,false);
        g.drawString(font,"SOBRE  •  GOJO CLIENT",x+20,y+h-36,PURPLE,false);

        // Search and main heading
        int mainX=x+sidebar+18, mainW=w-sidebar-right-36;
        g.fill(mainX,y+17,mainX+mainW,y+54,CARD);
        g.drawString(font,"⌕",mainX+12,y+29,PURPLE,false);
        g.drawString(font,(searchFocused?"Pesquisar: "+search+"_":(search.isEmpty()?"Pesquisar módulos...":search)),mainX+34,y+30,MUTED,false);
        g.drawString(font,selected.name(),mainX,y+72,PURPLE,false);
        g.drawString(font,"Ative e configure os módulos do cliente",mainX,y+88,MUTED,false);

        // Module cards
        List<Module> allVisible=visibleModules();
        int pageCount=Math.max(1,(allVisible.size()+11)/12); page=Math.min(page,pageCount-1);
        List<Module> modules=allVisible.subList(Math.min(page*12,allVisible.size()),Math.min(page*12+12,allVisible.size()));
        int gap=10, cols=3, cardW=Math.max(110,(mainW-gap*(cols-1))/cols), cardH=90;
        for(int i=0;i<modules.size();i++){
            Module m=modules.get(i); int col=i%cols,row=i/cols;
            int cx=mainX+col*(cardW+gap), cy=y+108+row*(cardH+10);
            g.fill(cx,cy,cx+cardW,cy+cardH,CARD);
            g.fill(cx,cy,cx+cardW,cy+1,m.enabled()?PURPLE:LINE);
            g.drawString(font,m.name(),cx+10,cy+12,TEXT,false);
            String desc=m.description(); if(desc.length()>27)desc=desc.substring(0,26)+"…";
            g.drawString(font,desc,cx+10,cy+30,MUTED,false);
            int sx=cx+cardW-39, sy=cy+48;
            g.fill(sx,sy,sx+28,sy+14,m.enabled()?PURPLE:0xFF39394A);
            g.fill(sx+(m.enabled()?15:2),sy+2,sx+(m.enabled()?26:13),sy+12,0xFFFFFFFF);
            g.drawString(font,m.enabled()?"ON":"OFF",cx+10,cy+52,m.enabled()?PURPLE: MUTED,false);
            if(m.configurable()){
                g.drawString(font,"Valor: "+String.format(Locale.ROOT,"%.1f",m.value()),cx+10,cy+70,MUTED,false);
                g.fill(cx+cardW-76,cy+66,cx+cardW-58,cy+83,0xFF343044);
                g.fill(cx+cardW-52,cy+66,cx+cardW-34,cy+83,0xFF343044);
                g.drawCenteredString(font,"-",cx+cardW-67,cy+70,TEXT);
                g.drawCenteredString(font,"+",cx+cardW-43,cy+70,TEXT);
            }
        }
        if(modules.isEmpty())g.drawString(font,"Nenhum módulo encontrado.",mainX,y+126,MUTED,false);
        g.drawString(font,"Página "+(page+1)+" / "+pageCount+"  •  Use Page Up / Page Down",mainX,y+h-24,MUTED,false);

        // Right information rail
        int rx=x+w-right+14, rw=right-28;
        g.fill(rx,y+18,rx+rw,y+148,CARD);
        g.drawCenteredString(font,"♛  GOJO CLIENT",rx+rw/2,y+38,PURPLE);
        g.drawCenteredString(font,"Mais desempenho.",rx+rw/2,y+70,TEXT);
        g.drawCenteredString(font,"Mais controlo.",rx+rw/2,y+86,TEXT);
        g.fill(rx,y+160,rx+rw,y+310,CARD);
        g.drawString(font,"INFORMAÇÕES",rx+12,y+174,PURPLE,false);
        var mc=net.minecraft.client.Minecraft.getInstance();
        g.drawString(font,"FPS: "+mc.getFps(),rx+12,y+198,TEXT,false);
        if(mc.player!=null){
            g.drawString(font,"XYZ: "+(int)mc.player.getX()+" "+(int)mc.player.getY()+" "+(int)mc.player.getZ(),rx+12,y+218,TEXT,false);
            g.drawString(font,"Mundo: "+(mc.level==null?"-":mc.level.dimension().toString()),rx+12,y+238,MUTED,false);
        } else g.drawString(font,"Mundo: menu",rx+12,y+218,MUTED,false);
        g.drawString(font,"Módulos: "+Modules.all().size(),rx+12,y+258,TEXT,false);
        g.drawString(font,"Fabric • Java 21",rx+12,y+278,MUTED,false);
        g.fill(rx,y+322,rx+rw,y+382,CARD);
        g.drawString(font,"NOTIFICAÇÕES",rx+12,y+337,PURPLE,false);
        g.drawString(font,"GOJO CLIENT ativo",rx+12,y+359,TEXT,false);
        g.drawString(font,"RIGHT SHIFT  •  FECHAR: ESC",x+sidebar+18,y+h-24,MUTED,false);
        super.render(g,mouseX,mouseY,delta);
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick){
        double mx=event.x(), my=event.y();
        int button=event.button();
        if(button!=0)return super.mouseClicked(event,doubleClick);
        int navY=y+96;
        for(int i=0;i<Category.values().length;i++){
            int ny=navY+i*39;
            if(mx>=x+10&&mx<=x+sidebar-10&&my>=ny-7&&my<=ny+24){selected=Category.values()[i];page=0;return true;}
        }
        int mainX=x+sidebar+18, mainW=w-sidebar-right-36, cols=3, gap=10;
        int cardW=Math.max(110,(mainW-gap*2)/3),cardH=90;
        List<Module> allVisible=visibleModules();
        int pageCount=Math.max(1,(allVisible.size()+11)/12); page=Math.min(page,pageCount-1);
        List<Module> modules=allVisible.subList(Math.min(page*12,allVisible.size()),Math.min(page*12+12,allVisible.size()));
        for(int i=0;i<modules.size();i++){
            int col=i%cols,row=i/cols,cx=mainX+col*(cardW+gap),cy=y+108+row*(cardH+10);
            if(mx>=cx&&mx<=cx+cardW&&my>=cy&&my<=cy+cardH){
                Module module=modules.get(i);
                if(module.configurable()&&my>=cy+64&&mx>=cx+cardW-80&&mx<=cx+cardW-30){
                    module.adjustValue(mx<cx+cardW-55?-1:1);
                }else if(my<cy+62){module.toggle();ConfigManager.save();}
                return true;
            }
        }
        searchFocused=mx>=mainX&&mx<=mainX+mainW&&my>=y+17&&my<=y+54;
        return true;
    }
    @Override public boolean charTyped(CharacterEvent event){
        int codepoint=event.codepoint();
        if(searchFocused&&!Character.isISOControl(codepoint)){search+=new String(Character.toChars(codepoint));page=0;return true;}
        return super.charTyped(event);
    }
    @Override public boolean keyPressed(KeyEvent event){
        int key=event.key();
        if(searchFocused&&key==256){searchFocused=false;return true;}
        if(searchFocused&&key==259&&!search.isEmpty()){search=search.substring(0,search.length()-1);page=0;return true;}
        if(key==266){page=Math.max(0,page-1);return true;}
        if(key==267){int count=Math.max(1,(visibleModules().size()+11)/12);page=Math.min(count-1,page+1);return true;}
        if(key==256){onClose();return true;}
        return super.keyPressed(event);
    }
    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){ConfigManager.save();super.onClose();}
}
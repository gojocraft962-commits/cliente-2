package com.gojo.client.module;
import net.minecraft.client.Minecraft;
public abstract class Module {
 private final String name, description; private final Category category; private boolean enabled;
 private double value=4.0,minValue=1.0,maxValue=6.0,step=0.5; private boolean configurable;
 protected Module(String name,String description,Category category){this.name=name;this.description=description;this.category=category;}
 protected final void configure(double initial,double min,double max,double step){this.value=initial;this.minValue=min;this.maxValue=max;this.step=step;this.configurable=true;}
 public final void adjustValue(double direction){if(configurable)value=Math.max(minValue,Math.min(maxValue,Math.round((value+direction*step)*100.0)/100.0));}
 public final double value(){return value;} public final boolean configurable(){return configurable;} public final void setValue(double next){if(configurable)value=Math.max(minValue,Math.min(maxValue,Math.round(next/step)*step));}
 public final void toggle(){setEnabled(!enabled);} public final void setEnabled(boolean value){if(enabled==value)return;enabled=value;if(value)onEnable();else onDisable();}
 public void onEnable(){} public void onDisable(){} public void onTick(Minecraft client){}
 public String name(){return name;} public String description(){return description;} public Category category(){return category;} public boolean enabled(){return enabled;}
}
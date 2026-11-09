package com.gojo.client.module;
import net.minecraft.client.Minecraft;
public abstract class Module {
 private final String name, description; private final Category category; private boolean enabled;
 protected Module(String name,String description,Category category){this.name=name;this.description=description;this.category=category;}
 public final void toggle(){setEnabled(!enabled);} public final void setEnabled(boolean value){if(enabled==value)return;enabled=value;if(value)onEnable();else onDisable();}
 public void onEnable(){} public void onDisable(){} public void onTick(Minecraft client){}
 public String name(){return name;} public String description(){return description;} public Category category(){return category;} public boolean enabled(){return enabled;}
}
package com.gojo.client.config;
import com.gojo.client.module.Module; import com.gojo.client.module.Modules; import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader; import java.io.*; import java.nio.file.*;
public final class ConfigManager {
 private static final Path FILE=FabricLoader.getInstance().getConfigDir().resolve("gojoclient.json"); private ConfigManager(){}
 public static void save(){JsonObject root=new JsonObject(),states=new JsonObject();for(Module m:Modules.all())states.addProperty(m.name(),m.enabled());root.add("modules",states);try{Files.createDirectories(FILE.getParent());Files.writeString(FILE,new GsonBuilder().setPrettyPrinting().create().toJson(root));}catch(IOException e){System.err.println("[GOJO] Falha ao salvar config: "+e.getMessage());}}
 public static void load(){if(!Files.exists(FILE))return;try(Reader reader=Files.newBufferedReader(FILE)){JsonObject root=JsonParser.parseReader(reader).getAsJsonObject();if(!root.has("modules"))return;JsonObject states=root.getAsJsonObject("modules");for(Module m:Modules.all())if(states.has(m.name()))m.setEnabled(states.get(m.name()).getAsBoolean());}catch(Exception e){System.err.println("[GOJO] Config inválida: "+e.getMessage());}}
}
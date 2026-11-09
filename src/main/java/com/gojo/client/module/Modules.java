package com.gojo.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.entity.player.Inventory;
import java.util.*;

public final class Modules {
 private static final List<Module> ALL=new ArrayList<>();
 private static final Map<Entity,Boolean> GLOW_STATE=new WeakHashMap<>();
 private static int clickDelay=0, scaffoldDelay=0;
 private Modules(){}
 public static void init(){ALL.clear();
  add(new Module("Auto Sprint","Corre automaticamente quando estiver andando.",Category.MOVEMENT){@Override public void onTick(Minecraft mc){if(mc.player!=null&&mc.options.keyUp.isDown()&&!mc.player.isCrouching())mc.player.setSprinting(true);}});
  add(new Module("Fullbright","Aumenta a gama visual localmente.",Category.RENDER){private double previous=1.0;@Override public void onEnable(){Minecraft mc=Minecraft.getInstance();previous=mc.options.gamma().get();mc.options.gamma().set(16.0);}@Override public void onDisable(){Minecraft.getInstance().options.gamma().set(previous);}});
  add(new Module("Auto Respawn","Solicita respawn quando a tela de morte estiver aberta.",Category.PLAYER){@Override public void onTick(Minecraft mc){if(mc.screen instanceof net.minecraft.client.gui.screens.DeathScreen&&mc.player!=null)mc.player.respawn();}});

  add(new Module("Kill Aura","Ataca automaticamente entidades próximas.",Category.COMBAT){ {configure(3.5,1.0,6.0,0.5);} @Override public void onTick(Minecraft mc){attackNearby(mc,value(),false);} });
  add(new Module("Kill Aura Mobs","Ataca automaticamente mobs próximos.",Category.COMBAT){ {configure(4.0,1.0,8.0,0.5);} @Override public void onTick(Minecraft mc){attackNearby(mc,value(),true);} });
  add(new Module("Aimbot","Aponta para o alvo vivo mais próximo.",Category.COMBAT){ {configure(5.0,1.0,12.0,0.5);} @Override public void onTick(Minecraft mc){aimAtNearest(mc,value(),false);} });
  add(new Module("Auto Click","Repete o ataque enquanto o botão de ataque estiver pressionado.",Category.COMBAT){ {configure(8.0,2.0,20.0,1.0);} @Override public void onTick(Minecraft mc){autoClick(mc,value());} });

  add(new Module("Fly","Voo local experimental; pode ser corrigido pelo servidor.",Category.MOVEMENT){ {configure(1.0,0.2,3.0,0.2);} @Override public void onTick(Minecraft mc){fly(mc,value());} });
  add(new Module("Scaffold","Tenta colocar um bloco sob os pés ao caminhar.",Category.MOVEMENT){ {configure(1.0,1.0,4.0,1.0);} @Override public void onTick(Minecraft mc){scaffold(mc);} });

  add(new Module("Player ESP","Realça jogadores com brilho.",Category.RENDER){@Override public void onTick(Minecraft mc){glowEntities(mc,e->e instanceof net.minecraft.world.entity.player.Player && e!=mc.player);}@Override public void onDisable(){restoreGlow();}});
  add(new Module("Mob ESP","Realça mobs com brilho.",Category.RENDER){@Override public void onTick(Minecraft mc){glowEntities(mc,e->e instanceof Mob);}@Override public void onDisable(){restoreGlow();}});
  add(new Module("Entity ESP","Realça entidades vivas próximas.",Category.RENDER){@Override public void onTick(Minecraft mc){glowEntities(mc,e->e instanceof LivingEntity && e!=mc.player);}@Override public void onDisable(){restoreGlow();}});
  add(new Module("Item ESP","Realça itens largados no mundo.",Category.RENDER){@Override public void onTick(Minecraft mc){glowEntities(mc,e->e instanceof net.minecraft.world.entity.item.ItemEntity);}@Override public void onDisable(){restoreGlow();}});
  add(new Module("Tracers","Realce por brilho de entidades; linhas 3D ainda não implementadas.",Category.RENDER){@Override public void onTick(Minecraft mc){glowEntities(mc,e->e instanceof LivingEntity && e!=mc.player);}@Override public void onDisable(){restoreGlow();}});
  add(new Module("Nametags","Mantém nomes padrão e realça entidades.",Category.RENDER){@Override public void onTick(Minecraft mc){glowEntities(mc,e->e instanceof LivingEntity && e!=mc.player);}@Override public void onDisable(){restoreGlow();}});
  add(new Module("Block Indicator","Destaca o bloco observado.",Category.RENDER){});
  add(new Module("Reach Display","Mostra a distância até o alvo focado.",Category.COMBAT){});
  add(new Module("Target Info","Exibe informações do alvo focado.",Category.COMBAT){});
  add(new Module("Speed Info","Indicador visual de velocidade.",Category.MOVEMENT){});
  add(new Module("Coordinates","Exibe coordenadas do jogador.",Category.PLAYER){});
  add(new Module("Auto Tool","Seleciona ferramenta adequada (experimental).",Category.PLAYER){});
  add(new Module("Inventory Info","Informações do inventário.",Category.PLAYER){});
  add(new Module("Block Info","Informações do bloco observado.",Category.WORLD){});
  add(new Module("Entity Viewer","Lista entidades próximas.",Category.WORLD){});
  add(new Module("Environment Info","Informações do bioma e dimensão.",Category.WORLD){});
  add(new Module("FPS","Exibe os quadros por segundo.",Category.HUD){});
  add(new Module("Ping","Exibe latência quando disponível.",Category.HUD){});
  add(new Module("Durability","Exibe durabilidade dos equipamentos.",Category.HUD){});
  add(new Module("Clock","Exibe relógio local.",Category.HUD){});
  add(new Module("Server Info","Exibe informações da sessão.",Category.HUD){});

  // Additional client modules: listed transparently while their full implementations are developed.
  String[][] more={
   {"COMBAT","Trigger Bot","Ataca o alvo observado automaticamente — em desenvolvimento."},
   {"COMBAT","Aim Assist","Assistência de mira configurável — em desenvolvimento."},
   {"COMBAT","Criticals","Controle de ataques críticos — em desenvolvimento."},
   {"COMBAT","Auto Totem","Gerenciamento automático de totem — em desenvolvimento."},
   {"COMBAT","Hit Sounds","Som personalizado ao acertar — em desenvolvimento."},
   {"COMBAT","Anti Knockback","Controle de repulsão — em desenvolvimento."},
   {"MOVEMENT","Step","Ajuste de subida de blocos — em desenvolvimento."},
   {"MOVEMENT","Spider","Escalada de paredes — em desenvolvimento."},
   {"MOVEMENT","No Slow","Redução de lentidão — em desenvolvimento."},
   {"MOVEMENT","Safe Walk","Evita cair de bordas — em desenvolvimento."},
   {"MOVEMENT","Elytra Helper","Ferramentas para voo com élitros — em desenvolvimento."},
   {"MOVEMENT","Auto Walk","Movimento contínuo — em desenvolvimento."},
   {"MOVEMENT","Jesus","Movimento sobre líquidos — em desenvolvimento."},
   {"RENDER","Storage ESP","Realce de baús e armazenamentos — em desenvolvimento."},
   {"RENDER","Chest ESP","Realce de baús — em desenvolvimento."},
   {"RENDER","Shulker ESP","Realce de caixas de shulker — em desenvolvimento."},
   {"RENDER","Freecam","Câmara livre — em desenvolvimento."},
   {"RENDER","Zoom","Ampliação de visão — em desenvolvimento."},
   {"RENDER","Search","Realce de blocos escolhidos — em desenvolvimento."},
   {"RENDER","No Render","Ocultação seletiva de efeitos — em desenvolvimento."},
   {"RENDER","Trails","Rastros visuais — em desenvolvimento."},
   {"PLAYER","Auto Eat","Consome comida quando necessário — em desenvolvimento."},
   {"PLAYER","Fast Use","Uso rápido de itens — em desenvolvimento."},
   {"PLAYER","Auto Reconnect","Reconexão automática — em desenvolvimento."},
   {"PLAYER","Potion Manager","Gestão de poções — em desenvolvimento."},
   {"PLAYER","Anti AFK","Evita inatividade — em desenvolvimento."},
   {"WORLD","Nuker","Quebra de blocos em área — em desenvolvimento."},
   {"WORLD","Auto Mine","Mineração assistida — em desenvolvimento."},
   {"WORLD","Build Helper","Ferramentas de construção — em desenvolvimento."},
   {"WORLD","Waypoints","Marcação de locais — em desenvolvimento."},
   {"WORLD","World Border","Indicador dos limites do mundo — em desenvolvimento."},
   {"WORLD","Seed Info","Informações da semente — em desenvolvimento."},
   {"HUD","Armor HUD","Mostra armadura equipada — em desenvolvimento."},
   {"HUD","Totem HUD","Indicador de totens — em desenvolvimento."},
   {"HUD","Effects HUD","Mostra efeitos ativos — em desenvolvimento."},
   {"HUD","Compass HUD","Bússola na tela — em desenvolvimento."},
   {"HUD","Direction HUD","Mostra direção — em desenvolvimento."},
   {"HUD","Keystrokes","Mostra teclas pressionadas — em desenvolvimento."},
   {"HUD","Crosshair","Mira configurável — em desenvolvimento."},
   {"HUD","Watermark","Marca GOJO no HUD — em desenvolvimento."},
   {"HUD","Module List","Lista módulos ativos — em desenvolvimento."},
   {"HUD","Notifications","Notificações de módulos — em desenvolvimento."}
  };
  for(String[] e:more)add(new Module(e[1],e[2],Category.valueOf(e[0])){});

 }
 private static void attackNearby(Minecraft mc,double range,boolean mobsOnly){
  if(mc.player==null||mc.level==null||mc.gameMode==null||mc.screen!=null)return;
  LivingEntity target=mc.level.getEntitiesOfClass(LivingEntity.class,mc.player.getBoundingBox().inflate(range),
    e->e!=mc.player&&e.isAlive()&&(!mobsOnly||e instanceof Mob))
    .stream().min(Comparator.comparingDouble(e->e.distanceToSqr(mc.player))).orElse(null);
  if(target!=null&&mc.player.getAttackStrengthScale(0.0F)>=1.0F)mc.gameMode.attack(mc.player,target);
 }
 private static void aimAtNearest(Minecraft mc,double range,boolean mobsOnly){
  if(mc.player==null||mc.level==null||mc.screen!=null)return;
  LivingEntity target=mc.level.getEntitiesOfClass(LivingEntity.class,mc.player.getBoundingBox().inflate(range),
    e->e!=mc.player&&e.isAlive()&&(!mobsOnly||e instanceof Mob))
    .stream().min(Comparator.comparingDouble(e->e.distanceToSqr(mc.player))).orElse(null);
  if(target!=null){
   Vec3 diff=target.getEyePosition().subtract(mc.player.getEyePosition());
   double horizontal=Math.sqrt(diff.x*diff.x+diff.z*diff.z);
   float yaw=(float)(Math.toDegrees(Math.atan2(diff.z,diff.x))-90.0);
   float pitch=(float)(-Math.toDegrees(Math.atan2(diff.y,horizontal)));
   mc.player.setYRot(yaw); mc.player.setXRot(pitch); mc.player.setYHeadRot(yaw);
  }
 }
 private static void autoClick(Minecraft mc,double cps){
  if(mc.player==null||mc.gameMode==null||mc.hitResult==null||!mc.options.keyAttack.isDown()||mc.screen!=null){clickDelay=0;return;}
  if(clickDelay>0){clickDelay--;return;}
  if(mc.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity living && living.isAlive())
    mc.gameMode.attack(mc.player,living);
  else if(mc.hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHit)
    mc.gameMode.startDestroyBlock(blockHit.getBlockPos(),blockHit.getDirection());
  clickDelay=Math.max(1,(int)Math.round(20.0/Math.max(1.0,cps)));
 }
 private static void fly(Minecraft mc,double speed){
  if(mc.player==null||mc.screen!=null)return;
  double vertical=0;
  if(mc.options.keyJump.isDown())vertical=speed;
  if(mc.options.keyShift.isDown())vertical=-speed;
  Vec3 v=mc.player.getDeltaMovement();
  mc.player.setDeltaMovement(v.x,vertical,v.z);
  mc.player.fallDistance=0;
 }
 private static void scaffold(Minecraft mc){
  if(mc.player==null||mc.level==null||mc.gameMode==null||mc.screen!=null||scaffoldDelay-->0)return;
  BlockPos below=BlockPos.containing(mc.player.getX(),mc.player.getY()-1.0,mc.player.getZ());
  BlockState state=mc.level.getBlockState(below);
  if(!state.isAir())return;
  Inventory inv=mc.player.getInventory(); int old=inv.getSelectedSlot(); int slot=-1;
  for(int i=0;i<9;i++)if(inv.getItem(i).getItem() instanceof BlockItem){slot=i;break;}
  if(slot<0)return;
  inv.setSelectedSlot(slot);
  BlockPos support=below.below(); Direction face=Direction.UP;
  Vec3 hit=Vec3.atCenterOf(support).add(0,0.5,0);
  BlockHitResult bhr=new BlockHitResult(hit,face,support,false);
  mc.gameMode.useItemOn(mc.player,InteractionHand.MAIN_HAND,bhr);
  inv.setSelectedSlot(old); scaffoldDelay=3;
 }
 private static void glowEntities(Minecraft mc,java.util.function.Predicate<Entity> filter){
  if(mc.player==null||mc.level==null)return;
  for(Entity e:mc.level.entitiesForRendering())if(e!=mc.player&&filter.test(e)){
    GLOW_STATE.putIfAbsent(e,e.isCurrentlyGlowing());
    e.setGlowingTag(true);
  }
 }
 private static void restoreGlow(){
  for(var entry:GLOW_STATE.entrySet())if(entry.getKey()!=null)entry.getKey().setGlowingTag(entry.getValue());
  GLOW_STATE.clear();
 }
 private static void add(Module m){ALL.add(m);}
 public static List<Module> all(){return Collections.unmodifiableList(ALL);}
 public static List<Module> in(Category c){return ALL.stream().filter(m->m.category()==c).toList();}
 public static void tick(Minecraft client){for(Module m:ALL)if(m.enabled())m.onTick(client);}
 public static Module find(String name){return ALL.stream().filter(m->m.name().equalsIgnoreCase(name)).findFirst().orElse(null);}
}
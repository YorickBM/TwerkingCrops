package Spigot.TwerkingCrops.PlayerEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.Materials;
import Spigot.TwerkingCrops.ToolBox;
import io.netty.util.internal.ThreadLocalRandom;
import Spigot.TwerkingCrops.Materials.EMaterial;

/*
 * Created by Yorick, Last modified on: 11-07-2020
 */
public class PlayerEvents_1_13 implements Listener, PlayerEvents {    
	private Random randomSC = new Random();
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		ArrayList<Block> SeedsInRange = new ArrayList<Block>();
		Player player = e.getPlayer();
		
		// Twerk Enabled, Has Permission, isSneaking, Is not flying
		if (!ToolBox.checkFunctionState("Twerking"))
			return;
		if (!player.hasPermission("Twerk.use"))
			return;
		if(Materials.IsSimilar(player.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air) && !Materials.IsSimilar(player.getLocation().getBlock(), EMaterial.Soil))
			return;
		if (!player.isSneaking())
			return;
		
		//Apply Twerks Per Second
		int twerks = 0; 
        if(Core.getInstance().TwerkData.containsKey(player.getUniqueId())) { twerks = Core.getInstance().TwerkData.get(player.getUniqueId()) + 1; } else { twerks = 1; }
        Core.getInstance().TwerkData.put(player.getUniqueId(), twerks);
		
		//Check if surrounding blocks are seeds
        World world = player.getWorld();
        int yOffset = 0;
        if (Materials.IsSimilar(player.getLocation().getBlock(), EMaterial.Soil)) {
          yOffset++;
        }

        int ConfigRange = Integer.parseInt(Core.getInstance().getConfig().getString("Custom.TwerkRange"));
        int FinalRange = (int) ConfigRange / 2;
      
        for (int x = -FinalRange; x < FinalRange + 1; x++) {
            for (int z = -FinalRange; z < FinalRange + 1; z++) {
				Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX() + x, player.getLocation().getY() + yOffset, player.getLocation().getZ() + z);
				Block block = world.getBlockAt(loc);
				if(block.getType() != Material.AIR)
						SeedsInRange.add(block);
            }
        }

        //Apply Random Effect
        float random = ThreadLocalRandom.current().nextFloat();
        if(!player.hasPermission("Twerk.noRandomizer") && random >= (Float.parseFloat(Core.getInstance().getConfig().getString("Custom.Randomizer")) / 100)) return;
        
        //Run the event
        SeedsInRange.stream().forEach(s -> CheckSeed(s));
        
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		
		if(Materials.IsSimilar(block, EMaterial.Pumpkin) || Materials.IsSimilar(block, EMaterial.Melon)) {
			try {
				Location StemLoc = Core.getInstance().BlockToStem.get(block.getLocation());
				Core.getInstance().StemToBlock.remove(StemLoc);
			}catch(NullPointerException ex) {
				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void CheckSeed(Block block) {
			if(Materials.IsSimilar(block, EMaterial.Cactus) || Materials.IsSimilar(block, EMaterial.Sugar_Cane)) {
				int bonemealPercentage = randomSC.nextInt(5);
				createParticles(block.getLocation());
				
				if(bonemealPercentage >= 3) {
					double x = block.getLocation().getX();
					double y = block.getLocation().getY();
					double z = block.getLocation().getZ();
					
					Block b1 = new Location(block.getLocation().getWorld(), x, y + 1, z).getBlock();
					Block b2 = new Location(block.getLocation().getWorld(), x, y + 2, z).getBlock();
					Block b3 = new Location(block.getLocation().getWorld(), x, y + 3, z).getBlock();
					
					if(Materials.IsSimilar(b1, EMaterial.Air) && Materials.IsSimilar(b2, EMaterial.Air)) {
						b1.setType(block.getType());
					} else if(Materials.IsSimilar(b2, EMaterial.Air) && Materials.IsSimilar(b3, EMaterial.Air)) {
						b2.setType(block.getType());
					}
				}
			} else if(block.getData() != 7) {
				if(Materials.IsSimilar(block, EMaterial.Beetroot_Seeds)) {
					if(block.getData() == 3) {
					return;
					}
				}   
				if(Core.getInstance().GetBonemealer().applyBoneMeal(block))
					createParticles(block.getLocation());
			} else {
				if(Materials.IsSimilar(block, EMaterial.Melon_Stem) || Materials.IsSimilar(block, EMaterial.Pumpkin_Stem)) { //block.getType() == Material.PUMPKIN_STEM || 
					Random ran = new Random();
					int num = ran.nextInt(5);
					
					double x = block.getLocation().getX();
					double y = block.getLocation().getY();
					double z = block.getLocation().getZ();
					
					Block b1 = new Location(block.getLocation().getWorld(), x + 1, y, z).getBlock();
					Block b2 = new Location(block.getLocation().getWorld(), x - 1, y, z).getBlock();
					Block b3 = new Location(block.getLocation().getWorld(), x, y, z + 1).getBlock();
					Block b4 = new Location(block.getLocation().getWorld(), x, y, z - 1).getBlock();
					
					if(!CheckHasBlock(block)) {
						createParticles(block.getLocation());
						if(num == 1) {   				
							if(Materials.IsSimilar(b1, EMaterial.Air) && !EntityInSpace(b1) && !Materials.IsSimilar(b1.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
								checkStem(block, b1);
							} else
							if(Materials.IsSimilar(b2, EMaterial.Air) && !EntityInSpace(b2) && !Materials.IsSimilar(b2.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
								checkStem(block, b2);
							} else
							if(Materials.IsSimilar(b3, EMaterial.Air) && !EntityInSpace(b3) && !Materials.IsSimilar(b3.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
								checkStem(block, b3);
							} else
							if(Materials.IsSimilar(b4, EMaterial.Air) && !EntityInSpace(b4) && !Materials.IsSimilar(b4.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
								checkStem(block, b4);
							}
						} 
					}
				}
			}
	}
	public boolean EntityInSpace(Block b) {
		for(Entity e : b.getChunk().getEntities()){
			if(e.getLocation().distance(b.getLocation())<=1.5){ //1.5 because the entity could be between a block
				return true;
			}	
		}
		return false;
	}
	public boolean CheckHasBlock(Block StemBlock) {
			if(Core.getInstance().StemToBlock.get(StemBlock.getLocation()) != null) {
				return true;
			}
		return false;
	}
	public void checkStem(Block StemBlock, Block Space) {
		if(Materials.IsSimilar(StemBlock, EMaterial.Melon_Stem))
			Materials.SetType(Space, EMaterial.Melon);
		if(Materials.IsSimilar(StemBlock, EMaterial.Pumpkin_Stem))
			Materials.SetType(Space, EMaterial.Pumpkin);
		  
		//Put Data in HashMap
		HashMap<Location, EMaterial> data = new HashMap<Location, EMaterial>();
		data.put(Space.getLocation(), Materials.GetType(Space));
		  
		Core.getInstance().StemToBlock.put(StemBlock.getLocation(), data);
		Core.getInstance().BlockToStem.put(Space.getLocation(), StemBlock.getLocation());
	} 
	public void createParticles(Location loc) {
		if (ToolBox.checkFunctionState("Particles")) {
			Location newLoc = new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.spawnParticle(Particle.VILLAGER_HAPPY, newLoc, 5, 0.2D, 0.2D, 0.2D);
			}
		}
	}
}

package Spigot.TwerkingCrops.PlayerEvents;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.Materials;
import Spigot.TwerkingCrops.Materials.EMaterial;
import Spigot.TwerkingCrops.ToolBox;
import io.netty.util.internal.ThreadLocalRandom;

/*
 * Created by Yorick, Last modified on: 11-07-2020
 */
public class PlayerEvents_1_13 implements Listener, PlayerEvents {    

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		ArrayList<Block> SeedsInRange = new ArrayList<Block>();
		Player player = e.getPlayer();
		
		// Twerk Enabled, Has Permission, isSneaking, Is not flying
		if (!ToolBox.checkFunctionState("Twerking"))
			return;
		if (!player.hasPermission("Twerk.use"))
			return;
		if(Core.getInstance().GetWorldBlacklist().IsBlacklisted(player.getLocation().getWorld().getName()))
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
        Core.DebugPrint("Running twerk event for: " + player.getDisplayName() + "\nHas No Randomizer Permission: " + player.hasPermission("Twerk.noRandomizer"));
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

	public void CheckSeed(Block block) {
		Core.DebugPrint("Running Material Checker for: " + block);
		if(Materials.ContainsType(Materials.GetType(block))) {
			if(Core.getInstance().GetBonemealer().applyBoneMeal(block))
				createParticles(block.getLocation());
		} else Core.DebugPrint("Following material is not registered: " + Materials.TypeConverter(block));
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

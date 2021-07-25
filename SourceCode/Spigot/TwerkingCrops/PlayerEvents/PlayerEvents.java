package Spigot.TwerkingCrops.PlayerEvents;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public interface PlayerEvents {
	
	public void onSneak(PlayerToggleSneakEvent e);
	public void onBlockBreak(BlockBreakEvent e);
	  
	public void CheckSeed(Block block);
	public void createParticles(Location loc);

}

package Spigot.TwerkingCrops.CustomTimer;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.Materials;
import Spigot.TwerkingCrops.Materials.EMaterial;
import Spigot.TwerkingCrops.ToolBox;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public class CustomTimer {
	//TODO: Create blacklist
	//TODO: Save crops by chunk, not by crop
	//TODO: Execute growth per chunk not per crop
	
	boolean reloadTwerk, reloadCrops = false;
	
	private void TwerkPerSecond() {
		new BukkitRunnable()
	      {
	        public void run()
	        {
	        	if(reloadTwerk) {
	        		this.cancel();
	        		reloadTwerk = false;
	        	}
	        	if(ToolBox.checkFunctionState("TwerkPerSecond")) {
	        	 for (Player p : Bukkit.getOnlinePlayers()) {
	        		 if(Core.getInstance().TwerkData.get(p.getUniqueId()) != null) {
	        			 int twerkData = Core.getInstance().TwerkData.get(p.getUniqueId());
		        		 String TwerkAmount = Integer.toString(twerkData);
		        		 if(twerkData >= 2) {
		        			 String message = Core.getInstance().GetLanguageManager().GetValue("TwerkingPerSecond.Shifting").replace("%ShiftingRate%", TwerkAmount);	
		        			 Core.getInstance().GetActionBar().sendActionBar(p, ToolBox.cc(message));
		        		 }
		        		 Core.getInstance().TwerkData.remove(p.getUniqueId());
	        		 }
	        	 	}
	        	}
	        }
	      }.runTaskTimer(JavaPlugin.getPlugin(Core.class), 0L, 20);
	}
	
	private int wheat,carrot,potato,beetroot = 0;
	
	private void CropTimer() {
		wheat = 0;
		carrot = 0;
		potato = 0;
		beetroot = 0;
		
		new BukkitRunnable()
	      {
	        public void run()
	        {
	        	if(reloadCrops) {
	        		this.cancel();
	        		reloadCrops = false;
	        	}
	        	if(ToolBox.checkFunctionState("CustomTime")) {
	        	 
	        		if(wheat >= Core.getInstance().getConfig().getInt("CustomTime.SEEDS")) {
	        			wheat = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Wheat_Seeds)).forEach(s -> Core.getInstance().GetBonemealer().applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		if(carrot >= Core.getInstance().getConfig().getInt("CustomTime.CARROT")) {
	        			carrot = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Carrot)).forEach(s -> Core.getInstance().GetBonemealer().applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		if(potato >= Core.getInstance().getConfig().getInt("CustomTime.POTATO")) {
	        			potato = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Carrot)).forEach(s -> Core.getInstance().GetBonemealer().applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		if(beetroot >= Core.getInstance().getConfig().getInt("CustomTime.BEETROOT")) {
	        			beetroot = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Carrot)).forEach(s -> Core.getInstance().GetBonemealer().applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		
	        		wheat += 1;
	        		carrot += 1;
	        		potato += 1;
	        		beetroot += 1;
	        	}
	        }
	      }.runTaskTimer(JavaPlugin.getPlugin(Core.class), 0L, 20);
	}
	
	
	public void VersionControl() {
		new BukkitRunnable()
	      {
	        public void run()
	        {
	        	Core.getInstance().checkVersion(version -> {
					if(!Core.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
						Core.getInstance().getLogger().log(Level.WARNING, "You are currently not running the newest version");
						Core.getInstance().getLogger().log(Level.WARNING, "Please update to: " + version + " from " + Core.getInstance().getDescription().getVersion());
					}
				});
	        }
	      }.runTaskTimer(JavaPlugin.getPlugin(Core.class), 0L, 86400 * 20);
	}
	
	public void startRunnables()
	{
		//Twerking Per Second Function
		TwerkPerSecond();
		
		//Version Control Every Day (For long term running servers)
		VersionControl();
		
		//CropTimer
		CropTimer();
		
	}
	
	public boolean Restart() {
		try {
			ReloadRunnables();	
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}
	
	public void ReloadRunnables() {
		reloadCrops = true;
		reloadTwerk = true;		
		Core.getInstance().getLogger().log(Level.WARNING, "Restarting ALL bukkit runnables from Twerking-Crops");
		
		new BukkitRunnable()
	      {
	        public void run()
	        {
	        	while(reloadCrops || reloadTwerk) return;
	        	startRunnables();
	        	this.cancel();
	        }
	      }.runTaskTimer(JavaPlugin.getPlugin(Core.class), 0L, 40);
	}
}

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
	
	boolean reload = false;
	
	private void TwerkPerSecond() {
		new BukkitRunnable()
	      {
	        public void run()
	        {
	        	if(reload) this.cancel();
	        	if(ToolBox.checkFunctionState("TwerkPerSecond")) {
	        	 for (Player p : Bukkit.getOnlinePlayers()) {
	        		 if(Core.getInstance().TwerkData.get(p.getUniqueId()) != null) {
	        			 int twerkData = Core.getInstance().TwerkData.get(p.getUniqueId());
		        		 String TwerkAmount = Integer.toString(twerkData);
		        		 if(twerkData >= 2) {
		        			 String message = Core.getInstance().getConfig().getString("Messages.TwerkingPerSecond.Shifting").replace("%ShiftingRate%", TwerkAmount);	
		        			 Core.getInstance().actionBar.sendActionBar(p, ToolBox.cc(message));
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
	        	if(reload) this.cancel();
	        	if(ToolBox.checkFunctionState("CustomTime")) {
	        	 
	        		if(wheat >= Core.getInstance().getConfig().getInt("CustomTime.SEEDS")) {
	        			wheat = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Wheat_Seeds)).forEach(s -> Core.getInstance().boneMealer.applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		if(carrot >= Core.getInstance().getConfig().getInt("CustomTime.CARROT")) {
	        			carrot = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Carrot)).forEach(s -> Core.getInstance().boneMealer.applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		if(potato >= Core.getInstance().getConfig().getInt("CustomTime.POTATO")) {
	        			potato = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Carrot)).forEach(s -> Core.getInstance().boneMealer.applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		if(beetroot >= Core.getInstance().getConfig().getInt("CustomTime.BEETROOT")) {
	        			beetroot = -1;
	        			Core.getInstance().seedsForTimer.stream().filter(s -> Materials.IsSimilar(s.getLocation().getBlock(), EMaterial.Carrot)).forEach(s -> Core.getInstance().boneMealer.applyBoneMeal(s.getLocation().getBlock()));
	        		}
	        		
	        		wheat += 1;
	        		carrot += 1;
	        		potato += 1;
	        		beetroot += 1;
	        	}
	        }
	      }.runTaskTimer(JavaPlugin.getPlugin(Core.class), 0L, 20);
	}
	
	public void startRunnables()
	{
		//Twerking Per Second Function
		TwerkPerSecond();
		
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
		reload = true;
		Core.getInstance().getLogger().log(Level.WARNING, "Restarting ALL bukkit runnables from Twerking-Crops");
		reload = false;
		
		startRunnables();
	}
}

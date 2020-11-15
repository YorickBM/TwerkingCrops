package Spigot.TwerkingCrops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import Spigot.TwerkingCrops.Materials.EMaterial;
import Spigot.TwerkingCrops.ActionBar.ActionBar;
import Spigot.TwerkingCrops.ActionBar.ActionBar_1_11_B;
import Spigot.TwerkingCrops.ActionBar.ActionBar_1_12_A;
import Spigot.TwerkingCrops.ActionBar.ActionBar_1_16_A;
import Spigot.TwerkingCrops.Commands.SetAutoCompleter;
import Spigot.TwerkingCrops.Commands.SetFunctie;
import Spigot.TwerkingCrops.CustomTimer.CustomTimer;
import Spigot.TwerkingCrops.CustomTimer.EventHandlerCustomTimer;
import Spigot.TwerkingCrops.CustomTimer.SeedType;
import Spigot.TwerkingCrops.PlayerEvents.PlayerEvents;
import Spigot.TwerkingCrops.PlayerEvents.PlayerEvents_1_13;
import Spigot.TwerkingCrops.PlayerEvents.PlayerEvents_1_9_ABOVE;

/*
 * Version Fix List:
 * 8.2
 * -> Enchantment's support for 1.13+ (Don't forget check in Toolbox.java)
 * 
 * 8.1
 * -> Pumpkin with face fix 1.13+ (Set it by mine craft ID not material type) 
 * -> Fix performance issues custom crop grow timer (Complete Redo needed)
 * -> Block break Event & Sneak event optimized ✓ 
 * -> 1.16 support ✓  (Actionbar only spigot?!?!?)
 * -> Nether items that are bonemeal affected added
 * -> Mushrooms added
 * -> Custom Materials System ✓
 * --> Applying patricles & bonemeal acting strange
 * 
 * 8.0
 * -> Sugar cane & Cactus Support ✓
 * -> 1.13 & 1.14 & 1.15 support ✓
 * -> Air platforms allowed ✓
 * -> Pumpkin & Melon can't force grow on air anymore ✓
 */

/*
 * Created by Yorick, Last modified on: 12-06-2020
 */
public class Core extends JavaPlugin {
	private static Core instance = null;
	public ActionBar actionBar;
	public PlayerEvents playerEvents;
	public BoneMealer boneMealer;
	
	public Permission playerPermission = new Permission("Twerk.use");
	public Permission staffPermission = new Permission("Twerk.staff");
	
	public String version;
	public ConfigManager cfgm;
	public boolean NotifSpigotOnly = false;
	
	public List<String> Functions = new ArrayList<String>();	  
	public List<SeedType> seedsForTimer = new ArrayList<>();
	  
	public HashMap<UUID, Integer> TwerkData = new HashMap<UUID, Integer>();
	public HashMap<Location, HashMap<Location, EMaterial>> StemToBlock = new HashMap<Location, HashMap<Location, EMaterial>>();
	public HashMap<Location, Location> BlockToStem = new HashMap<Location, Location>();
	public ArrayList<Material> Crops = new ArrayList<Material>();
	public ArrayList<Material> CropsPlaceholder = new ArrayList<Material>();
	
	public static Core getInstance()
	{
		return instance;
  	}
	
	/*
	 * Will run when plugin gets initialized by Spigot/Bukkit
	 */
	public void onEnable() {
		getConfig().options().copyDefaults(true);
	    saveDefaultConfig();
	    
		setupNMS();
	    instance = this;
	    Materials.InitializeMaterials();
	    TreeTypes.InitializeTreeTypes();
	    
	    getServer().getPluginManager().registerEvents(new EventHandlerCustomTimer(), this);
	    getServer().getPluginManager().registerEvents((Listener) playerEvents, this);
		//getServer().getLogger().log(Level.WARNING, "DO NOT DISTRIBUTE THIS PLUGIN, it has no complete sourcecode (This plugin is not made to be distributed use only for your self)");
	    
	    cfgm = new ConfigManager();
	    cfgm.seeds();
	    cfgm.saveSeeds();
	    cfgm.reloadSeeds();
	    
	    ToolBox.InitLocations(Core.getInstance().cfgm.getSeeds().getInt("Ints.SEEDS"));
	    ToolBox.LoadStemsFromConfig();
	    ToolBox.CheckFuncties();
	    
	    CustomTimer timer = new CustomTimer();
	    timer.startRunnables();
	    Commands();
	    
	    PluginManager pmp = getServer().getPluginManager();
	    pmp.addPermission(this.playerPermission);
	    pmp.addPermission(this.staffPermission);
	    
	    if(!Core.getInstance().getConfig().getString("Custom.bStats").contentEquals("FALSE")) {
	    	Metrics metrics = new Metrics(this, 7832);
	    	if(metrics.isEnabled())
	    		Bukkit.getLogger().log(Level.INFO, "bStats has been enabled. You can disbale bStats in your Config.yml.");
	    }
	}
	/*
	 * Will run when plugin gets disabled by Spigot/Bukkit
	 */
	public void onDisable()
	  {
		  
	   ToolBox.SaveCropsToConfig();
	   ToolBox.SaveStemsToConfig();
		  
	    saveConfig();
	  }
	
	/*
	 * Will Configure Commands
	 */
	  public void Commands()
	  {
	    getCommand("set").setExecutor(new SetFunctie());
	    getCommand("set").setTabCompleter(new SetAutoCompleter());
	  }
	
	/*
	 * Will Implement Correct Classes and Imports to let 
	 * Twerking-Crops function properly on your server version!
	 * 
	 * Level.INFO > Every is Just Fine
	 * Level.SEVERE > HUGE ERROR
	 * Level.WARNING > Just a headsup if something aint working
	 */
	private void setupNMS() {
		if (CheckNMS()) {
		      getLogger().log(Level.INFO, "Succesfully enabled correct NMS Classes");
		  } else {
			  getLogger().log(Level.SEVERE, "Failed to find the NMS class corresponding to your version!");
			  getLogger().log(Level.SEVERE, "Please report this error to the Plugin Creator with your server Version");
		      getServer().getPluginManager().disablePlugin(this);
		  }
	}
	/*
	 * This will return true or false depending if we found a NMS classes for the compatible
	 * server version, so every function is compatible!
	 */
	private boolean CheckNMS() {
		try {
	          version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	      } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
	          return false;
	      }
		
		boneMealer = new BoneMealer();

	      getLogger().info("Your server is running version " + version);
	      boolean NMS = true;

	      /*if (version.equals("v1_8_R1")) {
	    	  actionBar = new ActionBar_1_11_B();
	    	  playerEvents = new PlayerEvents_1_8();
	      } else if (version.equals("v1_8_R2")) {
	    	  actionBar = new ActionBar_1_11_B();
	    	  playerEvents = new PlayerEvents_1_8();
	      } else if (version.equals("v1_8_R3")) {
	    	  actionBar = new ActionBar_1_11_B();
	    	  playerEvents = new PlayerEvents_1_8();
	      } else */if (version.equals("v1_9_R2")) {
	    	  actionBar = new ActionBar_1_11_B();
	    	  playerEvents = new PlayerEvents_1_9_ABOVE();
	      } else if (version.equals("v1_10_R1")) {
	    	  actionBar = new ActionBar_1_11_B();
	    	  playerEvents = new PlayerEvents_1_9_ABOVE();
	      } else if (version.equals("v1_11_R1")) {
	    	  actionBar = new ActionBar_1_11_B();
	    	  playerEvents = new PlayerEvents_1_9_ABOVE();
	      } else if (version.equals("v1_12_R1")) {
	    	  playerEvents = new PlayerEvents_1_9_ABOVE();
	    	  actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_13_R1")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_13_R2")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_14_R1")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_15_R1")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_16_R1")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_16_A();
	      } else if (version.equals("v1_16_R2")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_16_A();
	      } else if (version.equals("v1_16_R3")) {
	    	  Materials.InitExtra();
	    	  playerEvents = new PlayerEvents_1_13();
	    	  actionBar = new ActionBar_1_16_A();
	      } else {
	    	  NMS = false;
	      }
	      return NMS;
	}
	
	/*
	 * Puts correct crops into Crops list corresponding to your server version
	 
	  public void initLists(String version)
	  {		  
		  Materials.InitializeMaterials(); //Adds support for EVERY Material!
		  
		  //CropsPlaceholder.add(Material.SAPLING);
		  //CropsPlaceholder.add(Material.SEEDS);
		  CropsPlaceholder.add(Material.POTATO);
		  CropsPlaceholder.add(Material.CARROT);
		  CropsPlaceholder.add(Material.MELON_STEM);
		  CropsPlaceholder.add(Material.PUMPKIN_STEM);
		  CropsPlaceholder.add(Material.GRASS);
		  CropsPlaceholder.add(Material.SUGAR_CANE);
		  CropsPlaceholder.add(Material.CACTUS);
		  CropsPlaceholder.add(Material.BROWN_MUSHROOM);
		  CropsPlaceholder.add(Material.RED_MUSHROOM);
		  
		  if (version.equals("v1_9_R2")) {
			  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_10_R1")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_11_R1")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_12_R1")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_13_R1")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_13_R2")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_14_R1")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      } else if (version.equals("v1_15_R1")) {
	    	  CropsPlaceholder.add(Material.BEETROOT_SEEDS);
	      }
		  
		  List<String> cropsFromConfig = getConfig().getStringList("Crops");
		  for(String str : cropsFromConfig) {
			  Material matr = Material.getMaterial(str);
			  if(CropsPlaceholder.contains(matr))
					  Crops.add(matr);
			  else
				  getLogger().log(Level.WARNING, "Material not supported: " + str + ".");
		  }
	  }
	  */
}

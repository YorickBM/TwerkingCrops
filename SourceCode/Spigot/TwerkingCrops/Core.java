package Spigot.TwerkingCrops;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

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
 * -> Block break Event & Sneak event optimized âœ“ 
 * -> 1.16 support âœ“  (Actionbar only spigot?!?!?)
 * -> Nether items that are bonemeal affected added
 * -> Mushrooms added
 * -> Custom Materials System âœ“
 * --> Applying patricles & bonemeal acting strange
 * 
 * 8.0
 * -> Sugar cane & Cactus Support âœ“
 * -> 1.13 & 1.14 & 1.15 support âœ“
 * -> Air platforms allowed âœ“
 * -> Pumpkin & Melon can't force grow on air anymore âœ“
 */

/*
 * Created by Yorick, Last modified on: 12-06-2020
 */
public class Core extends JavaPlugin {
	private static Core instance = null;
	private ActionBar _actionBar;
	private PlayerEvents _playerEvents;
	private BoneMealer _boneMealer;
	private LanguageManager _languageManager;
	
	public ActionBar GetActionBar() { return _actionBar; }
	public PlayerEvents GetPlayerEvents() { return _playerEvents; }
	public BoneMealer GetBonemealer() { return _boneMealer; }
	public LanguageManager GetLanguageManager() { return _languageManager; }
	
	public Permission playerPermission = new Permission("Twerk.use");
	public Permission staffPermission = new Permission("Twerk.staff");
	public Permission noRandomizerPermission = new Permission("Twerk.noRandomizer");
	
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
		if(!getServer().getPluginManager().isPluginEnabled(this)) return; 
		
	    instance = this;
	    Materials.InitializeMaterials();
	    TreeTypes.InitializeTreeTypes();
	    
	    getServer().getPluginManager().registerEvents(new EventHandlerCustomTimer(), this);
	    getServer().getPluginManager().registerEvents((Listener) _playerEvents, this);

	    cfgm = new ConfigManager();
	    cfgm.seeds();
	    cfgm.saveSeeds();
	    cfgm.reloadSeeds();
	    
	    ToolBox.InitLocations(Core.getInstance().cfgm.getSeeds().getInt("Ints.SEEDS"));
	    ToolBox.LoadStemsFromConfig();
	    ToolBox.CheckFuncties();
	    
	    generateLanguageFiles();
		setupLanguageManager();
		Core.getInstance().Functions.add("TwerkRange");
		Core.getInstance().Functions.add("Randomizer");
		
		if(!getServer().getPluginManager().isPluginEnabled(this)) return; 
	    
	    CustomTimer timer = new CustomTimer();
	    timer.startRunnables();
	    Commands();
	    
	    PluginManager pmp = getServer().getPluginManager();
	    pmp.addPermission(this.playerPermission);
	    pmp.addPermission(this.staffPermission);
	    pmp.addPermission(this.noRandomizerPermission);
	    
	    setupbStats();
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
	 * Setup Version Control
	 */
	public void checkVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try(InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=36533").openStream(); Scanner scanner = new Scanner(inputStream)) {
				
				if(scanner.hasNext())
					consumer.accept(scanner.next());
				
			} catch (IOException ex) {
				getLogger().log(Level.WARNING, "Could not look for updates!");
			}
		});
	}
	
	/*
	* Setup bStats
	*/
	private void setupbStats() {
		if(Core.getInstance().getConfig().getString("Custom.bStats").contentEquals("FALSE")) {
	    	Bukkit.getLogger().log(Level.INFO, "bStats has been disabled. You can enable bStats in your Config.yml. We would advise you to enable it for research purposes.");
	    		
	    } else {
	    	Metrics metrics = new Metrics(this, 7832);
	    	metrics.addCustomChart(new Metrics.DrilldownPie("languages", () -> {
	            Map<String, Map<String, Integer>> map = new HashMap<>();
	            Map<String, Integer> entry = new HashMap<>();
	            String lang = getConfig().getString("Custom.Language");
	            entry.put(lang, 1);
	            
	            if (lang.startsWith("EN") || lang.endsWith("EN")) {
	                map.put("English", entry);
	            }  else if (lang.startsWith("NL") || lang.endsWith("NL")) {
	                map.put("Dutch", entry);
	            } else if (lang.startsWith("FR") || lang.endsWith("FR")) {
	                map.put("French", entry);
	            } else if (lang.startsWith("DE") || lang.endsWith("DE")) {
	                map.put("German", entry);
	            } else {
	                map.put("Other", entry);
	            }
	            return map;
	        }));
	    	
	    	metrics.addCustomChart(new Metrics.SimplePie("customtime", new Callable<String>() {
	            @Override
	            public String call() throws Exception {
	                return Core.getInstance().getConfig().getString("Custom.CustomTime").toLowerCase();
	            }
	        }));
	    	metrics.addCustomChart(new Metrics.SimplePie("particles", new Callable<String>() {
	            @Override
	            public String call() throws Exception {
	                return Core.getInstance().getConfig().getString("Custom.Particles").toLowerCase();
	            }
	        }));
	    	metrics.addCustomChart(new Metrics.SimplePie("randomizer", new Callable<String>() {
	            @Override
	            public String call() throws Exception {
	            	return Core.getInstance().getConfig().getString("Custom.Randomizer").toLowerCase() + "%";
	            }
	        }));
	    	metrics.addCustomChart(new Metrics.SimplePie("twerkpersecond", new Callable<String>() {
	            @Override
	            public String call() throws Exception {
	                return Core.getInstance().getConfig().getString("Custom.TwerkPerSecond").toLowerCase();
	            }
	        }));
	    }
	}
	
	/*
	 * Load all pre translated language files
	 */
	private void generateLanguageFiles() {
		File dir = new File(getDataFolder() + "/lang/", "");
		if(!dir.exists()) dir.mkdir();
		
		//Generate the Paths
		File langEN = new File(dir.getPath(), "EN.local");
		File langNL = new File(dir.getPath(), "NL.local");
		//File langDE = new File(dir.getPath(), "DE.local");
		//File langFR = new File(dir.getPath(), "FR.local");
		
		//Generate The Files
		if(!langEN.exists()) { //Create default EN locale
			try {
				langEN.createNewFile();
				
				FileWriter myWriter = new FileWriter(langEN);
				myWriter.write(
						"Set.NoPerms=&5&lTwerking Crops → &7Whoops, you don''t have the permission to do this!\r\n" + 
						"Set.Error=&5&lTwerking Crops → &7Use /set <%Functions%> <value>\r\n" + 
						"Set.Error.Bool=&5&lTwerking Crops → &7Use /set %Func% <True/False>\r\n" + 
						"Set.Error.Lang=&5&lTwerking Crops → &7Use /set %Func% <%Langs%>\r\n" + 
						"Set.Error.Number=&5&lTwerking Crops → &7Use /set %Func% <0-9+>\r\n" + 
						"Set.Error.Func=&5&lTwerking Crops → &7`%Func%` is not an valid Function, use: `%Functions%`\r\n" + 
						"Set.Succes=&5&lTwerking Crops → &7You succesfully set %Func% to %Result%\r\n" + 
						"Set.NotAble=&5&lTwerking Crops → &7You can''t set %Func% to %Result% because %Reason%\r\n" + 
						"\r\n" + 
						"TwerkingPerSecond.Shifting=* You are &ntwerking&r at &5&n%ShiftingRate% per second *\r\n" + 
						"\r\n" + 
						"Runnables.NoPerms=&5&lTwerking Crops → &7Whoops, you don''t have the permission to do this!\r\n" + 
						"Runnables.Failed=&5&lTwerking Crops → &7Could not restart all Bukkit Runnables, Use /reload to hard restart\r\n" + 
						"Runnables.Succes=&5&lTwerking Crops → &7You succesfully restarted all Bukkit Runnables!"
						);
				myWriter.close();
			} catch (IOException e) {
			}
		}
		//if(!langDE.exists()) { //Create default DE locale
			//try {
				//langDE.createNewFile();
				
				//FileWriter myWriter = new FileWriter(langDE);
				//myWriter.write(
				//		"Set.NoPerms=&5&lTwerking Crops â†’ &7Whoops, you don''t have the permission to do this!\r\n" + 
				//		"Set.Error=&5&lTwerking Crops â†’ &7Use /set <%Functions%> <value>\r\n" + 
				//		"Set.Error.Bool=&5&lTwerking Crops â†’ &7Use /set %Func% <True/False>\r\n" + 
				//		"Set.Error.Lang=&5&lTwerking Crops â†’ &7Use /set %Func% <%Langs%>\r\n" + 
				//		"Set.Error.Number=&5&lTwerking Crops â†’ &7Use /set %Func% <0-9+>\r\n" + 
				//		"Set.Error.Func=&5&lTwerking Crops â†’ &7`%Func%` is not an valid Function, use: `%Functions%`\r\n" + 
				//		"Set.Succes=&5&lTwerking Crops â†’ &7You succesfully set %Func% to %Result%\r\n" + 
				//		"Set.NotAble=&5&lTwerking Crops â†’ &7You can''t set %Func% to %Result% because %Reason%\r\n" + 
				//		"\r\n" + 
				//		"TwerkingPerSecond.Shifting=* You are &ntwerking&r at &5&n%ShiftingRate% per second *\r\n" + 
				//		"\r\n" + 
				//		"Runnables.NoPerms=&5&lTwerking Crops â†’ &7Whoops, you don''t have the permission to do this!\r\n" + 
				//		"Runnables.Failed=&5&lTwerking Crops â†’ &7Could not restart all Bukkit Runnables, Use /reload to hard restart\r\n" + 
				//		"Runnables.Succes=&5&lTwerking Crops â†’ &7You succesfully restarted all Bukkit Runnables!"
				//		);
				//myWriter.close();
			//} catch (IOException e) {
			//}
		//}
		if(!langNL.exists()) { //Create default NL locale
			try {
				langNL.createNewFile();
				
				FileWriter myWriter = new FileWriter(langNL);
				myWriter.write(
						"Set.NoPerms=&5&lTwerking Crops → &7Whoops, je hebt niet de bevoegdheid om dit uit te voeren!\r\n" + 
						"Set.Error=&5&lTwerking Crops → &7Gebruik /set <%Functions%> <waarden>\r\n" + 
						"Set.Error.Bool=&5&lTwerking Crops → &7Gebruik /set %Func% <True/False>\r\n" + 
						"Set.Error.Lang=&5&lTwerking Crops → &7Gebruik /set %Func% <%Langs%>\r\n" + 
						"Set.Error.Number=&5&lTwerking Crops → &7Gebruik /set %Func% <0-9+>\r\n" + 
						"Set.Error.Func=&5&lTwerking Crops → &7`%Func%` is niet gevonden, gebruik: `%Functions%`\r\n" + 
						"Set.Succes=&5&lTwerking Crops → &7Succesvol %Func% gezet naar %Result%\r\n" + 
						"Set.NotAble=&5&lTwerking Crops → &7Je kunt %Func% niet naar %Result% zetten, want %Reason%\r\n" + 
						"\r\n" + 
						"TwerkingPerSecond.Shifting=* Je bent &5&n%ShiftingRate% keer per seconde aan het &ntwerken&r *\r\n" + 
						"\r\n" + 
						"Runnables.NoPerms=&5&lTwerking Crops → &7Whoops, je hebt niet de bevoegdheid om dit te doen!\r\n" + 
						"Runnables.Failed=&5&lTwerking Crops → &7We kunnen niet alle Bukkit Runnables herstarten, gebruik /reload voor een hard reload!\r\n" + 
						"Runnables.Succes=&5&lTwerking Crops → &7We hebben succesvol alle Bukkit Runnables herstart!"
						);
				myWriter.close();
			} catch (IOException e) {
			}
		}
		
	}
	
	/*
	 * Loads correct language file into the system
	 */
	private void setupLanguageManager() {
		_languageManager = new LanguageManager();
		if(_languageManager.load(this, getConfig().getString("Custom.Language"))) {
			Core.getInstance().getLogger().log(Level.INFO, "Successfully set language to " + getConfig().getString("Custom.Language"));
		} else {		
			Core.getInstance().getLogger().log(Level.SEVERE, "Failed to set language to " + getConfig().getString("Custom.Language"));
			Core.getInstance().getLogger().log(Level.SEVERE, "Please select a lanuage file that existis! (Plugin has been disabled)");
			getServer().getPluginManager().disablePlugin(this);
		}
		Core.getInstance().Functions.add("Language");
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
		
		_boneMealer = new BoneMealer();

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
	    	  _actionBar = new ActionBar_1_11_B();
	    	  _playerEvents = new PlayerEvents_1_9_ABOVE();
	      } else if (version.equals("v1_10_R1")) {
	    	  _actionBar = new ActionBar_1_11_B();
	    	  _playerEvents = new PlayerEvents_1_9_ABOVE();
	      } else if (version.equals("v1_11_R1")) {
	    	  _actionBar = new ActionBar_1_11_B();
	    	  _playerEvents = new PlayerEvents_1_9_ABOVE();
	      } else if (version.equals("v1_12_R1")) {
	    	  _playerEvents = new PlayerEvents_1_9_ABOVE();
	    	  _actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_13_R1")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_13_R2")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_14_R1")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_15_R1")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_12_A();
	      } else if (version.equals("v1_16_R1")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_16_A();
	      } else if (version.equals("v1_16_R2")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_16_A();
	      } else if (version.equals("v1_16_R3")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_16_A();
	      } else if (version.contains("v1_17_")) { //Speculation
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_16_A();
	      } else {
	    	  NMS = false;
	      }
	      return NMS;
	}
}

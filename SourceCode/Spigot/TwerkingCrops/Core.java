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
import Spigot.TwerkingCrops.Commands.BlacklistAutoCompleter;
import Spigot.TwerkingCrops.Commands.BlacklistFunctie;
import Spigot.TwerkingCrops.Commands.SetAutoCompleter;
import Spigot.TwerkingCrops.Commands.SetFunctie;
import Spigot.TwerkingCrops.Configuration.Blacklist;
import Spigot.TwerkingCrops.Configuration.ConfigManager;
import Spigot.TwerkingCrops.Configuration.LanguageManager;
import Spigot.TwerkingCrops.CustomTimer.CustomTimer;
import Spigot.TwerkingCrops.CustomTimer.EventHandlerCustomTimer;
import Spigot.TwerkingCrops.CustomTimer.SeedType;
import Spigot.TwerkingCrops.PlayerEvents.PlayerEvents;
import Spigot.TwerkingCrops.PlayerEvents.PlayerEvents_1_13;
import Spigot.TwerkingCrops.PlayerEvents.PlayerEvents_1_9_ABOVE;


/*
 * Created by Yorick, Last modified on: 06-10-2021
 */
public class Core extends JavaPlugin {
	private static Core instance = null;
	private ActionBar _actionBar;
	private PlayerEvents _playerEvents;
	private CustomTimer _timer;
	private BoneMealer _boneMealer;
	private LanguageManager _languageManager;
	
	public ActionBar GetActionBar() { return _actionBar; }
	public PlayerEvents GetPlayerEvents() { return _playerEvents; }
	public CustomTimer GetCustomTimer() { return _timer; }
	public BoneMealer GetBonemealer() { return _boneMealer; }
	public LanguageManager GetLanguageManager() { return _languageManager; }
	public boolean isShutingdown = false;
	
	public Permission playerPermission = new Permission("Twerk.use");
	public Permission staffPermission = new Permission("Twerk.staff");
	public Permission noRandomizerPermission = new Permission("Twerk.noRandomizer");
	
	private ConfigManager _scfgm;
	private Blacklist _wbcfgm, _sbcfgm, _tbcfgm;
	public ConfigManager GetSeedsConfig() { return _scfgm; }
	public Blacklist GetWorldBlacklist() { return _wbcfgm; }
	public Blacklist GetCropBlacklist() { return _sbcfgm; }
	public Blacklist GetTimerBlacklist() { return _tbcfgm; }
	
	public String version;
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
	
	public static void DeveloperPrint(String msg) {
		//Bukkit.getLogger().log(Level.WARNING, msg);
	}
	public static void DebugPrint(String msg) {
		//Bukkit.getLogger().log(Level.SEVERE, msg);
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

	    setupConfiguration();
	    
	    ToolBox.InitLocations(GetSeedsConfig().GetData().getInt("Ints.SEEDS"));
	    ToolBox.LoadStemsFromConfig();
	    ToolBox.CheckFuncties();
	    
	    generateLanguageFiles();
		setupLanguageManager();
		Core.getInstance().Functions.add("TwerkRange");
		Core.getInstance().Functions.add("Randomizer");
		
		if(!getServer().getPluginManager().isPluginEnabled(this)) return; 
	    
	    _timer = new CustomTimer();
	    _timer.initiateRunnables();
	    Commands();
	    
	    PluginManager pmp = getServer().getPluginManager();
	    pmp.addPermission(this.playerPermission);
	    pmp.addPermission(this.staffPermission);
	    pmp.addPermission(this.noRandomizerPermission);
	    
	    setupbStats();
	    if(Core.getInstance().getDescription().getVersion().contains("-dev")) {
	    	getLogger().log(Level.WARNING, "Please note that you are running a developer version.");
	    	getLogger().log(Level.WARNING, "This means that there may be debug messages and we may not be able to provide full support.");
	    	getLogger().log(Level.WARNING, "Only use the developer version if so instructed, other wise download the version from spigot/curse.");
	    }
	}
	
	/*
	 * Will run when plugin gets disabled by Spigot/Bukkit
	 */
	public void onDisable()
	  {
	    isShutingdown = true;

		PluginManager pmp = getServer().getPluginManager();
	    pmp.removePermission(this.playerPermission);
	    pmp.removePermission(this.staffPermission);
	    pmp.removePermission(this.noRandomizerPermission);
		  
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
		
		getCommand("blacklist").setExecutor(new BlacklistFunctie());
		getCommand("blacklist").setTabCompleter(new BlacklistAutoCompleter());
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
				getLogger().log(Level.WARNING, "We could not acces spigot servers to check your plugin version!");
			}
		});
	}
	
	/*
	* Setup bStats
	*/
	private void setupbStats() {
		if(Core.getInstance().getConfig().getString("Custom.bStats").contentEquals("FALSE")) {
			getLogger().log(Level.INFO, "bStats has been disabled. You can enable bStats in your Config.yml. We would advise you to enable it for research purposes.");
	    		
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
						"Blacklist.NoPerms=&5&lTwerking Crops → &7Whoops, you don''t have the permission to do this!\r\n" +
						"Blacklist.Error=&5&lTwerking Crops → &7Use /blacklist <crop/world> <add/remove/list/save> [item]\r\n" +
						"Blacklist.Error.NoItem=&5&lTwerking Crops → &7Please enter an item to %action%.\r\n" +
						"Blacklist.Error.List=&5&lTwerking Crops → &7Please enter a valid blacklist.\r\n" +
						"Blacklist.Error.Action=&5&lTwerking Crops → &7Please enter a valid action.\r\n" +
						"Blacklist.AlreadyOn=&5&lTwerking Crops → &7%item% is already on the %blacklist%-blacklist.\r\n" +
						"Blacklist.NotFound=&5&lTwerking Crops → &7%item% is not found on the %blacklist%-blacklist.\r\n" +
						"Blacklist.Succes=&5&lTwerking Crops → &7Succesfully execute the action: %action% for the %blacklist%-blacklist.\r\n" +
						"Blacklist.List.Header=&5&lTwerking Crops → &7Blacklist items for %blacklist%:\r\n" +
						"Blacklist.List.Item=&5&l→ &7%activeItem%\r\n" +
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
						"Blacklist.NoPerms=&5&lTwerking Crops → &7Whoops, je hebt niet de bevoegdheid om dit uit te voeren!\r\n" +
						"Blacklist.Error=&5&lTwerking Crops → &7Gebruik /blacklist <crop/world> <add/remove/list/save> [item]\r\n" +
						"Blacklist.Error.NoItem=&5&lTwerking Crops → &7Vul alstublieft een item toe om de volgende actie op uit te voeren: %action%\r\n" +
						"Blacklist.Error.List=&5&lTwerking Crops → &7De volgende blacklist niet gevonden: %blacklist%\r\n" +
						"Blacklist.Error.Action=&5&lTwerking Crops → &7De volgende actie kan niet uitgevoerd worden: %action%\r\n" +
						"Blacklist.AlreadyOn=&5&lTwerking Crops → &7%item% is al toegevoegd aan %blacklist%-blacklist.\r\n" +
						"Blacklist.NotFound=&5&lTwerking Crops → &7%item% is niet gevonden op %blacklist%-blacklist.\r\n" +
						"Blacklist.Succes=&5&lTwerking Crops → &7De actie: %action% is succesvol uitgevoerd voor %blacklist%-blacklist.\r\n" +
						"Blacklist.List.Header=&5&lTwerking Crops → &7Blacklist items voor %blacklist%:\r\n" +
						"Blacklist.List.Item=&5&l→ &7%activeItem%\r\n" +
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
	 * Setup all custom config files
	 */
	private void setupConfiguration() {
		_scfgm = new ConfigManager();
	    _wbcfgm = new Blacklist();
	    _sbcfgm = new Blacklist();
	    _tbcfgm = new Blacklist();
	    try {
	    	GetSeedsConfig().Initialize("/Data/Seeds.yml");
	    	GetSeedsConfig().Save();
	    	GetSeedsConfig().Reload();
	    	
	    	GetWorldBlacklist().Initialize("World-Blacklist.yml");
	    	GetWorldBlacklist().Save();
	    	GetWorldBlacklist().Reload();
	    	
	    	GetCropBlacklist().Initialize("Crop-Blacklist.yml");
	    	GetCropBlacklist().Save();
	    	GetCropBlacklist().Reload();

			new ConfigManager().Initialize("CustomTimer.json");
	    	
	    	//GetTimerBlacklist().Initialize("Timer-Blacklist.yml");
	    	//GetTimerBlacklist().Save();
	    	//GetTimerBlacklist().Reload();
		} catch (IOException e) {
			Core.DebugPrint("Error on initializing configuration files: \n" + e);
		}
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
			  getLogger().log(Level.SEVERE, "We have enabled the NMS Classes for the latest tested minecraft version.");
			  getLogger().log(Level.SEVERE, "Due to this, bugs may occur when using this plugin.");
		      //getServer().getPluginManager().disablePlugin(this);
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

	      if (version.equals("v1_9_R2")) {
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
	      } else if (version.equals("v1_16_R")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_16_A();
	      }  else if (version.contains("v1_17_R")) {
	    	  Materials.InitExtra();
	    	  _playerEvents = new PlayerEvents_1_13();
	    	  _actionBar = new ActionBar_1_16_A();
	      } else if (version.contains("v1_18_R")) {
			  Materials.InitExtra();
			  _playerEvents = new PlayerEvents_1_13();
			  _actionBar = new ActionBar_1_16_A();
		  } else {
	    	  NMS = false;
			  Materials.InitExtra();
			  _playerEvents = new PlayerEvents_1_13();
			  _actionBar = new ActionBar_1_16_A();
	      }
	      return NMS;
	}
}

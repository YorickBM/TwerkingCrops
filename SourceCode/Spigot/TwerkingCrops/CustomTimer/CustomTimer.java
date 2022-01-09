package Spigot.TwerkingCrops.CustomTimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import JsonAPI.JsonHandler;
import JsonAPI.Serializers.JsonSerializer;
import Spigot.TwerkingCrops.Materials;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.ToolBox;
import org.json.JSONObject;

/*
 * Created by Yorick, Last modified on: 24-11-2021
 */

class BiomeConfiguration {
	List<String> biomes;
	List<Chunk> activeChunks, inactiveChunks;
	List<Materials.EMaterial> _blacklisted;
	World world;

	int minHeight;
	int maxHeight;

	BukkitRunnable chunkUpdater, cropUpdater;
	Random random;

	public BiomeConfiguration(JSONObject data) {
		biomes = JsonSerializer.JsonArrayToList(data.getJSONArray("Biomes"), String.class);
		minHeight = data.getInt("Minimum");
		maxHeight = data.getInt("Maximum");
		world = Bukkit.getWorld(data.getString("World"));

		activeChunks = new ArrayList<>();
		inactiveChunks = new ArrayList<>();
		_blacklisted = Arrays.asList(new Materials.EMaterial[]{Materials.EMaterial.UNKNOWN, Materials.EMaterial.Air, Materials.EMaterial.Grass, Materials.EMaterial.Kelp, Materials.EMaterial.Water});

		random = new Random();

		chunkUpdater = new BukkitRunnable() {
			@Override
			public void run() {
				if(Core.getInstance().isShutingdown) this.cancel();

				for (Chunk chunk : inactiveChunks.stream().filter(c -> !activeChunks.contains(c)).collect(Collectors.toList())) {
					AtomicBoolean cropFound = new AtomicBoolean(false);
					GoThroughBlocks(chunk, (block -> {
						Materials.EMaterial mat = Materials.GetType(block);
						if(!_blacklisted.contains(mat)) {
							System.out.println("Found crop: " + Materials.GetType(block));
							cropFound.set(true);
						}
					}));
					if(cropFound.get()) activeChunks.add(chunk);
				}

				List<Chunk> remChunks = new ArrayList<>();
				for (Chunk chunk : activeChunks) {
					AtomicBoolean cropFound = new AtomicBoolean(false);
					GoThroughBlocks(chunk, (block -> {
						if(cropFound.get()) return;
						Materials.EMaterial mat = Materials.GetType(block);
						if(!_blacklisted.contains(mat)) cropFound.set(true);
					}));
					if(!cropFound.get()) remChunks.add(chunk);
				}
				activeChunks.removeAll(remChunks);
			}
		};

		cropUpdater  = new BukkitRunnable() {



			
			@Override
			public void run() {
				if(Core.getInstance().isShutingdown) this.cancel();

				System.out.println("Crop Updater - (" + activeChunks.size() + ")");

				for (Chunk chunk : activeChunks) {
					GoThroughBlocks(chunk, (block -> {
						if(Materials.GetType(block) != Materials.EMaterial.UNKNOWN
						&& Materials.GetType(block) != Materials.EMaterial.Air
						&& Materials.GetType(block) != Materials.EMaterial.Water) {
							System.out.println("Crop Found in active chunk!");
							if(random.nextInt(2) != 1) return;
							Core.getInstance().GetBonemealer().applyBoneMeal(block);
						}
					}));
				}
			}
		};
	}

	private void GoThroughBlocks(Chunk chunk, Consumer<Block> callback) {
		for(int x = 0; x < 16; x++){
			for(int z = 0;z < 16; z++){
				for(int y = minHeight; y < maxHeight; y++) {
					callback.accept(chunk.getBlock(x, y, z));
				}
			}
		}
	}

	public void start() {
		chunkUpdater.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Core.class), 0l, 15 * 60 * 20); //Run async every 15 minutes
		cropUpdater.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Core.class), 0l, 20); //Run async every second
	}

	public void cancel() {
		chunkUpdater.cancel();
		cropUpdater.cancel();
	}

	public boolean hasBiome(String name) {
		return biomes.contains(name);
	}
	public boolean matchesWorld(World world) {
		return this.world == world;
	}

	public void registerChunk(Chunk chunk) {
		inactiveChunks.add(chunk);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(Core.getInstance().isShutingdown) this.cancel();

				AtomicBoolean cropFound = new AtomicBoolean(false);
				GoThroughBlocks(chunk, (block -> {
					Materials.EMaterial mat = Materials.GetType(block);
					if(!_blacklisted.contains(mat)) {
						System.out.println("Found crop: " + Materials.GetType(block));
						cropFound.set(true);
						return;
					}
				}));
				if(cropFound.get()) activeChunks.add(chunk);
			}
		}.runTaskAsynchronously(JavaPlugin.getPlugin(Core.class)); //Async check if chunk contains registered crop
	}
}

public class CustomTimer {
	
	boolean reloadTwerk, reloadCrops = false;
	private List<BiomeConfiguration> configuredBiomes;

	private void TwerkPerSecondAsync() {
		new BukkitRunnable()
	      {
	        public void run()
	        {
				if(Core.getInstance().isShutingdown) this.cancel();

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
	      }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Core.class), 0L, 20);
	}

	public void VersionControl() {
		new BukkitRunnable()
	      {
	        public void run()
	        {
				if(Core.getInstance().isShutingdown) this.cancel();

	        	Core.getInstance().checkVersion(version -> {
	        		if(Core.getInstance().getDescription().getVersion().contains("-dev")) return;
					if(!Core.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
						Core.getInstance().getLogger().log(Level.WARNING, "You are currently not running the newest version");
						Core.getInstance().getLogger().log(Level.WARNING, "Please update to: " + version + " from " + Core.getInstance().getDescription().getVersion());
					}
				});
	        }
	      }.runTaskTimer(JavaPlugin.getPlugin(Core.class), 0L, 86400 * 20);
	}

	private void CustomTimerAsync() {

		if(!ToolBox.checkFunctionState("CustomTime")) return;

		JSONObject object = JsonHandler.loadJson(Core.getInstance().getDataFolder() + "/CustomTimer.json");
		configuredBiomes = JsonSerializer.JsonArrayToList(object.getJSONArray("Configurations"), BiomeConfiguration::new);

		configuredBiomes.forEach(biomeConfiguration -> biomeConfiguration.start());
	}

	public void initiateChunkAsync(World world, Chunk chunk) {
		if(!ToolBox.checkFunctionState("CustomTime")) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				if(Core.getInstance().isShutingdown) this.cancel();

				if(!chunk.isLoaded()) return;

				for(int x = 0; x < 16; x++) {
					for(int z = 0; z < 16; z++) {
						Biome biome = chunk.getBlock(x, 0, z).getBiome();
						configuredBiomes.forEach(conf -> {
							if(conf.hasBiome(biome.name()) && conf.matchesWorld(world))
								conf.registerChunk(chunk);
						});
					}
				}
			}
		}.runTaskLaterAsynchronously(JavaPlugin.getPlugin(Core.class), 5 * 20); //Run task after x seconds delay
	}
	
	public void initiateRunnables()
	{
		//Twerking Per Second Function
		TwerkPerSecondAsync();
		
		//Version Control Every Day (For long term running servers)
		VersionControl();

		//Custom timer
		CustomTimerAsync();

	}
}

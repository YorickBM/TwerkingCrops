package Spigot.TwerkingCrops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Block;

import Spigot.TwerkingCrops.Materials.EMaterial;

public class TreeTypes {
	
	public static enum ETreeType { Oak, Large_Oak, Birch, Spruce, Large_Spruce, Jungle, Large_Jungle, Acacia, Dark_Oak, Red_Mushroom, Brown_Mushroom, Crimson_Fungs, Warped_Fungs};
	
	private static HashMap<ETreeType, ArrayList<String>> TreeTypeByType = new HashMap<ETreeType, ArrayList<String>>();
	
	public static void InitializeTreeTypes() {
		ArrayList<String> data = new ArrayList<String>();
		
		data.add("TREE");
		TreeTypeByType.put(ETreeType.Oak, data);
		
		data = new ArrayList<String>();
		data.add("BIG_TREE");
		TreeTypeByType.put(ETreeType.Large_Oak, data);
		
		data = new ArrayList<String>();
		data.add("REDWOOD");
		TreeTypeByType.put(ETreeType.Spruce, data);
		
		data = new ArrayList<String>();
		data.add("MEGA_REDWOOD");
		TreeTypeByType.put(ETreeType.Large_Spruce, data);
		
		data = new ArrayList<String>();
		data.add("SMALL_JUNGLE");
		TreeTypeByType.put(ETreeType.Jungle, data);
		
		data = new ArrayList<String>();
		data.add("JUNGLE");
		TreeTypeByType.put(ETreeType.Large_Jungle, data);
		
		data = new ArrayList<String>();
		data.add("ACACIA");
		TreeTypeByType.put(ETreeType.Acacia, data);
		
		data = new ArrayList<String>();
		data.add("DARK_OAK");
		TreeTypeByType.put(ETreeType.Dark_Oak, data);
		
		data = new ArrayList<String>();
		data.add("BIRCH");
		TreeTypeByType.put(ETreeType.Birch, data);
		
		data = new ArrayList<String>();
		data.add("RED_MUSHROOM");
		TreeTypeByType.put(ETreeType.Red_Mushroom, data);
		
		data = new ArrayList<String>();
		data.add("BROWN_MUSHROOM");
		TreeTypeByType.put(ETreeType.Brown_Mushroom, data);
		
		data = new ArrayList<String>();
		data.add("CRIMSON_FUNGUS");
		TreeTypeByType.put(ETreeType.Crimson_Fungs, data);
		
		data = new ArrayList<String>();
		data.add("WARPED_FUNGUS");
		TreeTypeByType.put(ETreeType.Warped_Fungs, data);
	}
	
	
	public static TreeType GetType(ETreeType type) {
		return TreeType.valueOf(TreeTypeByType.get(type).get(0));
	}

	public static boolean spawnFungus(Location location, ETreeType fungusType) {

		int maxFree = 8;
		//if(!FreeSpace(location, 8, maxFree)) return false;
		
		int height = ThreadLocalRandom.current().nextInt(4,maxFree);
		for(int i = 0; i < height; i++) {
			Materials.SetType(location.clone().add(0, i, 0).getBlock(), EMaterial.Warped_Stem);
		}
		
		generateThreeByThree(location.clone().add(0, height, 0), EMaterial.Warped_Wart_Block, true);
		
		int fullRings = ThreadLocalRandom.current().nextInt(1,3);
		for(int i = 0; i < fullRings; i++) {
			generateFiveByFive(location.clone().add(0, height - 1, 0).subtract(0, i, 0), EMaterial.Warped_Wart_Block, true, true);
		}
		
		return true;		
	}
	
	private static void generateThreeByThree(Location center, EMaterial blockType, boolean AirOnly) {
		
		for(int x = -1; x < 2; x++) {
			for(int z = -1; z < 2; z++) {
				Block block = center.clone().add(x, 0, z).getBlock();
				replace(block, blockType, AirOnly);
			}
		}
	}
	private static void generateFiveByFive(Location center, EMaterial blockType, boolean AirOnly, boolean Hollow) {
		
		for(int x = -2; x < 3; x++) {
			for(int z = -2; z < 3; z++) {
				Block block = center.clone().add(x, 0, z).getBlock();
				if(!Hollow) replace(block, blockType, AirOnly);
				else if(x == -2 || x == 3 || z == -2 || z == 3) replace(block, blockType, AirOnly);
				
			}
		}
	}
	private static void replace(Block block, EMaterial blockType, boolean AirOnly) {
		if(!AirOnly) { 
			Materials.SetType(block, blockType);
		} else if(Materials.IsSimilar(block, EMaterial.Air)) {
			Materials.SetType(block, blockType);
		} else {
			System.out.println("Not air!");
		}
	}

}

package Spigot.TwerkingCrops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sapling;

import Spigot.TwerkingCrops.TreeTypes.ETreeType;

/*
 * Created by Yorick, Last modified on: 01-10-2020
 */

@SuppressWarnings("deprecation")
public class Materials {
	public static enum EMaterial {Air, Water, Bone_Meal, Sea_Grass, Sea_Pickle, Soil, Pumpkin, Pumpkin_Stem, 
		Melon, Melon_Stem, Wheat_Seeds, Carrot, Potato, Grass, Sugar_Cane, Cactus, Brown_Mushroom, Red_Mushroom, Beetroot_Seeds, 
		Oak_Sapling, Dark_Oak_Sapling, Spruce_Sapling, Jungle_Sapling, Birch_Sapling, Acacia_Sapling, Sapling,
		Warped_Fungus, Crimson_Fungus, Fern, Bamboo, Kelp, Tall_Grass, Tall_Fern, Cocoa_Beans, Sweet_Berry_Bush, 
		Weeping_Vines, Twisting_Vines, Azalea_Bush, Cave_Vines, Flowering_Azalea, Azalea, UNKNOWN}
	
	private static HashMap<String, EMaterial> MaterialByType = new HashMap<String, EMaterial>();
	private static HashMap<String, EMaterial> TreeTypesL = new HashMap<String, EMaterial>();
	
	public static boolean IsSimilar(String mat, EMaterial type) {
		try {
			if(MaterialByType.get(mat).equals(type))
				return true;
			return false;
		}catch(Exception ex) {
			return false;
		}
	}
	
	public static boolean IsSimilar(Block mat, EMaterial type) {
		String matType = TypeConverter(mat);
		if(!MaterialByType.containsKey(matType)) {
			//System.out.println("Material not found: " + type.toString() + "\n-> " + mat);
			return false;
		}
			
		if(MaterialByType.get(matType) == type)
			return true;
		return false;
	}
	
	public static String TypeConverter(Block mat) {
		String output = mat.toString();

		String type = "";
		String data = "";
		for(String str : output.split(",")){
			String dataType = str.split("=")[0];
			String dataData = str.split("=")[1];
			
			if(dataType.equals("type")) type = dataData;
			if(dataType.equals("data")) data = dataData.replace("}", "");
		}
		
		switch(type) {
			default:
				return type;
				
			case "SAPLING":
				return type + "|" + data;
		}
	}
	
	public static List<EMaterial> GetTypes(Block mat) {
		List<EMaterial> returnV = new ArrayList<>();
		String type = TypeConverter(mat);
		for(HashMap.Entry<String, EMaterial> pair : MaterialByType.entrySet()) {
			if(type.equalsIgnoreCase(pair.getKey())) {
				returnV.add(pair.getValue());
			}
		}
		return returnV;
	}
	
	public static EMaterial GetType(Block mat) {
		String type = TypeConverter(mat);
		return GetType(type);
	}
	
	public static EMaterial GetType(String type) {
		for(HashMap.Entry<String, EMaterial> pair : MaterialByType.entrySet()) {
			if(type.equalsIgnoreCase(pair.getKey())) {
				return pair.getValue();
			}
		}
		
		return EMaterial.UNKNOWN;
	}
	
	public static String GetValueForType(EMaterial type) {
		for(HashMap.Entry<String, EMaterial> pair : MaterialByType.entrySet()) {
			if(pair.getValue() == type) {
				return pair.getKey();
			}
		}
		
		return "Air";
	}
	
	public static boolean SetType(Block mat, EMaterial type) {	
		boolean placed = false;
		if(MaterialByType.containsValue(type)) {
			for(HashMap.Entry<String, EMaterial> pair : MaterialByType.entrySet()) {
				if(pair.getValue() == type) {
					try {
						mat.setType(Material.valueOf(pair.getKey()));
						placed = true;
						return placed;
					}catch(Exception ex) {
						placed = false;
					}
				}
			}
			
			if(!placed) {
				Core.getInstance().getLogger().log(Level.SEVERE, "Could not place material: " + type);
			}
		}
		return placed;
	}
	
	private static void registerMaterial(EMaterial material, String...aliases) {
		for(String alias : aliases) {
			MaterialByType.put(alias, material);
		}
	}
	private static void registerTreeSpecies(EMaterial material, String...aliases) {
		for(String alias : aliases) {
			TreeTypesL.put(alias, material);
		}
	}
	
	public static void InitializeMaterials() {
		
		//Crops/Blocks
		registerMaterial(EMaterial.Grass, "GRASS", "LONG_GRASS");
		registerMaterial(EMaterial.Tall_Grass, "TALL_GRASS", "LARGE_GRASS");
		registerMaterial(EMaterial.Fern, "FERN");
		registerMaterial(EMaterial.Tall_Fern, "TALL_FERN", "LARGE_FERN");
		
		registerMaterial(EMaterial.Potato, "POTATOES", "POTATO");
		registerMaterial(EMaterial.Carrot, "CARROTS", "CARROT");
		registerMaterial(EMaterial.Wheat_Seeds, "WHEAT", "CROPS");
		registerMaterial(EMaterial.Beetroot_Seeds, "BEETROOTS", "BEETROOT_BLOCK");
		registerMaterial(EMaterial.Sweet_Berry_Bush, "BEE_GROWABLES", "SWEET_BERRY_BUSH");
		
		registerMaterial(EMaterial.Pumpkin_Stem, "PUMPKIN_STEM", "PUMPKIN_SEEDS");
		registerMaterial(EMaterial.Melon_Stem, "MELON_STEM", "MELON_SEEDS");
		registerMaterial(EMaterial.Cocoa_Beans, "COCOA_BEANS", "COCOA");
		
		registerMaterial(EMaterial.Pumpkin, "PUMPKIN");
		registerMaterial(EMaterial.Melon, "MELON_BLOCK");
		registerMaterial(EMaterial.Sugar_Cane, "SUGAR_CANE", "SUGAR_CANE_BLOCK");
		registerMaterial(EMaterial.Cactus, "CACTUS");
		
		registerMaterial(EMaterial.Soil, "SOIL", "FARMLAND");
		registerMaterial(EMaterial.Air, "AIR");
		registerMaterial(EMaterial.Water, "WATER");
		
		registerMaterial(EMaterial.Sea_Grass, "SEAGRASS");
		registerMaterial(EMaterial.Sea_Pickle, "SEA_PICKLE");
		registerMaterial(EMaterial.Kelp, "KELP", "KELP_PLANT");
		
		registerMaterial(EMaterial.Twisting_Vines, "TWISTING_VINES", "TWISTING_VINES_PLANT");
		registerMaterial(EMaterial.Weeping_Vines, "WEEPING_VINES", "WEEPING_VINES_PLANT");
		
		registerMaterial(EMaterial.Cave_Vines, "CAVE_VINES", "CAVE_VINES_PLANT");
		
		registerMaterial(EMaterial.Bamboo, "BAMBOO_SAPLING", "BAMBOO");
		registerMaterial(EMaterial.Sapling, 
				"OAK_SAPLING", "SAPLING|0", 
				"SPRUCE_SAPLING", "SAPLING|1", 
				"BIRCH_SAPLING", "SAPLING|2", 
				"JUNGLE_SAPLING", "SAPLING|3", 
				"ACACIA_SAPLING", "SAPLING|4", 
				"DARK_OAK_SAPLING", "SAPLING|5", 
				"RED_MUSHROOM", "BROWN_MUSHROOM",
				"WARPED_FUNGUS", "CRIMSON_FUNGUS",
				"FLOWERING_AZALEA", "AZALEA");
		
		//Trees
		registerTreeSpecies(EMaterial.Oak_Sapling, "OAK_SAPLING", "SAPLING|0");
		registerTreeSpecies(EMaterial.Spruce_Sapling, "SPRUCE_SAPLING", "SAPLING|1");
		registerTreeSpecies(EMaterial.Birch_Sapling, "BIRCH_SAPLING", "SAPLING|2");
		registerTreeSpecies(EMaterial.Jungle_Sapling, "JUNGLE_SAPLING", "SAPLING|3");
		registerTreeSpecies(EMaterial.Acacia_Sapling, "ACACIA_SAPLING", "SAPLING|4");
		registerTreeSpecies(EMaterial.Dark_Oak_Sapling, "DARK_OAK_SAPLING", "SAPLING|5");
		
		registerTreeSpecies(EMaterial.Red_Mushroom, "RED_MUSHROOM");
		registerTreeSpecies(EMaterial.Brown_Mushroom, "BROWN_MUSHROOM");
		
		registerTreeSpecies(EMaterial.Warped_Fungus, "WARPED_FUNGUS");
		registerTreeSpecies(EMaterial.Crimson_Fungus, "CRIMSON_FUNGUS");
		
		registerTreeSpecies(EMaterial.Flowering_Azalea, "FLOWERING_AZALEA");
		registerTreeSpecies(EMaterial.Azalea, "AZALEA");
	}
	
	public static void InitExtra() {
		MaterialByType.put("MELON", EMaterial.Melon);
	}
	
	public static EMaterial GetTreeType(Block mat) {
		String type = TypeConverter(mat);
		for(HashMap.Entry<String, EMaterial> pair : TreeTypesL.entrySet()) {
			if(type.equalsIgnoreCase(pair.getKey())) {
				return pair.getValue();
			}
		}
		return null;
	}
	
	public static boolean IsTreeSimilar(Block mat, EMaterial type) {
		EMaterial matType = GetTreeType(mat);
		if(matType == type) {
			return true;
		} else {
			//System.out.println("Tree not found: " + type.toString() + "\n-> " + mat);
			return false;
		}
	}
	
	public static void SetTree(Block mat, EMaterial type, ETreeType treeType) {
		
		boolean placed = false;
		if(TreeTypesL.containsValue(type)) {
			for(HashMap.Entry<String, EMaterial> pair : TreeTypesL.entrySet()) {
				if(pair.getValue() == type) {
					try {
						if(pair.getKey().contains("|")) {
							mat.setType(Material.valueOf("SAPLING"));
							BlockState bs = mat.getState();
							MaterialData state = bs.getData();
							
							if(state instanceof Sapling) {
								Sapling tree = (Sapling)state;
								TreeType typeX = TreeTypes.GetType(treeType);

								tree.setSpecies(TreeSpecies.valueOf(typeX.toString()));
							}
							
							bs.setData(state);;
							bs.update(true);
							
						} else
							mat.setType(Material.valueOf(pair.getKey()));
						placed = true;
						return;
					}catch(Exception ex) {
						placed = false;
					}
				}
			}
			
			if(!placed) {
				Core.getInstance().getLogger().log(Level.SEVERE, "Could not place sapling: " + type);
			}
		}
	}
	
	public static boolean ContainsType(EMaterial type) {
		return MaterialByType.containsValue(type);
	}
	
	public static boolean isTypeAllowed(EMaterial type) {
		return type == EMaterial.Beetroot_Seeds || type == EMaterial.Wheat_Seeds || type == EMaterial.Potato || type == EMaterial.Carrot;
	}

}

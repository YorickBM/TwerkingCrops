package Spigot.TwerkingCrops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;

/*
 * Created by Yorick, Last modified on: 01-10-2020
 */

public class Materials {
	public static enum EMaterial {Air, Bone_Meal, Sea_Grass, Sea_Pickle, Soil, Pumpkin, Pumpkin_Stem, 
		Melon, Melon_Stem, Wheat_Seeds, Carrot, Potato, Grass, Sugar_Cane, Cactus, Brown_Mushroom, Red_Mushroom, Beetroot_Seeds, 
		Oak_Sapling, Dark_Oak_Sapling, Spruce_Sapling, Jungle_Sapling, Birch_Sapling, Acacia_Sapling, Sapling,
		Warped_Stem, Warped_Wart_Block}
	
	private static HashMap<String, EMaterial> MaterialByType = new HashMap<String, EMaterial>();
	private static HashMap<String, EMaterial> TreeTypes = new HashMap<String, EMaterial>();
	
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
		
		return null;
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
						System.out.println("Catched!");
					}
				}
			}
			
			if(!placed) {
				Core.getInstance().getLogger().log(Level.SEVERE, "Could not place material: " + type);
			}
		}
		return placed;
	}
	
	public static void InitializeMaterials() {
		MaterialByType.put("GRASS", EMaterial.Grass);
		MaterialByType.put("LONG_GRASS", EMaterial.Grass);
		
		MaterialByType.put("POTATOES", EMaterial.Potato);
		MaterialByType.put("POTATO", EMaterial.Potato);
		
		MaterialByType.put("CARROTS", EMaterial.Carrot);
		MaterialByType.put("CARROT", EMaterial.Carrot);
		
		MaterialByType.put("WHEAT", EMaterial.Wheat_Seeds);
		MaterialByType.put("CROPS", EMaterial.Wheat_Seeds);

		MaterialByType.put("BEETROOTS", EMaterial.Beetroot_Seeds);
		MaterialByType.put("BEETROOT_BLOCK", EMaterial.Beetroot_Seeds);
		
		MaterialByType.put("PUMPKIN_STEM", EMaterial.Pumpkin_Stem);
		MaterialByType.put("PUMPKIN_SEEDS", EMaterial.Pumpkin_Stem);
		
		MaterialByType.put("MELON_STEM", EMaterial.Melon_Stem);
		MaterialByType.put("MELON_SEEDS", EMaterial.Melon_Stem);

		MaterialByType.put("PUMPKIN", EMaterial.Pumpkin);
		
		MaterialByType.put("MELON_BLOCK", EMaterial.Melon);
		
		MaterialByType.put("SUGAR_CANE", EMaterial.Sugar_Cane);
		MaterialByType.put("SUGAR_CANE_BLOCK", EMaterial.Sugar_Cane);
		
		MaterialByType.put("CACTUS", EMaterial.Cactus);
		
		MaterialByType.put("SOIL", EMaterial.Soil);
		MaterialByType.put("FARMLAND", EMaterial.Soil);
		
		MaterialByType.put("SEAGRASS", EMaterial.Sea_Grass);
		
		MaterialByType.put("SEA_PICKLE", EMaterial.Sea_Pickle);
		
		MaterialByType.put("OAK_SAPLING", EMaterial.Sapling);
		MaterialByType.put("SAPLING|0", EMaterial.Sapling);
		MaterialByType.put("SPRUCE_SAPLING", EMaterial.Sapling);
		MaterialByType.put("SAPLING|1", EMaterial.Sapling);
		MaterialByType.put("JUNGLE_SAPLING", EMaterial.Sapling);
		MaterialByType.put("SAPLING|4", EMaterial.Sapling);	
		MaterialByType.put("ACACIA_SAPLING", EMaterial.Sapling);
		MaterialByType.put("SAPLING|2", EMaterial.Sapling);
		MaterialByType.put("BIRCH_SAPLING", EMaterial.Sapling);
		MaterialByType.put("SAPLING|3", EMaterial.Sapling);
		MaterialByType.put("DARK_OAK_SAPLING", EMaterial.Sapling);
		MaterialByType.put("SAPLING|5", EMaterial.Sapling);
		MaterialByType.put("RED_MUSHROOM", EMaterial.Sapling);
		MaterialByType.put("BROWN_MUSHROOM", EMaterial.Sapling);
		
		MaterialByType.put("AIR", EMaterial.Air);
		
		//Trees
		TreeTypes.put("OAK_SAPLING", EMaterial.Oak_Sapling);
		TreeTypes.put("SAPLING|0", EMaterial.Oak_Sapling);
		
		TreeTypes.put("SPRUCE_SAPLING", EMaterial.Spruce_Sapling);
		TreeTypes.put("SAPLING|1", EMaterial.Spruce_Sapling);

		TreeTypes.put("JUNGLE_SAPLING", EMaterial.Jungle_Sapling);
		TreeTypes.put("SAPLING|3", EMaterial.Jungle_Sapling);
		
		TreeTypes.put("ACACIA_SAPLING", EMaterial.Acacia_Sapling);
		TreeTypes.put("SAPLING|4", EMaterial.Acacia_Sapling);
		
		TreeTypes.put("BIRCH_SAPLING", EMaterial.Birch_Sapling);
		TreeTypes.put("SAPLING|2", EMaterial.Birch_Sapling);
		
		TreeTypes.put("DARK_OAK_SAPLING", EMaterial.Dark_Oak_Sapling);
		TreeTypes.put("SAPLING|5", EMaterial.Dark_Oak_Sapling);
		
		TreeTypes.put("RED_MUSHROOM", EMaterial.Red_Mushroom);
		
		TreeTypes.put("BROWN_MUSHROOM", EMaterial.Brown_Mushroom);
	}
	
	public static void InitExtra() {
		MaterialByType.put("MELON", EMaterial.Melon);
	}
	
	public static EMaterial GetTreeType(Block mat) {
		String type = TypeConverter(mat);
		for(HashMap.Entry<String, EMaterial> pair : TreeTypes.entrySet()) {
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
	
	public static void SetTree(Block mat, EMaterial type) {
		
		boolean placed = false;
		if(TreeTypes.containsValue(type)) {
			for(HashMap.Entry<String, EMaterial> pair : TreeTypes.entrySet()) {
				if(pair.getValue() == type) {
					try {
						mat.setType(Material.valueOf(pair.getKey()));
						placed = true;
						return;
					}catch(Exception ex) {
						placed = false;
					}
				}
			}
			
			if(!placed) {
				Core.getInstance().getLogger().log(Level.SEVERE, "Could not place material: " + type);
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

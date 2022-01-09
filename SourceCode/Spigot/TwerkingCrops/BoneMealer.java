package Spigot.TwerkingCrops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import Spigot.TwerkingCrops.Materials.EMaterial;
import Spigot.TwerkingCrops.TreeTypes.ETreeType;
import Spigot.TwerkingCrops.Configuration.Blacklist;

@SuppressWarnings("deprecation")
public class BoneMealer {
	
	public BoneMealer() {
		
	}
	
	public List<Location> CheckTwoByTwo(Block block, EMaterial type) {
		ArrayList<Location> sapplingsInRange = new ArrayList<Location>();
		
		int ConfigRange = 3;
        int FinalRange = (int) ConfigRange / 2;
      
        for (int x = -FinalRange; x < FinalRange + 1; x++) {
            for (int z = -FinalRange; z < FinalRange + 1; z++) {
				Location loc = new Location(block.getLocation().getWorld(), block.getLocation().getX() + x, block.getLocation().getY(), block.getLocation().getZ() + z);
				Block blk = block.getLocation().getWorld().getBlockAt(loc);

				if(Materials.GetTreeType(blk) == type)
					sapplingsInRange.add(blk.getLocation());
            }
        }
	
        if(sapplingsInRange.size() > 3) {
        	for(Location loc : sapplingsInRange) {
        		List<Location> locations = new ArrayList<Location>();
        		locations.add(loc.clone());
        		locations.add(loc.clone().add(-1, 0, 0));
        		locations.add(loc.clone().add(0, 0, -1));
        		locations.add(loc.clone().add(-1, 0, -1));
        		
        		if(sapplingsInRange.containsAll(locations))
        			return locations;
        	}
        }
		
		return null;
	}
	
	public void reset(List<Location> locations, EMaterial type, ETreeType treeType) {
		for(Location loc : locations) {
			if(Materials.IsSimilar(loc.getBlock(), EMaterial.Air)) {
				Materials.SetTree(loc.getBlock(), type, treeType);
			}
		}
	}
	
	private boolean isSucces() {
		Random rand2 = new Random();
		int num2 = rand2.nextInt(8);
		if(num2 >= 2) {
			return false;
		}
		return true;
	}
	
	public boolean applyBoneMeal(Block block) {
		
		try {
		
		//Check for Crops & Trees if blacklisted
		if(
				Core.getInstance().GetCropBlacklist().IsBlacklisted(
						Materials.GetType(block).toString()
						) || 
				Materials.IsSimilar(block, EMaterial.Sapling) && 
				Core.getInstance().GetCropBlacklist().IsBlacklisted(
						Materials.GetTreeType(block).toString()
						)
				)
			return false;
		
		//Check if block is a sapling
        if (Materials.IsSimilar(block, EMaterial.Sapling)) {  

    		if(!isSucces()) return true;
        	
        	switch(Materials.GetTreeType(block)) {
        		case Oak_Sapling:
        			
        			Random rand = new Random();
        			int num = rand.nextInt(5);
        			
        			if(Materials.SetType(block, EMaterial.Air)) {
	        			if(num == 3) {
	        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Large_Oak));
	        				if(Materials.IsSimilar(block, EMaterial.Air))
	        					Materials.SetTree(block, EMaterial.Oak_Sapling,ETreeType.Oak);
	        				return true;
	        			} else {
	        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Oak));
	        				if(Materials.IsSimilar(block, EMaterial.Air))
	        					Materials.SetTree(block, EMaterial.Oak_Sapling, ETreeType.Oak);
	        				return true;
	        			}
        			}
        			break;
        			
        		case Spruce_Sapling:
        			List<Location> locS = CheckTwoByTwo(block, EMaterial.Spruce_Sapling);
        			if(locS != null) {
        				locS.stream().forEach(s -> Materials.SetType(s.getBlock(), EMaterial.Air));
        				locS.get(3).getWorld().generateTree(locS.get(3), TreeTypes.GetType(ETreeType.Large_Spruce));
        				reset(locS, EMaterial.Spruce_Sapling, ETreeType.Spruce);
        				return true;
        			}
        			
        			if(Materials.SetType(block, EMaterial.Air)) {
	    				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Spruce));
	    				if(Materials.IsSimilar(block, EMaterial.Air)) 
	    					Materials.SetTree(block, EMaterial.Spruce_Sapling, ETreeType.Spruce);
	    				return true;
        			}
        			break;
        			
        		case Jungle_Sapling:
        			List<Location> locJ = CheckTwoByTwo(block, EMaterial.Jungle_Sapling);
        			if(locJ != null) {
        				locJ.stream().forEach(s -> Materials.SetType(s.getBlock(), EMaterial.Air));
        				locJ.get(3).getWorld().generateTree(locJ.get(3), TreeTypes.GetType(ETreeType.Large_Jungle));
        				reset(locJ, EMaterial.Jungle_Sapling, ETreeType.Jungle);
        				return true;
        			}
        			
    				if(Materials.SetType(block, EMaterial.Air)) {
	    				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Jungle));
	    				if(Materials.IsSimilar(block, EMaterial.Air))
	    					Materials.SetTree(block, EMaterial.Jungle_Sapling, ETreeType.Jungle);
	        			return true;
    				}
    				break;
        			
        		case Dark_Oak_Sapling:
        			List<Location> locD = CheckTwoByTwo(block, EMaterial.Dark_Oak_Sapling);
        			if(locD != null) {
        				locD.stream().forEach(s -> Materials.SetType(s.getBlock(), EMaterial.Air));
        				locD.get(3).getWorld().generateTree(locD.get(3), TreeTypes.GetType(ETreeType.Dark_Oak));
        				reset(locD, EMaterial.Dark_Oak_Sapling, ETreeType.Dark_Oak);
        				return true;
        			}
        			break;
        			
        		case Acacia_Sapling:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Acacia));
        				if(Materials.IsSimilar(block, EMaterial.Air))
        					Materials.SetTree(block, EMaterial.Acacia_Sapling, ETreeType.Acacia);
        				return true;
        			}
        			break;
        			
        		case Birch_Sapling:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Birch));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Birch_Sapling, ETreeType.Birch);
        				return true;
        				}
        			}
        			break;
        			
        		case Brown_Mushroom:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Brown_Mushroom));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Brown_Mushroom, ETreeType.Brown_Mushroom);
        					return true;
        				}
        			}
        			break;
        			
        		case Red_Mushroom:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Red_Mushroom));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Red_Mushroom, ETreeType.Red_Mushroom);
        					return true;
        				}
        			}
        			break;
        			
        		case Flowering_Azalea:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Flowering_Azalea));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Flowering_Azalea, ETreeType.Flowering_Azalea);
        					return true;
        				}
        			}
        			break;
        			
        		case Azalea:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Azalea));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Azalea, ETreeType.Azalea);
        					return true;
        				}
        			}
        			break;
        			
        		case Warped_Fungus:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Warped_Fungus));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Warped_Fungus, ETreeType.Warped_Fungus);
        				}
        				return true;
        			}
        			break;
        			
        		case Crimson_Fungus:
        			if(Materials.SetType(block, EMaterial.Air)) {
        				block.getLocation().getWorld().generateTree(block.getLocation(), TreeTypes.GetType(ETreeType.Crimson_Fungus));
        				if(Materials.IsSimilar(block, EMaterial.Air)) {
        					Materials.SetTree(block, EMaterial.Crimson_Fungus, ETreeType.Crimson_Fungus);
        				}
        				return true;
        			}
        			break;
        			
        	
        		default:
        			Core.DebugPrint("Following tree type ntot supported: "  + Materials.TypeConverter(block) + " (" + Materials.GetType(block) + ")");
        			return false;
        	}
        	return false;
		} 
        
        Random rand = new Random();
        boolean particles = true;
        switch(Materials.GetType(block)) { //Work: 1.17 - 1.
        
        case Wheat_Seeds:
        case Potato:
        case Carrot:
        	particles = block.getData() != 7;
        	if(particles) updateCropState(block, true);
        	break;
        	
        case Beetroot_Seeds:
        	particles = block.getData() != 3;
        	if(particles) updateCropState(block, true);
        	break;
        	
        case Sweet_Berry_Bush: //TODO: What is the cast class?
        return false;
        
        case Twisting_Vines:
        	growBlockInAir(block, rand.nextInt(21 - 18) + 18, false);
        	break;
        	
        case Weeping_Vines:
        	growBlockInAir(block, rand.nextInt(21 - 18) + 18, true);
        	break;
        	
        case Cave_Vines: //TODO Find out correct cast
        	//particles = !((CaveVinesPlant)block.getState().getData().).isBerries();
        	//if(particles) updateCropState(block, false);
        	break;
        	
        case Cocoa_Beans:
        	particles = ((CocoaPlant)block.getState().getData()).getSize() != CocoaPlantSize.LARGE;
        	if(particles) updateCropState(block, false);
        	break;
        
        case Pumpkin_Stem: //TODO If block is grown, connect stem & disable particles
        case Melon_Stem:   
        	particles = block.getData() != 7;
        	if(particles) { updateRawData(block, true, 7); }
        	else if(isSucces()) { growBlockFromStem(block);  particles = false; }
        	else particles = true;
        	break;
        	
        case Grass: //TODO: Only sets the bottom half, need to set block data for top half buggy for lower verions...
        case Fern:
        	//makeTallInAir(block); 
        	return false;
        	
        case Sea_Grass:  //TODO: Only sets the bottom half, need to set block data for top half buggy for lower verions...
        	//makeTallInWater(block); 
        	return false;
        	
        case Sea_Pickle: //TODO: Does nothing when bonemealed by minecraft (Add 1 pickle is what we do)
        	return true;
        	
        case Kelp:
    		growBlockInWater(block, rand.nextInt(21 - 18) + 18);
        	return true;
        	
        case Bamboo: //TODO: Should be done differently??
        	//growBlockInAir(block, rand.nextInt(21 - 18) + 18);
        	return false;
        	
        case Cactus:
        case Sugar_Cane:
        	growBlockInAir(block, 2, false);
        	return true;
        	
        	default:
        		//Core.DebugPrint("Following crop is not registered: " + Materials.TypeConverter(block));
        		return false;
        }
		
        return particles;
		} catch(Exception ex) {
			
			Blacklist blacklistCrops = Core.getInstance().GetCropBlacklist();
			
			Core.DebugPrint("Exception caught within the BoneMealer. \n "
					+ "Bonemealed-block: " + block + "\n"
					+ "Blacklist not null: " + (blacklistCrops != null) + "\n"
					+ "Materials-convertion: " + Materials.GetType(block).toString() + "\n"
					+ "Is-tree: " + Materials.IsSimilar(block, EMaterial.Sapling) + "\n" + ex);
			
			return false;
		}
	}
	
	public void growBlockFromStem(Block block) { //TODO: Connect stem to block!!
		Core.DebugPrint("Growing a block from stem: " + block);
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		
		//Posible spawn locations
		Block b1 = new Location(block.getLocation().getWorld(), x + 1, y, z).getBlock();
		Block b2 = new Location(block.getLocation().getWorld(), x - 1, y, z).getBlock();
		Block b3 = new Location(block.getLocation().getWorld(), x, y, z + 1).getBlock();
		Block b4 = new Location(block.getLocation().getWorld(), x, y, z - 1).getBlock();
		
		if(Materials.IsSimilar(b1, EMaterial.Air) && !ToolBox.EntityInSpace(b1) && !Materials.IsSimilar(b1.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
			ToolBox.checkStem(block, b1);
		} else
		if(Materials.IsSimilar(b2, EMaterial.Air) && !ToolBox.EntityInSpace(b2) && !Materials.IsSimilar(b2.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
			ToolBox.checkStem(block, b2);
		} else
		if(Materials.IsSimilar(b3, EMaterial.Air) && !ToolBox.EntityInSpace(b3) && !Materials.IsSimilar(b3.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
			ToolBox.checkStem(block, b3);
		} else
		if(Materials.IsSimilar(b4, EMaterial.Air) && !ToolBox.EntityInSpace(b4) && !Materials.IsSimilar(b4.getLocation().clone().add(0, -1, 0).getBlock(), EMaterial.Air)) {
			ToolBox.checkStem(block, b4);
		}
	}
	
	public void makeTallInWater(Block block) {
		
	}
	
	public void makeTallInAir(Block block) {
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		
		Block b = new Location(block.getLocation().getWorld(), x, y + 1, z).getBlock();
		if(Materials.IsSimilar(b, EMaterial.Air))
			Materials.SetType(block, EMaterial.valueOf(ToolBox.toCamelCase("Tall_" + Materials.GetValueForType(Materials.GetType(block)), "_")));
	}
	
	public void growBlockInWater(Block block, int layers) {
		Core.DebugPrint("Growing a block within the water: " + block);
		if(!isSucces()) return;
		
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		
		for(int i = 1; i <= layers; i++) {
			Block b1 = new Location(block.getLocation().getWorld(), x, y + i, z).getBlock();
			//Block b2 = new Location(block.getLocation().getWorld(), x, y + i + 1, z).getBlock();
			
			if(Materials.IsSimilar(b1, EMaterial.Water)) { // && Materials.IsSimilar(b2, EMaterial.Water)
				b1.setType(block.getType());
				break;
			}
		}
	}
	
	public void growBlockInAir(Block block, int layers, boolean growDown) {
		Core.DebugPrint("Growing a block within the air: " + block + "\n Is Down: " + growDown);
		if(!isSucces()) return;
		
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		
		for(int i = 1; i <= layers; i++) {
			Block b1 = new Location(block.getLocation().getWorld(), x, y + i, z).getBlock();
			
			if(growDown) b1 = new Location(block.getLocation().getWorld(), x, y - i, z).getBlock();

			
			if(Materials.IsSimilar(b1, EMaterial.Air)) { // && Materials.IsSimilar(b2, EMaterial.Air)
				b1.setType(block.getType());
				break;
			}
		}
	}
	
	public void updateCropState(Block block, boolean useRandom) {
		Core.DebugPrint("Modifying Crop State for: " + block);
		BlockState bs = block.getState();
		MaterialData state = bs.getData();
		if(state instanceof Crops) {
			Crops crop = (Crops)state;
			
			CropState curState = crop.getState();
			
			Random rand = new Random();
			int data = curState.getData() + 1;
			if(useRandom) data += rand.nextInt(3);
			
			if(data > 7) data = 7;
			crop.setState(CropState.getByData((byte)data));
		} else if (state instanceof CocoaPlant) {
			CocoaPlant plant = (CocoaPlant)state;

        	if(isSucces()) {
        		if(plant.getSize() == CocoaPlantSize.SMALL) plant.setSize(CocoaPlantSize.MEDIUM);
        		else if(plant.getSize() == CocoaPlantSize.MEDIUM) plant.setSize(CocoaPlantSize.LARGE);
        	}
		} else if (state instanceof CaveVinesPlant) {
			CaveVinesPlant plant = (CaveVinesPlant)state;
			if(isSucces()) {
				plant.setBerries(true);
			}
		}
		else Core.DebugPrint("Crop State invalid: " + state);
		
		bs.setData(state);
		bs.update(true);
	}
	
	public void updateRawData(Block block, boolean useRandom, int dataLimit) {
		Core.DebugPrint("Modifying Raw Data for: " + block);
		BlockState bs = block.getState();
		
		Random rand = new Random();
		int data = bs.getData().getData() + 1;
		if(useRandom) data += rand.nextInt(3);
		
		if(data > dataLimit) data = dataLimit;
		bs.setRawData((byte)data);
		bs.update(true);
	}
}

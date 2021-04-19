package Spigot.TwerkingCrops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import Spigot.TwerkingCrops.Materials.EMaterial;
import Spigot.TwerkingCrops.TreeTypes.ETreeType;

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
	
	public boolean applyBoneMeal(Block block) {
		
        if (Materials.IsSimilar(block, EMaterial.Sapling)) {  
        	
    		Random rand2 = new Random();
    		int num2 = rand2.nextInt(8);
    		if(num2 >= 2) {
    			return true;
    		}
        	
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
        	
        		default:
        			//System.out.println("Tree Type Not Supported: " + Materials.TypeConverter(block) + " (" + Materials.GetType(block) + ")");
        			return false;
        	}
		} else if(Materials.IsSimilar(block, EMaterial.Wheat_Seeds) || Materials.IsSimilar(block, EMaterial.Potato) || Materials.IsSimilar(block, EMaterial.Carrot) || Materials.IsSimilar(block, EMaterial.Beetroot_Seeds)) {
			
			BlockState bs = block.getState();
			MaterialData state = bs.getData();
			if(state instanceof Crops) {
				Crops crop = (Crops)state;
				
				CropState curState = crop.getState();
				
				Random rand = new Random();
				int data = curState.getData() + rand.nextInt(3) + 1;
				
				if(data > 7) data = 7;
				crop.setState(CropState.getByData((byte)data));
			} else {
				//System.out.println("Not a Crop (" + state + ")");
			}
			bs.setData(state);
			bs.update(true);
			
		} else if(Materials.IsSimilar(block, EMaterial.Pumpkin_Stem) || Materials.IsSimilar(block, EMaterial.Melon_Stem)) {
		
			BlockState bs = block.getState();
			
			Random rand = new Random();
			int data = bs.getData().getData() + rand.nextInt(3) + 1;
			
			if(data > 7) data = 7;
			bs.setRawData((byte)data);
			bs.update(true);
			
		} else {
			//System.out.print(block);
			return false;
		}
        
        return true;
		
	}

}

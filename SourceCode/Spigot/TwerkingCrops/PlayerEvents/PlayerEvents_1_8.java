package Spigot.TwerkingCrops.PlayerEvents;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
/*
 * Created by Yorick, Last modified on: 12-06-2020
 */
public class PlayerEvents_1_8 implements Listener, PlayerEvents {

	@Override
	public void onSneak(PlayerToggleSneakEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockBreak(BlockBreakEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CheckSeed(Block block) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean EntityInSpace(Block b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean CheckHasBlock(Block StemBlock) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkStem(Block StemBlock, Block Space) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createParticles(Location loc) {
		// TODO Auto-generated method stub
		
	}  
/*	private int randomN = 0;
	private int succes = 0;
	private int EnchLevel = 0;
	private Random random = new Random();
	private Random randomSC = new Random();
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player player = e.getPlayer();  
		if (player.hasPermission("Twerk.use")) {
			if(player.getLocation().clone().add(0, -1, 0).getBlock().getType() == Material.AIR && player.getLocation().getBlock().getType() != Material.SOIL) {
				return;
			}
			if (!player.isSneaking()) {
				return;
			}
			int twerks = 0; 
	        if(Core.getInstance().TwerkData.containsKey(player.getUniqueId())) { twerks = Core.getInstance().TwerkData.get(player.getUniqueId()) + 1; } else { twerks = 1; }
	        Core.getInstance().TwerkData.put(player.getUniqueId(), twerks);
	        
	        //Twerking Function
	        if (Core.getInstance().getConfig().getString("Custom.Twerking").contentEquals("TRUE")) {
	        	//Randomizer Function
		        if (Core.getInstance().getConfig().getString("Custom.Randomizer").contentEquals("TRUE"))
		        {
		          if (EnchLevel == 0) {
		            succes = random.nextInt(5);
		            randomN = 4;
		          }
		          if (EnchLevel == 1) {
		            succes = random.nextInt(4);
		            randomN = 3;
		          }
		          if (EnchLevel == 2) {
		            succes = random.nextInt(3);
		            randomN = 2;
		          }
		          if (EnchLevel == 3) {
		            succes = random.nextInt(2);
		            randomN = 1;
		          }
		          if (succes < randomN) {
		            return;
		          }
		        }
	        	
	            World world = player.getWorld();
	            
	            int yOffset = 0;
	            if (player.getLocation().getBlock().getType() == Material.SOIL) {
	              yOffset++;
	            }
	            
	            int ConfigRange = Core.getInstance().getConfig().getInt("Custom.TwerkRange");
	            int FinalRange = (int) ConfigRange / 2;
	          
	            for (int x = -FinalRange; x < FinalRange + 1; x++) {
	                for (int z = -FinalRange; z < FinalRange + 1; z++) {
						Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX() + x, player.getLocation().getY() + yOffset, player.getLocation().getZ() + z);
						Block block = world.getBlockAt(loc);
						CheckSeed(block);
	                }
	            }
	        }
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		if(block.getType() == Material.MELON_BLOCK || block.getType() == Material.PUMPKIN) {
			if(Core.getInstance().BlockToStem.containsKey(block.getLocation())) {
				Location StemLoc = Core.getInstance().BlockToStem.get(block.getLocation());
				Core.getInstance().StemToBlock.remove(StemLoc);
			}
		}
	}
	@SuppressWarnings("deprecation")
	public void CheckSeed(Block block) {
		if (Core.getInstance().Crops.contains(block.getType()))
		{
			if(block.getType() == Material.SUGAR_CANE_BLOCK || block.getType() == Material.CACTUS) {
				int bonemealPercentage = randomSC.nextInt(5);
				createParticles(block.getLocation());
				
				if(bonemealPercentage >= 3) {
					double x = block.getLocation().getX();
					double y = block.getLocation().getY();
					double z = block.getLocation().getZ();
					
					Block b1 = new Location(block.getLocation().getWorld(), x, y + 1, z).getBlock();
					Block b2 = new Location(block.getLocation().getWorld(), x, y + 2, z).getBlock();
					Block b3 = new Location(block.getLocation().getWorld(), x, y + 3, z).getBlock();
					
					if(b1.getType() == Material.AIR && b2.getType() == Material.AIR) {
						b1.setType(block.getType());
					} else if(b2.getType() == Material.AIR && b3.getType() == Material.AIR) {
						b2.setType(block.getType());
					}
				}
			} else if(block.getData() != 7) {   		
				MinecraftReflectionProvider.boneMeal(block.getLocation());
				createParticles(block.getLocation());
				} else {
					if(block.getType() == Material.PUMPKIN_STEM || block.getType() == Material.MELON_STEM) {
						Random ran = new Random();
						int num = ran.nextInt(5);
						
						double x = block.getLocation().getX();
						double y = block.getLocation().getY();
						double z = block.getLocation().getZ();
						
						Block b1 = new Location(block.getLocation().getWorld(), x + 1, y, z).getBlock();
						Block b2 = new Location(block.getLocation().getWorld(), x - 1, y, z).getBlock();
						Block b3 = new Location(block.getLocation().getWorld(), x, y, z + 1).getBlock();
						Block b4 = new Location(block.getLocation().getWorld(), x, y, z - 1).getBlock();
						
						if(!CheckHasBlock(block)) {
						createParticles(block.getLocation());
						if(num == 1) {   				
							if(b1.getType() == Material.AIR && !EntityInSpace(b1) && b1.getLocation().clone().add(0, -1, 0).getBlock().getType() != Material.AIR) {
								checkStem(block, b1);
							} else
							if(b2.getType() == Material.AIR && !EntityInSpace(b2) && b2.getLocation().clone().add(0, -1, 0).getBlock().getType() != Material.AIR) {
								checkStem(block, b2);
							} else
							if(b3.getType() == Material.AIR && !EntityInSpace(b3) && b3.getLocation().clone().add(0, -1, 0).getBlock().getType() != Material.AIR) {
								checkStem(block, b3);
							} else
							if(b4.getType() == Material.AIR && !EntityInSpace(b4) && b4.getLocation().clone().add(0, -1, 0).getBlock().getType() != Material.AIR) {
								checkStem(block, b4);
							}
						}
					}
				}
			}
		}
	}
	public boolean EntityInSpace(Block b) {
		for(Entity e : b.getChunk().getEntities()){
			if(e.getLocation().distance(b.getLocation())<=1.5){ //1.5 because the entity could be between a block
				return true;
			}	
		}
		return false;
	}
	public boolean CheckHasBlock(Block StemBlock) {
		boolean IsFacing = false;  
			if(Core.getInstance().StemToBlock.get(StemBlock.getLocation()) != null) {
				IsFacing = true;
			}
		return IsFacing;
	}
	public void checkStem(Block StemBlock, Block Space) {
		if(StemBlock.getType() == Material.MELON_STEM)
		Space.setType(Material.MELON_BLOCK);
		if(StemBlock.getType() == Material.PUMPKIN_STEM)
		Space.setType(Material.PUMPKIN);
		  
		//Put Data in HashMap
		HashMap<Location, Material> data = new HashMap<Location, Material>();
		data.put(Space.getLocation(), Space.getType());
		  
		Core.getInstance().StemToBlock.put(StemBlock.getLocation(), data);
		Core.getInstance().BlockToStem.put(Space.getLocation(), StemBlock.getLocation());
	}  
	@SuppressWarnings("deprecation")
	public void createParticles(Location loc) {
		if (Core.getInstance().getConfig().getString("Custom.Particles").equals("TRUE")) {
			Location newLoc = new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D);
			for (Player p : Bukkit.getOnlinePlayers()) {
				Random ran = new Random();
					  
				for(int i = 0; i < 15; i++) {
					double ranX = (ran.nextInt(45)) / 100.0;
					double ranY = (ran.nextInt(30)) / 100.0;
					double ranZ = (ran.nextInt(45)) / 100.0;
					p.playEffect(newLoc.clone().add(new Location (loc.getWorld(), ranX, ranY, ranZ)), Effect.HAPPY_VILLAGER, 5);
				}
			}
		}
	}
	*/
}

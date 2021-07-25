package Spigot.TwerkingCrops.Configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import Spigot.TwerkingCrops.Core;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public class ConfigManager {
  private File Setupf;
  private FileConfiguration Setup;

  public void Initialize(String file) throws IOException {
	  File dataFolder = Core.getInstance().getDataFolder();
	  if(!dataFolder.exists()) dataFolder.mkdir();
	  
	  this.Setupf = new File(dataFolder, file);
	  if(!this.Setupf.exists()) LoadDefaults();
	  
	  this.Reload();
	  
  }
  
  public void LoadDefaults() {
	  Core.getInstance().saveResource(Setupf.getName(), false);
  }
  
  public void Save() {
		try {
			this.Setup.save(this.Setupf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
  }
  
  public FileConfiguration GetData() {
	  return this.Setup;
  }
  
  public void Reload() {
	  this.Setup = YamlConfiguration.loadConfiguration(this.Setupf);
  }
  
}

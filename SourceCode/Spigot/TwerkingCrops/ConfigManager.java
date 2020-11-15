package Spigot.TwerkingCrops;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public class ConfigManager {
  private File Setupf;
  private FileConfiguration Setup;
  
  public void seeds()
  {
    if (!Core.getInstance().getDataFolder().exists()) {
      Core.getInstance().getDataFolder().mkdir();
    }
    this.Setupf = new File(Core.getInstance().getDataFolder(), "Seeds.yml");
    if (!this.Setupf.exists()) {
      try
      {
        this.Setupf.createNewFile();
      }
      catch (IOException e)
      {
        Bukkit.getServer().getConsoleSender().sendMessage("Could not create All Config Files!");
      }
    }
    this.Setup = YamlConfiguration.loadConfiguration(this.Setupf);
  }
  
  public FileConfiguration getSeeds()
  {
    return this.Setup;
  }
  
  public void saveSeeds()
  {
    try
    {
      this.Setup.save(this.Setupf);
    }
    catch (IOException e)
    {
      Bukkit.getServer().getConsoleSender().sendMessage("Could not save the Seeds.yml file");
    }
  }
  
  public void reloadSeeds()
  {
    this.Setup = YamlConfiguration.loadConfiguration(this.Setupf);
  }
}

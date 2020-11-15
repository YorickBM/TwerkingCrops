package Spigot.TwerkingCrops.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.ToolBox;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public class SetFunctie implements CommandExecutor {
  public static String Bool = "Empty";
  public static String Func = "Empty";
  public static String Reason = "No Reason";
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
	  try {
		    if (cmd.getName().equalsIgnoreCase("set"))
		    {
		      if (!sender.hasPermission("Twerk.Staff"))
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().getConfig().getString("Messages.Set.NoPerms")));
		        return true;
		      }
		      if (args.length <= 1)
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().getConfig().getString("Messages.Set.Error")));
		        return true;
		      }
		      Bool = args[1];
		      Func = args[0];
		      
		      if (!Core.getInstance().Functions.contains(Func))
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().getConfig().getString("Messages.Set.Func")));
		        return true;
		      }
		      if ((!Bool.equalsIgnoreCase("true")) && (!Bool.equalsIgnoreCase("false")))
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().getConfig().getString("Messages.Set.Bool")));
		        return true;
		      }
		      ToolBox.UpdateConfig("set", new String[] { Bool, Func });
		      sender.sendMessage(ToolBox.cc(Core.getInstance().getConfig().getString("Messages.Set.Succes")));
		      return true;
		    }
		    return false; 
	  } catch (NullPointerException e) {
		  Core.getInstance().getLogger().log(Level.SEVERE, "An error occured on the Set Command function > Error log:", e);
		  return false;
	  }
  }
}

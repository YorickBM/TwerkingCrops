package Spigot.TwerkingCrops.Commands;

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
  public static String Result = "Empty";
  public static String Func = "Empty";
  public static String Reason = "No Reason";
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
	  try {
		    if (cmd.getName().equalsIgnoreCase("set"))
		    {
		      if (!sender.hasPermission("Twerk.Staff"))
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.NoPerms")));
		        return true;
		      }
		      if (args.length <= 1)
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Error")));
		        return true;
		      }
		      Result = args[1];
		      Func = args[0];
		      
		      if (!Core.getInstance().Functions.contains(Func))
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Func")));
		        return true;
		      }
		      if(Func.equalsIgnoreCase("Language") && !Core.getInstance().GetLanguageManager().GetAllLanguages().contains(Result)) {
		    	  sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Lang")));
		    	  return true;
		      }
		      if ((!Result.equalsIgnoreCase("true")) && (!Result.equalsIgnoreCase("false")))
		      {
		        sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Bool")));
		        return true;
		      }
		      ToolBox.UpdateConfig("set", new String[] { Result, Func });
		      sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Succes")));
		      return true;
		    }
		    return false; 
	  } catch (NullPointerException e) {
		  Core.getInstance().getLogger().log(Level.SEVERE, "An error occured on the Set Command function > Error log:", e);
		  return false;
	  }
  }
}

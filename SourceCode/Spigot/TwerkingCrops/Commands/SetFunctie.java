package Spigot.TwerkingCrops.Commands;

import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.ToolBox;

/*
 * Created by Yorick, Last modified on: 13-05-2021
 */
public class SetFunctie implements CommandExecutor {
	
	public static String Result = "Empty";
	public static String Func = "Empty";
	public static String Reason = "No Reason";
  
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("set")) {
				
				//Requirements checks
				if (!sender.hasPermission("Twerk.Staff")) {
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.NoPerms")));
					return true;
				}
  
				if (args.length <= 1) {
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Error")));
					return true;
				}
 
				//Initializing variable names
				Result = args[1];
				Func = args[0];
  
				//Checking if function exists
				if (!Core.getInstance().Functions.contains(Func)) {
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Error.Func")));
					return true;
				}
  
				//Error checking
				switch(Func.toLowerCase()) {
  
				case "twerkrange":
				case "randomizer":
					if (Pattern.matches("[a-zA-Z]+", Result) == true) {
						sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Error.Number")));
						return true;
					}
					break;
	  
				case "language":
					if(!Core.getInstance().GetLanguageManager().GetAllLanguages().contains(Result)) {
						sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Error.Lang")));
						return true;
					}
					Core.getInstance().GetLanguageManager().load(Core.getInstance(), Result.toUpperCase());
					break;
  
				default:
					if ((!Result.equalsIgnoreCase("true")) && (!Result.equalsIgnoreCase("false"))) {
						sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Error.Bool")));
						return true;
					}
					break;
				}      
  
				//Updating configuration file
				ToolBox.UpdateConfig("set", new String[] { Result, Func });
				sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Set.Succes")));
				
			}
			return true;
	    
		} catch (NullPointerException e) {
			Core.getInstance().getLogger().log(Level.SEVERE, "An error occured on the Set Command function > Error log:", e);
			return false;
		}
	}
}

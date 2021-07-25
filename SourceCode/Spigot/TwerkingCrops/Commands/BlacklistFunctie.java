package Spigot.TwerkingCrops.Commands;

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Spigot.TwerkingCrops.Core;
import Spigot.TwerkingCrops.ToolBox;

public class BlacklistFunctie implements CommandExecutor {
	
	public static String Action = "add";
	public static String Blacklist = "crop";
	public static String Item = "CRIMSON_FUNGUS";
	public static int ActiveList = -1;
  
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("blacklist")) {
				//Requirements checks
				if (!sender.hasPermission("Twerk.Staff")) {
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.NoPerms")));
					return true;
				}
  
				if (args.length <= 1) {
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.Error")));
					return true;
				}
				
				Action = args[1];
				Blacklist = args[0];
				
				if (!Action.equalsIgnoreCase("save") && !Action.equalsIgnoreCase("list") && !Action.equalsIgnoreCase("reload")) {
					if(args.length <= 2) {
						sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.Error.NoItem")));
						return true;
					}
					Item = args[2];
				}

				switch(Blacklist.toLowerCase()) {
				case "world":
					ActiveList = 0;
					break;
					
				case "crop":
					ActiveList = 1;
					break;
					
					default:
						sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.Error.List")));
						return true;
				}
				
				switch(Action.toLowerCase()) {
				case "add":
					if(ActiveList == 0) {
						if(!Core.getInstance().GetWorldBlacklist().AddItem(Item)) {
							sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.AlreadyOn")));
							return true;
						}
					} else if( ActiveList == 1) {
						if(!Core.getInstance().GetCropBlacklist().AddItem(Item)) {
							sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.AlreadyOn")));
							return true;
						}
					}
						if(ActiveList == 0) {
							Core.getInstance().GetWorldBlacklist().GetBlacklistItems().stream().forEach(item -> sender.sendMessage("> " + item));
						} else {
							Core.getInstance().GetCropBlacklist().GetBlacklistItems().stream().forEach(item -> sender.sendMessage("> " + item));
						}
					
					break;
					
				case "remove":
				case "delete":
					if(ActiveList == 0 && !Core.getInstance().GetWorldBlacklist().RemoveItem(Item) || ActiveList == 1 && !Core.getInstance().GetCropBlacklist().RemoveItem(Item)) {
						sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.NotFound")));
						return true;
					}
					break;
					
				case "save":
				case "reload":
					if(ActiveList == 0) {
						Core.getInstance().GetWorldBlacklist().Save();
						Core.getInstance().GetWorldBlacklist().Reload();
					} else {
						Core.getInstance().GetCropBlacklist().Save();
						Core.getInstance().GetCropBlacklist().Reload();
					}
					break;
					
				case "list":
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.List.Header")));
					if(ActiveList == 0) {
						Core.getInstance().GetWorldBlacklist().GetBlacklistItems().stream().forEach(item -> sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.List.Item").replace("%activeItem%", item))));
					} else {
						Core.getInstance().GetCropBlacklist().GetBlacklistItems().stream().forEach(item -> sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.List.Item").replace("%activeItem%", item))));
					}
					return true;
					
				default:
					sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.Error.Action")));
					return true;
				}
				
				sender.sendMessage(ToolBox.cc(Core.getInstance().GetLanguageManager().GetValue("Blacklist.Succes")));
				return true;
				
			}
			return true;
		} catch (NullPointerException e) {
			Core.getInstance().getLogger().log(Level.SEVERE, "An error occured on the Blacklist Command function > Error log:", e);
			return false;
		}
	}

}

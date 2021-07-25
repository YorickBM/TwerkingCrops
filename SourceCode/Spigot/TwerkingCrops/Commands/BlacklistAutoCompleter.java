package Spigot.TwerkingCrops.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class BlacklistAutoCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
		if (!sender.hasPermission("Twerk.Staff")) return new ArrayList<String>();
		
		List<String> lists = new ArrayList<String>();
		lists.add("crop");
		lists.add("world");
		
		List<String> actions = new ArrayList<String>();
		actions.add("add");
		actions.add("remove");
		actions.add("save");
		actions.add("list");
		
		if(cmd.getName().equalsIgnoreCase("blacklist") && args.length == 1) return lists;
		if(cmd.getName().equalsIgnoreCase("blacklist") && args.length == 2) return actions; 
		
		switch(args[0].toLowerCase().trim()) {
			default:
				 return new ArrayList<String>();
		}
	}

}

package Spigot.TwerkingCrops.Configuration;

import java.util.List;
import java.util.stream.Collectors;

public class Blacklist extends ConfigManager {

	public boolean IsBlacklisted(String item) {
		if(!HasItems()) return false;
		item = item.toLowerCase();
		
		return this.GetBlacklistItems().contains(item);
	}
	
	public boolean HasItems() {
		return this.GetBlacklistItems().size() > 0;
	}
	
	public List<String> GetBlacklistItems() {
		return super.GetData().getStringList("Blacklist").stream().distinct().map(String::toLowerCase).collect(Collectors.toList());
	}
	
	public boolean AddItem(String item) {
		if(this.GetBlacklistItems().contains(item.toLowerCase())) return false;
		List<String> data = this.GetBlacklistItems();
		data.add(item);
		super.GetData().set("Blacklist", data);
		return true;
	}
	
	public boolean RemoveItem(String item) {
		if(!this.GetBlacklistItems().contains(item.toLowerCase())) return false;
		List<String> data = this.GetBlacklistItems();
		data.remove(item.toLowerCase());
		super.GetData().set("Blacklist", data);
		return true;
	}
	
}

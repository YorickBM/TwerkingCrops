package Spigot.TwerkingCrops.CustomTimer;

import org.bukkit.Location;

import Spigot.TwerkingCrops.Materials.EMaterial;
import Spigot.TwerkingCrops.ToolBox;

public class SeedType {
	
	private EMaterial _type;
	private Location _location;
	
	public SeedType(EMaterial type, Location location) {
		_type = type;
		_location = location;
	}
	
	public SeedType(String type, String location) {
		this(EMaterial.valueOf(type), ToolBox.stringToLocation(location.split(",")));
	}
	
	public EMaterial getType() {
		return _type;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	public String toString() {
		String type = _type.toString();
		String location = ToolBox.locationToString(_location);
		return type + ";" + location;
	}
	
	public boolean check(EMaterial mat, Location loc) {
		if(_type == mat && _location.getBlockX() == loc.getBlockX() && _location.getBlockY() == loc.getBlockY() && _location.getBlockZ() == loc.getBlockZ()) {
			return true;
		} else {
			return false;
		}
	}

}

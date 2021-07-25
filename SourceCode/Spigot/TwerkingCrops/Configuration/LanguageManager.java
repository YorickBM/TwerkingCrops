package Spigot.TwerkingCrops.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Spigot.TwerkingCrops.Core;

public class LanguageManager {
	
	Map<String, String> _values;
	
	public LanguageManager() {
		_values = new HashMap<>();
	}
	
	public boolean load(Core core, String lang) {
		
		File dir = new File(core.getDataFolder() + "/lang/", "");
		if(!dir.exists()) return false; //path not found
		
		File file = new File(dir.getPath(), lang + ".local");
		
		try {
			Scanner myReader = new Scanner(file);
			if(_values.size() > 0) _values.clear();
			
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				if(line.length() < 2) continue;
				
				String[] data = line.split("=");
				_values.put(data[0], data[1]);
			}
			myReader.close();
			return true;
			
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	public List<String> GetAllLanguages() {
		File dir = new File(Core.getInstance().getDataFolder() + "/lang/");
		List<String> foundLanguages = new ArrayList<String>();
		
		for (final File fileEntry : dir.listFiles()) {
			if(fileEntry.isDirectory()) continue;
			else if(fileEntry.getName().endsWith(".local")) foundLanguages.add(fileEntry.getName().replace(".local", ""));
		}
		
		return foundLanguages;
	}
	
	public String GetValue(String key) {
		if(_values.containsKey(key))
			return _values.get(key);
		
		return "Could not find any local for [" + key + "]";
	}

}

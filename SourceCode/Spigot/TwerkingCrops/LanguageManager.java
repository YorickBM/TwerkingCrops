package Spigot.TwerkingCrops;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LanguageManager {
	
	Map<String, String> _values;
	
	public LanguageManager() {
		_values = new HashMap<>();
	}
	
	public boolean load(Core core, String lang) {
		
		File dir = new File(core.getDataFolder() + "/lang/", "");
		if(!dir.exists()) dir.mkdir();
		File file = new File(dir.getPath(), lang + ".local");
		if(lang.equalsIgnoreCase("EN") && !file.exists()) { //Create default EN locale
			try {
				file.createNewFile();
				
				FileWriter myWriter = new FileWriter(file);
				myWriter.write(
						"Set.NoPerms=&5&lTwerking Crops → &7Whoops, you don''t have the permission to do this!\r\n" + 
						"Set.Error=&5&lTwerking Crops → &7Use /set <%Functions%> <True/False> OR <%Langs%>\r\n" + 
						"Set.Bool=&5&lTwerking Crops → &7Use /set %Func% <True/False>\r\n" + 
						"Set.Lang=&5&lTwerking Crops → &7Use /set %Func% <%Langs%>\r\n" + 
						"Set.Func=&5&lTwerking Crops → &7`%Func%` is not an valid Function use `%Functions%`\r\n" + 
						"Set.Succes=&5&lTwerking Crops → &7You succesfully set %Func% to %Result%\r\n" + 
						"Set.NotAble=&5&lTwerking Crops → &7You can''t set %Func% to %Result% because %Reason%\r\n" + 
						"\r\n" + 
						"TwerkingPerSecond.Shifting=* You are &nshifting&r at &5&n%ShiftingRate% Shifts Per Second *\r\n" + 
						"\r\n" + 
						"Runnables.NoPerms=&5&lTwerking Crops → &7Whoops, you don''t have the permission to do this!\r\n" + 
						"Runnables.Failed=&5&lTwerking Crops → &7Could not restart all Bukkit Runnables, Use /reload to hard restart\r\n" + 
						"Runnables.Succes=&5&lTwerking Crops → &7You succesfully restarted all Bukkit Runnables!"
						);
				myWriter.close();
			} catch (IOException e) {
			}
		}
		
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

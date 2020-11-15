package Spigot.TwerkingCrops.ActionBar;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import Spigot.TwerkingCrops.Core;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public class ActionBar_1_16_A implements ActionBar {
	
	@Override
	public void sendActionBar(Player player, String msg) {
		try {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        } catch (Exception e) {
        	if(!Core.getInstance().NotifSpigotOnly) {
        		Core.getInstance().NotifSpigotOnly = true;
        		e.printStackTrace();
        		Core.getInstance().getLogger().log(Level.SEVERE, "1.16+ only has actionbar support for spigot.");
        	}
        }
    }

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    public String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}

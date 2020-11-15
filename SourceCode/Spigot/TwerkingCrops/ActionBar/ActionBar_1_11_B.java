package Spigot.TwerkingCrops.ActionBar;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/*
 * Created by Yorick, Last modified on: 14-1-2019
 */
public class ActionBar_1_11_B implements ActionBar {
	
	@Override
	public void sendActionBar(Player player, String msg) {
        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
              
            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + msg + "\"}");
            Object packet = constructor.newInstance(icbc, (byte) 2);
            Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
              e.printStackTrace();
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

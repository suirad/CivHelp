package vg.civcraft.mc.civhelp.civmenu.database;

import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.datamanager.FlatFileManager;
import vg.civcraft.mc.civhelp.civmenu.datamanager.ISaveLoad;
import vg.civcraft.mc.civhelp.civmenu.datamanager.MysqlManager;


public class TOSManager {
	private CivHelpPlugin plugin;
	private FileConfiguration config;
	
	private static ISaveLoad manager;
	
	public TOSManager(CivHelpPlugin plugin) {
		this.plugin = plugin;
		config = plugin.getCivMenu().getConfig();
		handleWriteManagerment();
	}
	
	private void handleWriteManagerment() {
		switch (config.getInt("save.manager")) {
		case 0:
			manager = new FlatFileManager(plugin);
			break;
		case 1:
			manager = new MysqlManager(plugin);
			break;
		default:
			plugin.getLogger().log(Level.WARNING, "[CivMenu] No database manager specified.");
			//Bukkit.getPluginManager().disablePlugin(plugin);
		}
		manager.load();
	}
	
	/**
	 * Adds an accepted term to a players track record.
	 */
	public static boolean addPlayer(Player p, String term) {
		if (manager != null) {
			manager.addPlayer(p, term);
		} else {
			return false;
		}
		
		return true;
	}

	/**
	 * Checks if a player has a specific term.
	 */
	public static boolean isTermPlayer(Player p, String term) {
		return manager != null && manager.isAddedPlayer(p, term);
	}
	
	public static void save() {
		manager.save();
	}
}

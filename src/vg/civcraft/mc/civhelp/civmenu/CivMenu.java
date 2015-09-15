package vg.civcraft.mc.civhelp.civmenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.database.TOSManager;

public class CivMenu {
	
	private static TOSManager tosManager;
	private static CivHelpPlugin plugin;
	private static CivMenuAPI api;
	private FileConfiguration config;
	
	public void onEnable() {
		plugin = CivHelpPlugin.getInstance();
		config = loadconfig();
		api = new CivMenuAPI();
		tosManager = new TOSManager(plugin);
		plugin.getServer().getPluginManager().registerEvents(new TOSListener(plugin), plugin);
		
		CommandHandler commandHandler = new CommandHandler(plugin);
		for (String command : plugin.getDescription().getCommands().keySet()) {
			if (plugin.getCommand(command).getPermission().equals("CivHelp.CivMenu")){
				plugin.getCommand(command).setExecutor(commandHandler);
			}
		}
		
	}
	
	public FileConfiguration getConfig(){
		return config;
	}
	
	
    @SuppressWarnings("deprecation")
	private FileConfiguration loadconfig() {
    	String cfgname = "civmenu.yml";
		File dir = plugin.getDataFolder();
		FileConfiguration con;
		boolean created = false;
		if (!dir.exists()){dir.mkdir();}
		File file = new File(dir,cfgname);
		if (!file.exists()){
			try {
				file.createNewFile();
				created = true;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (created){
			InputStream internal = plugin.getResource(cfgname);
			con = YamlConfiguration.loadConfiguration(internal);
			try {
				con.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else{
			con = YamlConfiguration.loadConfiguration(file);
		}
		
		return con;
	}


	public void onDisable() { 
    	TOSManager.save();
    }

	public static CivMenuAPI getApi() {
		return api;
	}


    
}

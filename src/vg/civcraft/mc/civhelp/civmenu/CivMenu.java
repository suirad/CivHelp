package vg.civcraft.mc.civhelp.civmenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.database.TOSManager;

public class CivMenu {
	
	private static TOSManager tosManager;
	private static CivHelpPlugin plugin;
	private static CivMenuAPI api;
	private FileConfiguration config;
	
	public void onEnable() {
		plugin = CivHelpPlugin.getInstance();
		api = new CivMenuAPI();
		config = loadconfig();
		
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

    public void SendHelpMenu(Player player, JavaPlugin plugin){
    	Menu menu = new Menu();
    	
		menu.setTitle(new TextComponent(plugin.getName()));

		if(plugin.getDescription().getDescription()!=null){
			menu.setSubTitle(new TextComponent(plugin.getDescription().getDescription()));
		}
		
    	for (String commandName : plugin.getDescription().getCommands().keySet()) {
    		Command command = plugin.getCommand(commandName);
			if(command.getPermission()!=null && !player.hasPermission(command.getPermission())){
				continue;
			}
			TextComponent part = new TextComponent(command.getLabel());
			part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(command.getDescription()).create()));
			
			//This simply doesn't work. Nice one Spigot.
			part.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command.getLabel()));
			
			menu.addPart(part);
		}
    	player.spigot().sendMessage(menu.create());
    	
    }
    
	public TOSManager getTosManager() {
		return tosManager;
	}



    
}

package vg.civcraft.mc.civhelp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civhelp.civguide.CivGuide;
import vg.civcraft.mc.civhelp.civmenu.CivMenu;

public class CivHelpPlugin extends JavaPlugin{
	private static CivHelpPlugin instance;
	private CivGuide civGuide;
	private CivMenu civMenu;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player && (label.equalsIgnoreCase("guide") || label.equalsIgnoreCase("gui"))){
			civGuide.onCommand((Player)sender, args);
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		if (civGuide != null){
			civGuide.onDisable();
			civGuide = null;
		}
		if (civMenu != null){
			civMenu.onDisable();
			civMenu = null;
		}
		
	}

	@Override
	public void onEnable() {
		instance = this;
		instance.saveDefaultConfig();
		instance.reloadConfig();
		if (instance.getConfig().getBoolean("civguide_enabled", false)){
			civGuide = new CivGuide();
			civGuide.onEnable();
			instance.getLogger().info("Loaded CivGuide");
		}
		if (instance.getConfig().getBoolean("civmenu_enabled", false)){
			civMenu = new CivMenu();
			civMenu.onEnable();
			instance.getLogger().info("Loaded CivMenu");
		}
		
	}

	public static CivHelpPlugin getInstance() {
		return instance;
	}
	
	public boolean isCivGuideEnabled(){
		return (civGuide != null);
	}
	
	public CivGuide getCivGuide(){
		return civGuide;
	}
	
	public CivMenu getCivMenu(){
		return civMenu;
	}
	
	public boolean isCivMenuEnabled(){
		return (civMenu != null);
	}
}

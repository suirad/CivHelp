package vg.civcraft.mc.civhelp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civhelp.civguide.CivGuide;

public class CivHelpPlugin extends JavaPlugin{
	private static CivHelpPlugin instance;
	private static CivGuide civGuide;

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
}

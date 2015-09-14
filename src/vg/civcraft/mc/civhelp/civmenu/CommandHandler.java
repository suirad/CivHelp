package vg.civcraft.mc.civhelp.civmenu;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.database.TOSManager;

public class CommandHandler implements CommandExecutor{

	private CivHelpPlugin pluginInstance = null;
	
	public CommandHandler(CivHelpPlugin pluginInstance) {
		this.pluginInstance = pluginInstance;
	}	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String caption, String[] argv) {
		
		if (caption.length() <= 0) {
			return false;
		}
		
		if (caption.equals("help")) {
			return commandHelp(sender, argv);
		}
		
		if (caption.equals("sign")) {
			return commandSign(sender, argv);
		}
		
		return false;
	}

	private boolean commandHelp(CommandSender sender, String[] argv) {
		
		if (argv.length < 1) {
			return false;
		}
		
		if(!(sender instanceof Player)){
			return false;
		}
		
		for(Plugin plugin : Bukkit.getPluginManager().getPlugins()){
			if(plugin.getName().equalsIgnoreCase(argv[0])){
				pluginInstance.getCivMenu().SendHelpMenu(((Player)sender), (JavaPlugin)plugin);
				return true;
			}
		}
		
		((Player)sender).sendMessage("Plugin wasn't found");
		return true;
	}

	private boolean commandSign(CommandSender sender, String[] argv) {
		
		if(!(sender instanceof Player)){
			return false;
		}
		
		Player player = (Player)sender;
		
		if (!TOSManager.isTermPlayer(player, "CivMenu Agreement")){
			if(TOSManager.addPlayer(player, "CivMenu Agreement")){
				TOSManager.save();
				player.sendMessage("Thank you for signing the terms of service");
				return true;
			}
		} else {
			player.sendMessage("You have already signed the terms of service");
			return true;
		}
		
		return false;
	}

}

package vg.civcraft.mc.civhelp.civmenu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.database.TOSManager;

public class CommandHandler implements CommandExecutor{

	private CivHelpPlugin plugin = null;
	private CivMenuAPI api;
	
	public CommandHandler(CivHelpPlugin pluginInstance) {
		this.plugin = pluginInstance;
		api = CivMenuAPI.getInstance();
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
		
		if(!(sender instanceof Player)){
			sender.sendMessage("This command can only be executed by a player");
			return true;
		}
		
		if (argv.length < 1){
			api.sendHelpMenu(((Player)sender), null);
			return true;
		}
		
		for(Plugin plugin : plugin.getServer().getPluginManager().getPlugins()){
			if(plugin.getName().equalsIgnoreCase(argv[0])){
				api.sendHelpMenu(((Player)sender), (JavaPlugin)plugin);
				return true;
			}
		}
		
		api.sendHelpMenu((Player)sender, null);
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

package vg.civcraft.mc.civhelp.civmenu;



import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;







import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civhelp.CivHelpPlugin;

public class CivMenuAPI {

	private static CivMenuAPI api;
	private FileConfiguration config;
	
	public CivMenuAPI() {
		api = this;
		config = CivHelpPlugin.getInstance().getCivMenu().getConfig();
	}
	
	public static CivMenuAPI getInstance() {
		return api;
	}
		
	public void sendAliasMenu(Player p, String alias){
		if (p == null || alias == null){return;}else if (alias.isEmpty()){return;}
		if (!config.contains("aliases") || !config.contains("aliases."+alias)){return;}
		
		Menu menu = new Menu();
		String text = config.getString("aliases."+alias);
		TextComponent subtitle = new TextComponent(text);
		subtitle.setColor(ChatColor.GREEN);
		menu.setSubTitle(subtitle);
		
		String name = alias.substring(0, alias.lastIndexOf("."));
		if (config.contains("aliases."+name+".default")){
			TextComponent cmd = new TextComponent(config.getString("aliases."+name+".default"));
			cmd.setColor(ChatColor.YELLOW);
			cmd.setItalic(true);
			cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help "+name));
			cmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("/help "+name).create()));
			menu.addPart(cmd);
		}
		
		menu.sendPlayer(p);
		
	}
	
    public void sendHelpMenu(Player player, JavaPlugin plugin){
    	if (player == null){return;}
    	
    	Menu menu = new Menu();

		if (plugin == null) {
			TextComponent title = new TextComponent("Civcraft Help Menu");
			title.setColor(ChatColor.RED);
			menu.setTitle(title);
			menu.setSubTitle(new TextComponent(config.getString("helpmenu.message")));
			String[] plugins = config.getString("helpmenu.plugins").split(", ");
			for(String pluginName:plugins){
				TextComponent part = new TextComponent(pluginName);
				part.setColor(ChatColor.YELLOW);
				part.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + pluginName));
				menu.addPart(part);
			}
		} else {
			menu.setTitle(new TextComponent(plugin.getName()));
			
			if (plugin.getDescription().getDescription() != null) {
				menu.setSubTitle(new TextComponent(plugin.getDescription()
						.getDescription()));
			}
			
			for (String commandName : plugin.getDescription().getCommands()
					.keySet()) {
				Command command = plugin.getCommand(commandName);
				if (command.getPermission() != null
						&& !player.hasPermission(command.getPermission())) {
					continue;
				}
				TextComponent part = new TextComponent(command.getLabel());
				part.setColor(ChatColor.YELLOW);
				part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(command.getUsage() + " \n"
								+ command.getDescription()).create()));
				part.setClickEvent(new ClickEvent(
						ClickEvent.Action.SUGGEST_COMMAND, "/" + command.getLabel()));
	
				menu.addPart(part);
			}
		}

		menu.sendPlayer(player);
    	
    }

}

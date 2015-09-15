package vg.civcraft.mc.civhelp.civmenu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.database.TOSManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

public class TOSListener implements Listener {

	private CivHelpPlugin plugin;
	private Map<UUID, Location> locations;
	private FileConfiguration config;

	public TOSListener(CivHelpPlugin plugin) {
		this.plugin = plugin;
		config = plugin.getCivMenu().getConfig();
		locations = new ConcurrentHashMap<UUID, Location>();
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (!TOSManager.isTermPlayer(p, "CivMenu Agreement")) {
			sendTOS(p);
			locations.put(p.getUniqueId(), p.getLocation());
			new BukkitRunnable() {
				
				@Override
				public void run() {
					locations.remove(p.getUniqueId());
					if(!TOSManager.isTermPlayer(p, "CivMenu Agreement")){
						p.kickPlayer(config.getString("terms.kickmessage",
								"You must accept the terms in order to play"));
					}
					
				}
			}.runTaskLater(this.plugin, config.getInt("terms.kickdelay", 6000));
		}
	}
	
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!TOSManager.isTermPlayer(p, "CivMenu Agreement")) {
			if (!locations.containsKey(p.getUniqueId())){return;}
			if(event.getTo().distance(locations.get(p.getUniqueId())) > config.getInt("terms.movementrange",15)){
				p.sendMessage(ChatColor.RED + "You must accept the terms in order to play.");
				sendTOS(p);
				event.setTo(locations.get(p.getUniqueId()));
			}
		}
	}

	public void sendTOS(Player p) {

		Menu menu = new Menu();

		TextComponent welcome = new TextComponent(config.getString("terms.title"));
		welcome.setColor(ChatColor.YELLOW);
		menu.setTitle(welcome);

		TextComponent message = new TextComponent(config.getString("terms.message"));
		message.setColor(ChatColor.AQUA);
		menu.setSubTitle(message);

		TextComponent link = new TextComponent(config.getString("terms.linkmessage"));
		link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("terms.link")));
		link.setItalic(true);
		menu.addPart(link);
		
		TextComponent confirm = new TextComponent(config.getString("terms.confirm"));
		confirm.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/sign"));
		confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/sign").create()));
		confirm.setItalic(true);
		menu.addPart(confirm);
		
		menu.sendPlayer(p);
	}
}
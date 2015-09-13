package vg.civcraft.mc.civhelp.civmenu.datamanager;

import org.bukkit.entity.Player;

public interface ISaveLoad {

	public void load();
	public void save();
	public void addPlayer(Player p, String term);
	public boolean isAddedPlayer(Player p, String term);
}

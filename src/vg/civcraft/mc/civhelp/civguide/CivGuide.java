package vg.civcraft.mc.civhelp.civguide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civguide.books.CivGuideBook;
import vg.civcraft.mc.civhelp.civguide.books.CivGuideReader;
import vg.civcraft.mc.civhelp.civguide.listener.CivGuideCommandListener;
import vg.civcraft.mc.civhelp.civguide.listener.CivGuidePlayerListener;

public class CivGuide {
	private static CivGuide instance;
	private CivGuidePlayerListener playerListener;
	private CivGuideCommandListener commandListener;
	private HashMap<String, CivGuideBook> guideBooks;
	private HashMap<UUID, CivGuideReader> playersWithBooks;
	public String bookauthor = ChatColor.DARK_PURPLE+"CivCraft";
	private static CivHelpPlugin help;
	
	public CivGuide(){
		help = CivHelpPlugin.getInstance();
	}
	

	public void onCommand(Player sender, String[] args) {
		commandListener.handleCommand(sender, args);
	}

	public void onDisable() {
		for (Player player : help.getServer().getOnlinePlayers())
			player.getInventory().setContents(removeBooks(player.getInventory().getContents()));
		
		guideBooks.clear();
		playersWithBooks.clear();
		
	}

	public void onEnable() {
		instance = this;
		playerListener = new CivGuidePlayerListener();
		commandListener = new CivGuideCommandListener();
		playersWithBooks = new HashMap<UUID, CivGuideReader>();
		guideBooks = new HashMap<String, CivGuideBook>();
		loadBooks();
		help.getServer().getPluginManager().registerEvents(playerListener, help);
		
		for (Player player : help.getServer().getOnlinePlayers())
			player.getInventory().setContents(removeBooks(player.getInventory().getContents()));
		
	}
	
	@SuppressWarnings("deprecation")
	private void loadBooks() {
		File dir = help.getDataFolder();
		FileConfiguration config;
		boolean created = false;
		if (!dir.exists()){dir.mkdir();}
		File file = new File(dir,"civguide.yml");
		if (!file.exists()){
			try {
				file.createNewFile();
				created = true;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
		if (created){
			InputStream internal = help.getResource("civguide.yml");
			config = YamlConfiguration.loadConfiguration(internal);
			try {
				config.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else{
			config = YamlConfiguration.loadConfiguration(file);
		}
		
		
		if (!config.contains("booklist")){return;}
		
		List<String> booklist = config.getStringList("booklist");
		if (booklist == null){return;}
		
		String defaultbook = config.getString("defaultbook", "");
		
		String fullname;
		List<String> pages;
		CivGuideBook book;
		
		for (String bookname : booklist){
			if (!config.contains("books."+bookname) || bookname.isEmpty()){continue;}
			
			fullname = config.getString("books."+bookname+".fullname", bookname);
			pages = config.getStringList("books."+bookname+".pages");
			book = new CivGuideBook(fullname); 
			book.addPages(pages);
			guideBooks.put(bookname.toLowerCase(), book);
			if (bookname.equalsIgnoreCase(defaultbook)){
				guideBooks.put("default", book);
			}
		}
	}

	public static CivGuide getInstance(){
		return instance;
	}
	
	public HashMap<String, CivGuideBook> getGuideBooks(){
		return guideBooks;
	}
	
	public boolean isGuideBook(String bookname){
		if (guideBooks.containsKey(bookname)){return true;}
		for (CivGuideBook book: guideBooks.values()){
			if (book.getName().equalsIgnoreCase(bookname))
				return true;
		}
		return false;
	}
	
	public boolean sendGuideBook(Player player, String bookname){
		if (!isGuideBook(bookname) || player == null){
			return false;
		}
		
		String booknamefixed = fixbookname(bookname);
		
		ItemStack inhand = player.getItemInHand();
		if (!(inhand.getType().equals(Material.AIR))){
			if (!(inhand.getType().equals(Material.WRITTEN_BOOK))){
				player.sendMessage(ChatColor.DARK_GREEN+"[CivGuide] "+ChatColor.DARK_RED+"You're hand needs to be empty before you can summon a guidebook.");
				return false;
			}
			BookMeta handbook = (BookMeta)inhand.getItemMeta();
			if (!(handbook.getAuthor().equals(bookauthor))){
				player.sendMessage(ChatColor.DARK_GREEN+"[CivGuide] "+ChatColor.DARK_RED+"You're hand needs to be empty before you can summon a guidebook.");
				return false;
			} else{
				player.setItemInHand(new ItemStack(Material.AIR));
			}
		}
		
		player.setItemInHand(guideBooks.get(booknamefixed).makeBookItem());
		playersWithBooks.put(player.getUniqueId(), new CivGuideReader());
		return true;
	}
	
	private String fixbookname(String bookname) {
		if (guideBooks.containsKey(bookname)){return bookname;}
		String name = bookname;
		for (String book: guideBooks.keySet()){
			if (guideBooks.get(book).getName().equalsIgnoreCase(bookname)){
				name = book;
				break;
			}
		}
		return name;
	}
	
	public boolean hasBook(Player player){
		if (player == null){
			return false;
		} else{
			return hasBook(player.getUniqueId());
		}
	}

	public boolean hasBook(UUID uuid){
		if (uuid == null){return false;}
		if (playersWithBooks.containsKey(uuid)){
			return true;
		}
		return false;
	}
	
	public boolean isReading(Player player){
		if (player == null){
			return false;
		} else {
			return isReading(player.getUniqueId());
		}
	}
	
	public boolean isReading(UUID uuid) {
		if (!hasBook(uuid)){
			return false;
		}
		return playersWithBooks.get(uuid).isReading();
	}
	
	public long getReadTimeSecs(Player player){
		if (player == null){
			return 0;
		} else{
			return getReadTimeSecs(player.getUniqueId());
		}
	}
	
	public long getReadTimeSecs(UUID uuid){
		long ret = 0;
		if (isReading(uuid)){
			ret = playersWithBooks.get(uuid).getReadTimeSecs();
		}
		return ret;
	}
	
	public long getOwnTimeSecs(Player player){
		if (player == null){
			return 0;
		} else{
			return getOwnTimeSecs(player.getUniqueId());
		}
	}
	
	public long getOwnTimeSecs(UUID uuid){
		long ret = 0;
		if (hasBook(uuid)){
			ret = playersWithBooks.get(uuid).getOwnTime();
		}
		return ret;
	}

	
	public void setReading(Player player, boolean reading){
		if (player == null)
			return;
		setReading(player.getUniqueId(), reading);
	}

	public void setReading(UUID uuid, boolean reading) {
		if (!hasBook(uuid))
			return;
		playersWithBooks.get(uuid).setReading(reading);
	}

	public void removeBook(Player player){
		removeBook(player, -1);
	}
	
	public void removeBook(Player player, int slot){
		if (player == null){
			return;
		} else if (!hasBook(player))
			return;
		playersWithBooks.remove(player.getUniqueId());
		ItemStack inhand;
		if (slot == -1){
			inhand = player.getItemInHand();
		} else{
			inhand = player.getInventory().getItem(slot);
		}
		if (inhand == null){return;}
		if (!(inhand.getType().equals(Material.WRITTEN_BOOK))){
			return;
		}
		BookMeta bookdat = (BookMeta)inhand.getItemMeta();
		if (bookdat.getAuthor().equals(bookauthor)){
			player.setItemInHand(new ItemStack(Material.AIR));
		}

	}
	
	public List<ItemStack> removeBooks(List<ItemStack> items){
		for (ItemStack item : items){
			if (item.getType().equals(Material.WRITTEN_BOOK)){
				if (((BookMeta)item.getItemMeta()).getAuthor().equals(bookauthor)){
					item.setType(Material.AIR);
				}
			}
		}
		return items;
	}
	
	public ItemStack[] removeBooks(ItemStack[] items) {
		for (ItemStack item : items){
			if (item == null){continue;}
			if (item.getType().equals(Material.WRITTEN_BOOK)){
				if (((BookMeta)item.getItemMeta()).getAuthor().equals(bookauthor)){
					item.setType(Material.AIR);
				}
			}
		}
		return items;
	}



}

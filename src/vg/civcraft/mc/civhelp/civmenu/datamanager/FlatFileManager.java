package vg.civcraft.mc.civhelp.civmenu.datamanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civhelp.CivHelpPlugin;
import vg.civcraft.mc.civhelp.civmenu.TermObject;

public class FlatFileManager implements ISaveLoad{

	private CivHelpPlugin plugin;
	private File logFile;
	
	private Map<UUID, TermObject> registeredPlayers = new HashMap<UUID, TermObject>();
	
	public FlatFileManager(CivHelpPlugin plugin) {
		this.plugin = plugin;
		logFile = new File(plugin.getDataFolder().getAbsolutePath(), "registeredPlayers.txt");
		if (!logFile.exists())
			try {
				plugin.getLogger().log(Level.WARNING, "[CivMenu] File not found, so no data about people who accepted the TOS before was created");
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Override
	public void load() {
		parseLogFile();
	}

	@Override
	public void save() {
		StringBuilder builder = new StringBuilder();
		for (TermObject term: registeredPlayers.values())
			builder.append(term.toString() + "\n");
		writeToFile(builder.toString(), logFile);
	}
	
	private static void writeToFile(String content, File file) {
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(content+"\n");

		}
		catch ( IOException e)
		{
			//TODO log something
		}
		finally
		{
		    try
		    {
		        if ( writer != null) {
		        	writer.flush();
		        	writer.close( );
		        }
		    }
		    catch ( IOException e)
		    {
		    	//TODO log something
		    }
		}
	}
	
	private void parseLine(String line) {
		String [] values = line.split(":");
		if (values.length < 2) {
			plugin.getLogger().log(Level.WARNING, "[CivMenu] Found invalid line while loading registered players: "+line); //TODO
			return;
		}
		UUID playerUUID = UUID.fromString(values[0]);
		registeredPlayers.put(playerUUID, new TermObject(playerUUID));
		for (int x = 1; x < values.length; x++)
			registeredPlayers.get(playerUUID).addTerm(values[x]);
	}
	
	private void parseLogFile() {
        String line = null;
        try {
            FileReader fileReader = 
                new FileReader(logFile);
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                parseLine(line);
            }    
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            //TODO fix this so it works with main plugin
        	plugin.getLogger().log(Level.WARNING, "[CivMenu] File not found, so no data about people who accepted the TOS before was created");
        }
        catch(IOException ex) {
        	plugin.getLogger().log(Level.WARNING, "[CivMenu] Error reading file '" + logFile.getName() + "'");
            ex.printStackTrace();
        }
	}

	@Override
	public void addPlayer(Player p, String term) {
		if (!registeredPlayers.containsKey(p.getUniqueId()))
			registeredPlayers.put(p.getUniqueId(), new TermObject(p.getUniqueId()));
		registeredPlayers.get(p.getUniqueId()).addTerm(term);
	}

	@Override
	public boolean isAddedPlayer(Player p, String term) {
		return registeredPlayers.get(p.getUniqueId()) != null && registeredPlayers.get(p.getUniqueId()).hasTerm(term);
	}

}

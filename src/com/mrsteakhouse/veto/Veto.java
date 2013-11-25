package com.mrsteakhouse.veto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Veto extends JavaPlugin{
	private List<Umfrage> UmfrageList = new ArrayList<Umfrage>();
	private String root;
	private boolean autoEnd;
	private String uPath;
	private String autoEndDate;
	
	public Veto() {
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		try {
			loadConfig(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadUmfragen();
		Bukkit.getLogger().log(Level.INFO,"[Veto] " + UmfrageList.size() + " Umfragen geladen!");
		
		getCommand("veto").setExecutor(new CommandHandler(this));
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Scheduler(this, UmfrageList), 120L, 18000L);
	}
	
	@Override
	public void onDisable() {
		saveUmfragen();
		saveConfig();
		
	}
	
	public void loadConfig(Plugin plugin) throws Exception
	{
		FileConfiguration config = plugin.getConfig();
		File file = new File(root + "/config.yml");
		
		if (!file.exists()) {
		      config.options().copyDefaults(true);
		      saveConfig();
		}
		ConfigurationSection cs = config.getConfigurationSection("veto");
		root = cs.getString("root");
		autoEnd = cs.getBoolean("auto-end");
		uPath = cs.getString("pollfolder");
		autoEndDate = cs.getString("auto-end-date");
		
	}
	
	
	public void saveUmfragen() {
		for(Umfrage u : UmfrageList) {
			File file = new File(uPath, u.getName() + ".yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if(!file.exists()) {
				config.createSection(u.getName());
			}
			ConfigurationSection cs = config.getConfigurationSection(u.getName());
			cs.set("topic", u.getThema());
			cs.set("votes", u.getVotes());
			cs.set("started", u.getStarted());
			cs.set("estEnde", u.getestEnde());
			cs.set("auto-end", u.getAutoEnd());
			cs.set("mul-choice", u.getMulChoice());
			cs.set("perm", u.getPerm());
			cs.set("player", u.getPlayerList());
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean loadUmfragen() {
		File folder = new File(uPath);
		
		if(!folder.exists()) {
			folder.mkdir();
			Bukkit.getLogger().log(Level.INFO, "Keine Umfragen in diesem Ordner vorhanden!");
			return false;
		}
		
		File[] fileList = folder.listFiles();
		for(File file : fileList) {
			if(file.isDirectory()) { continue; }
			String name = file.getName().substring(0, file.getName().length()-4);

			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			ConfigurationSection cs = config.getConfigurationSection(name);
			
			boolean started = cs.getBoolean("started");
			String estEnde = cs.getString("estEnde");
			boolean uautoEnd = cs.getBoolean("auto-end");
			String perm = cs.getString("perm");
			List<String> topic = cs.getStringList("topic");
			List<String> playerList = cs.getStringList("player");
			boolean mulChoice = cs.getBoolean("mul-choice");
			Map<String, Object> votes = cs.getConfigurationSection("votes").getValues(false);

			Umfrage temp = new Umfrage(this, name, topic, votes, started, estEnde, uautoEnd, playerList, perm, mulChoice);
			UmfrageList.add(temp);
		}
		return true;
	}
	
	public void createUmfrage(String name, String perm) {
		Umfrage u = new Umfrage(this, name, perm);
		UmfrageList.add(u);
		reload();
	}
	
	public void deleteUmfrage(String str) {
		Umfrage u = getUmfrage(str);
		if(u != null) {
			File file = new File(uPath, u.getName() + ".yml");
			if(file.exists()) {
				file.delete();
				UmfrageList.remove(u);
				reload();
			}
		}
	}
	
	public List<Umfrage> getUmfragenListe() {
		return UmfrageList;
	}
	
	public void reload() {
		try {
			loadConfig(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveUmfragen();
		UmfrageList.clear();
		loadUmfragen();
	}
	
	public boolean getAutoEnde() {
		return autoEnd;
	}
	
	public String getAutoEndDate() {
		return autoEndDate;
	}
	
	public Umfrage getUmfrage(String name) {
		for(Umfrage u : UmfrageList) {
			if(u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}
}
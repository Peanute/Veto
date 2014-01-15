package com.mrsteakhouse.veto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Umfrage {
	
	private String name;
	private List<String> thema;
	private Map<String, Object> votes;
	private Veto plugin;
	private boolean started;
	private String estEnde;
	private boolean autoEnd;
	private String perm;
	private Integer multipleChoice;
	private List<String> playerList;
	private NumberFormat nf = NumberFormat.getInstance();
	
	
	public Umfrage(Veto plugin, String name, List<String> thema, Map<String, Object> votes, boolean started, String estEnde, boolean autoEnd, List<String> playerList, String perm, Integer multipleChoice) {
		this.name = name;
		this.thema = thema;
		this.votes = votes;
		this.plugin = plugin;
		this.started = started;
		this.estEnde = estEnde;
		this.autoEnd = autoEnd;
		this.playerList = playerList;
		this.multipleChoice = multipleChoice;
		this.perm = perm;
		nf.setMaximumFractionDigits(2);
	}
	
	public Umfrage(Veto plugin, String name, String perm) {
		this.plugin = plugin;
		this.name = name;
		this.thema = new ArrayList<String>();
		this.votes = new HashMap<String, Object>();
		this.started = false;
		this.estEnde = "01/01/1990 00:00";
		this.autoEnd = false;
		this.playerList = new ArrayList<String>();
		this.multipleChoice = 1;
		this.perm = perm;
		nf.setMaximumFractionDigits(2);
		
	}
	
	public boolean addVote(Integer[] answer, String name) {
		if(playerList.contains(name)) { return false; }
		Object[] sArray = votes.keySet().toArray();
		int anzahl = 0;
		for(Integer i : answer) {
			if(anzahl >= multipleChoice) {
				break;
			}
			String s = (String)sArray[i-1];
			Integer count = (Integer)votes.get(s)+1;
			votes.put(s, count);
			anzahl++;
		}
		playerList.add(name);
		return true;
	}
	
	public Map<String, Object> getVotes() {
		return votes;
	}
	
	public void printUmfrage(CommandSender sender) {
		sender.sendMessage(ChatColor.BOLD + String.valueOf(plugin.getLanguageData().get("u-print-top")) + this.name);
		for(String s : thema) {
			sender.sendMessage(ChatColor.GREEN + s);
		}
		sender.sendMessage(ChatColor.GOLD + String.valueOf(plugin.getLanguageData().get("u-print-answers")));
		int i = 1;
		for(String s : votes.keySet()) {
			sender.sendMessage(ChatColor.AQUA.toString() + i + ". : " + s);
			i++;
		}
		sender.sendMessage(ChatColor.YELLOW + String.valueOf(plugin.getLanguageData().get("u-print-startet")) + (started ? String.valueOf(plugin.getLanguageData().get("u-print-no")) : String.valueOf(plugin.getLanguageData().get("u-print-yes"))));
		sender.sendMessage(ChatColor.YELLOW + String.valueOf(plugin.getLanguageData().get("u-print-enddate")) + estEnde);
		sender.sendMessage(ChatColor.YELLOW + String.valueOf(plugin.getLanguageData().get("u-print-ao")) + (autoEnd ? String.valueOf(plugin.getLanguageData().get("u-print-no")) : String.valueOf(plugin.getLanguageData().get("u-print-yes"))));
		sender.sendMessage(ChatColor.YELLOW + String.valueOf(plugin.getLanguageData().get("u-print-mp")) + String.valueOf(this.multipleChoice));
		sender.sendMessage(ChatColor.YELLOW + "Permissions: " + perm);
		if(sender instanceof Player) { sender.sendMessage(ChatColor.RED + String.valueOf(plugin.getLanguageData().get("u-voted-top")) + (playerList.contains(sender.getName()) ? String.valueOf(plugin.getLanguageData().get("u-voted-yes")) : String.valueOf(plugin.getLanguageData().get("u-voted-no"))) + String.valueOf(plugin.getLanguageData().get("u-voted-bot"))); }
	}
	
	public void shortPrint(CommandSender sender) {
		if(!this.thema.isEmpty()) {
			sender.sendMessage(ChatColor.GOLD + "(" + (started?ChatColor.GREEN:ChatColor.DARK_RED) + name + ChatColor.GOLD + "): " + thema.get(0));
		}
	}
	
	public void printStat(CommandSender sender) {
		sender.sendMessage(ChatColor.BOLD + String.valueOf(plugin.getLanguageData().get("u-stat-top")) + name);
		int i = 1;
		for(String s : votes.keySet()) {
			sender.sendMessage(ChatColor.AQUA.toString() + i + ". : " + s + ": " + votes.get(s) + " (" + nf.format(Double.valueOf((Integer)votes.get(s)) / Double.valueOf(countVotes()) * 100.0D) + "%)");
			i++;
		}
		sender.sendMessage(ChatColor.YELLOW + String.valueOf(plugin.getLanguageData().get("u-stat-bot1")) + countVotes() + String.valueOf(plugin.getLanguageData().get("u-stat-bot2")));
	}
	
	public void printPlayerlist(CommandSender sender) {
		if(!playerList.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + String.valueOf(plugin.getLanguageData().get("u-pList-cont")));
			String msg="";
			int i = 1;
			for(String ply : playerList) {
				msg = msg.concat(ply + (i<playerList.size()? ", " : "."));
				i++;
			}
			sender.sendMessage(ChatColor.GOLD + msg);
		} else {
			sender.sendMessage(ChatColor.DARK_RED + String.valueOf(plugin.getLanguageData().get("u-pList-not")));
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getThema() {
		return thema;
	}
	
	public boolean getStarted() {
		return started;
	}
	
	public String getestEnde() {
		return estEnde;
	}
	
	public boolean getAutoEnd() {
		return autoEnd;
	}
	
	public void setAutoEnd(Boolean ende) {
		this.autoEnd = ende;
	}
	
	public void start() {
		started = true;
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + String.valueOf(plugin.getLanguageData().get("u-startend")) + ChatColor.DARK_RED + name + ChatColor.GREEN + String.valueOf(plugin.getLanguageData().get("u-start")));
	}
	
	public void end() {
		started = false;
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + String.valueOf(plugin.getLanguageData().get("u-startend")) + ChatColor.DARK_RED + name + ChatColor.GREEN + String.valueOf(plugin.getLanguageData().get("u-end")));
	}
	
	public Integer countVotes() {
		Integer temp=0;
		for(String s : votes.keySet()) {
			temp += (int)votes.get(s);
		}
		return temp;
	}

	public List<String> getPlayerList() {
		return playerList;
	}
	
	public void setEnde(String ende) {
		this.estEnde = ende;
	}
	
	public String getPerm() {
		return perm;
	}

	public void setPerm(String perm) {
		this.perm = perm;
	}
	
	public void setMulChoice(Integer mChoice) {
		this.multipleChoice = mChoice;
	}
	
	public Integer getMulChoice() {
		return multipleChoice;
	}
	
	public boolean addPlayer(String player) {
		if(playerList.contains(player)) { return false; }
		playerList.add(player);
		plugin.reloadUmfragen();
		return true;
	}
	
	public void editVoteAdd(String vote, Integer count) {
		votes.put(vote, count);
		plugin.reloadUmfragen();
	}
	
	public boolean editVoteRemove(Integer key) {
		int i = 1;
		for(String s : votes.keySet()) {
			if(i==key) {
				votes.remove(s);
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean editPlayerRemove(String name, String player) {
		if(!playerList.contains(player)) { return false; }
		playerList.remove(player);
		plugin.reloadUmfragen();
		return true;
	}
	
	public void editTopic(String topic, Integer line) {
		line-=1;
		if(topic.equals("")) {
			if(thema.size()>line) {
				thema.set(line, "");
				thema.remove((int)line);
			} else {
				line = thema.size()-1;
				thema.set(line, "");
				thema.remove((int)line);
			}
			plugin.reloadUmfragen();
			return;
		}
		if(!(thema.size()<=line)) {
			thema.set(line, topic);
		} else {
			thema.add(topic);
		}
		plugin.reloadUmfragen();
	}
	
	public boolean editVoteCount(Integer key, Integer count) {
		int i = 1;
		for(String s : votes.keySet()) {
			if(i==key) {
				votes.put(s, count);
				return true;
			}
			i++;
		}
		return false;
	}
}

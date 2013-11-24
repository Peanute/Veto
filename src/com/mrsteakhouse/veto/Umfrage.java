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
import org.bukkit.help.HelpTopicComparator.TopicNameComparator;

public class Umfrage {
	
	private String name;
	private List<String> thema;
	private Map<String, Object> votes;
	private Veto plugin;
	private boolean started;
	private String estEnde;
	private boolean autoEnd;
	private String perm;
	private boolean multipleChoice;
	private List<String> playerList;
	private NumberFormat nf = NumberFormat.getInstance();
	
	
	public Umfrage(Veto plugin, String name, List<String> thema, Map<String, Object> votes, boolean started, String estEnde, boolean autoEnd, List<String> playerList, String perm, boolean multipleChoice) {
		this.name = name;
		this.thema = thema;
		this.votes = votes;
		this.plugin = plugin;
		this.started = started;
		this.estEnde = estEnde;
		this.autoEnd = autoEnd;
		this.playerList = playerList;
		this.multipleChoice = multipleChoice;
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
		this.multipleChoice = false;
		nf.setMaximumFractionDigits(2);
		
	}
	
	public boolean addVote(Integer answer, String name) {
		if(playerList.contains(name)) { return false; } 
		Object[] sArray = votes.keySet().toArray();
		String s = (String)sArray[answer-1];
		Integer count = (Integer)votes.get(s)+1;
		votes.put(s, count);
		playerList.add(name);
		return true;
	}
	
	public boolean addMulVote(Integer[] answers, String name) {
		if(playerList.contains(name)) { return false;}
		
		Object[] sArray = votes.keySet().toArray();
		for(Integer answer : answers) {
			String s = (String)sArray[answer-1];
			Integer count = (Integer)votes.get(s)+1;
			votes.put(s, count);
		}
		playerList.add(name);
		return true;
	}
	
	public Map<String, Object> getVotes() {
		return votes;
	}
	
	public void printUmfrage(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "Umfrage: " + this.name);
		for(String s : thema) {
			sender.sendMessage(ChatColor.GREEN + s);
		}
		sender.sendMessage(ChatColor.GOLD + "Mögliche Antworten: ");
		int i = 1;
		for(String s : votes.keySet()) {
			sender.sendMessage(ChatColor.AQUA.toString() + i + ". : " + s);
			i++;
		}
		sender.sendMessage(ChatColor.YELLOW + "Gestartet: " + (started ? "Ja" : "Nein"));
		sender.sendMessage(ChatColor.YELLOW + "Endet am: " + estEnde);
		sender.sendMessage(ChatColor.YELLOW + "Autom. Ende: " + (autoEnd ? "Ja" : "Nein"));
		sender.sendMessage(ChatColor.YELLOW + "Multiple Choice: " + (multipleChoice ? "Ja" : "Nein"));
		sender.sendMessage(ChatColor.YELLOW + "Permissions: " + perm);
		if(sender instanceof Player) { sender.sendMessage(ChatColor.RED + "Du hast " + (playerList.contains(sender.getName()) ? "bereits" : "noch nicht") + " abgestimmt."); }
	}
	
	public void shortPrint(CommandSender sender) {
		sender.sendMessage((started?ChatColor.GREEN:ChatColor.RED) + "(" + name + "): "+ ChatColor.GOLD + thema.toArray()[0]);
	}
	
	public void printStat(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "Statistiken für " + name);
		int i = 1;
		for(String s : votes.keySet()) {
			sender.sendMessage(ChatColor.AQUA.toString() + i + ". : " + s + ": " + votes.get(s) + " (" + nf.format(Double.valueOf((Integer)votes.get(s)) / Double.valueOf(countVotes()) * 100.0D) + "%)");
			i++;
		}
		sender.sendMessage(ChatColor.YELLOW + "Es wurde insgesamt " + countVotes() + " mal abgestimmt.");
	}
	
	public void printPlayerlist(CommandSender sender) {
		if(!playerList.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + "Es haben folgende Spieler abegestimmt:");
			String msg="";
			int i = 1;
			for(String ply : playerList) {
				msg = msg.concat(ply + (i<playerList.size()? ", " : "."));
				i++;
			}
			sender.sendMessage(ChatColor.GOLD + msg);
		} else {
			sender.sendMessage(ChatColor.RED + "Es hat noch niemand abgestimmt.");
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
	
	public void start() {
		started = true;
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Die Umfrage " + ChatColor.RED + name + ChatColor.GREEN + " wurde gestartet.");
	}
	
	public void end() {
		started = false;
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Die Umfrage " + ChatColor.RED + name + ChatColor.GREEN + " wurde beendet.");
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
	
	public void setMulChoice(boolean mChoice) {
		this.multipleChoice = mChoice;
	}
	
	public boolean getMulChoice() {
		return multipleChoice;
	}
	
}

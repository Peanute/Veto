package com.mrsteakhouse.veto;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Scheduler implements Runnable {

	Veto plugin;
	List<Umfrage> umfrageList;
	Date date = new Date();
	
	public Scheduler(Veto plugin, List<Umfrage> umfrageList) {
		this.plugin = plugin;
		this.umfrageList = umfrageList;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		date = new Date();
		if(plugin.getAutoEnde()) {
			if(date.after(new Date(plugin.getAutoEndDate()))) {
				for(Umfrage u : umfrageList) {
					u.end();
				}
				Bukkit.broadcastMessage(ChatColor.DARK_RED +  String.valueOf(plugin.getLanguageData().get("sch-allClosed")));
			}
		}
		for(Umfrage u : umfrageList) {
			if(u.getAutoEnd()) {
				if(date.after(new Date(u.getestEnde())) && u.getStarted()) {
					u.end();
				}
			}
		}
	}

}

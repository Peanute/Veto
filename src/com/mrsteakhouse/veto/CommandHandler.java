package com.mrsteakhouse.veto;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor{
	private Veto plugin;
	
	
	public CommandHandler(Veto plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		
		if(!cmd.getName().equalsIgnoreCase("veto")) {
			return false;
		}
		if(args.length == 0) {
			showHelp(sender);
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
			if(!sender.hasPermission("Veto.help")) {
				noPerms(sender, "Veto.help");
				return false;
			}
			showHelp(sender);
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
			if(!sender.hasPermission("Veto.info")) {
				noPerms(sender, "Veto.info");
				return false;
			}
			
			sender.sendMessage(ChatColor.BLUE + "Folgende Umfragen sind verfügbar:");
			String msg = new String();
			int i=1;
			for(Umfrage u : plugin.getUmfragenListe()) {
				if(sender.hasPermission("Veto.survey."  +u.getPerm())) {
					msg = msg.concat((u.getStarted()?ChatColor.GREEN:ChatColor.RED) + u.getName() + (i<plugin.getUmfragenListe().size()? ", " : "."));
					i++;
				}
			}
			sender.sendMessage(msg);
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("info")) {
			if(!sender.hasPermission("Veto.info")) {
				noPerms(sender, "Veto.info");
				return false;
			}
			Umfrage u =plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!sender.hasPermission("Veto.survey." + u.getPerm())) { 
					noPerms(sender, "Veto.survey." + u.getPerm()); 
					return false; 
				}
				u.printUmfrage(sender);
				return true;
			}
			notFound(sender, args[1]);
			return true;
		}
		
		if(args.length == 3 && args[0].equalsIgnoreCase("info") && args[1].equalsIgnoreCase("playerlist")) {
			if(!sender.hasPermission("Veto.info.pList")) {
				noPerms(sender, "Veto.info.pList");
				return false;
			}
			Umfrage u = plugin.getUmfrage(args[2]);
			if(u != null) {
				if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
				u.printPlayerlist(sender);
				return true;
			}
			notFound(sender, args[2]);
			return false;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
			if(!sender.hasPermission("Veto.list")) {
				noPerms(sender, "Veto.list");
				return false;
			}
			sender.sendMessage(ChatColor.BLUE + "Die folgenden Umfragen sind verfügbar:");
			for(Umfrage u : plugin.getUmfragenListe()) {
				if(sender.hasPermission("Veto.survey."  + u.getPerm())) {
					u.shortPrint(sender);
				}
			}
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if(!sender.hasPermission("Veto.reload")) {
				noPerms(sender, "Veto.reload");
				return false;
			}
			sender.sendMessage(ChatColor.RED + "Reloading config and polls...");
			plugin.reload();
			sender.sendMessage(ChatColor.RED + "Done!");
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("start")) {
			if(!sender.hasPermission("Veto.start")) {
				noPerms(sender, "Veto.start");
				return false;
			}
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!sender.hasPermission("Veto.sruvey." + u.getPerm())) {
					noPerms(sender, "Veto.sruvey." + u.getPerm());
					return false;
				}
				u.start();
				return true;
			}
			notFound(sender, args[1]);
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("end")) {
			if(!sender.hasPermission("Veto.end")) {
				noPerms(sender, "Veto.end");
				return false;
			}
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!sender.hasPermission("Veto.sruvey." + u.getPerm())) {
					noPerms(sender, "Veto.sruvey." + u.getPerm());
					return false;
				}
				u.end();
				return true;
			}
			notFound(sender, args[1]);
			return true;
		}
		
		if(args.length==2 && args[0].equalsIgnoreCase("stat")) {
			if(!sender.hasPermission("Veto.stat")) {
				noPerms(sender, "Veto.stat");
				return false;
			}
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!sender.hasPermission("Veto.survey." + u.getPerm())) {
					noPerms(sender, "Veto.survey." + u.getPerm());
					return false;
				}
				if(!(sender instanceof Player)) {
					u.printStat(sender); 
					return true;
				}
				if(!u.getPlayerList().contains(sender.getName())) {
					notVoted(sender);
					return false;
				}
				u.printStat(sender);
				return true;
			}
			notFound(sender, args[1]);
			return true;
		}
		
		if(args.length==1 && args[0].equalsIgnoreCase("stat")) {
			if(sender.hasPermission("Veto.stat")) {
				noPerms(sender, "Veto.stat");
				return false;
			}
			showHelp(sender);
			return true;
		}
		
		if(args.length==3 && args[0].equalsIgnoreCase("vote")) {
			if(!(sender instanceof Player)) {
				noConsole(sender);
				return false;
			}
			if(!sender.hasPermission("Veto.vote")) {
				noPerms(sender, "Veto.vote");
				return false;
			}
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(sender.hasPermission("Veto.survey." + u.getPerm())) {
					noPerms(sender, "Veto.survey." + u.getPerm());
				}
				if(!u.getStarted()) {
					sender.sendMessage(ChatColor.RED + "Umfrage ist nicht gestartet!");
					return false;
				}
				if(u.addVote(Integer.valueOf(args[2]), sender.getName())) {
					sender.sendMessage(ChatColor.AQUA + "Viele Dank für deine Stimme.");
				} else {
					sender.sendMessage(ChatColor.RED + "Du hast bereits abgestimmt.");
				}
				return true;
			}
			notFound(sender, args[1]);
			return false;
		}
		
		Player player = Bukkit.getPlayer(sender.getName());
		if(args.length == 2 && args[0].equalsIgnoreCase("create")) {
			if(!(sender instanceof Player)) { noConsole(sender); return false; }
			if(!player.hasPermission("Veto.create")) { noPerms(player, "Veto.create"); return false; }
			plugin.createUmfrage(args[1], args[2]);
			player.sendMessage(ChatColor.GREEN + "Die Umfrage " + args[1] + " wurde erstellt.");
			return true;
			
		}
		if(args.length == 2 && args[0].equalsIgnoreCase("delete")) {
			if(!(sender instanceof Player)) { noConsole(sender); return false; }
			if(!player.hasPermission("Veto.delete")) { noPerms(player, "Veto.delete"); return false; }
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
				plugin.deleteUmfrage(args[1]);
				player.sendMessage(ChatColor.RED + args[1] + " wurde gelöscht!");
				return true;
			}
			notFound(player, args[1]);
		}
		if(args.length > 2 && args[0].equalsIgnoreCase("edit")) {
			if(!(sender instanceof Player)) { noConsole(sender); return false;}
			if(!player.hasPermission("Veto.edit")) { noPerms(player, "Veto.edit"); return false; }
			if(args[1].equalsIgnoreCase("topic")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!player.hasPermission("Veto.edit.topic")) { noPerms(player, "Veto.edit.topic"); return false; }
					if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					String str = "";
					int i=0;
					for(i=4;!args[i].equals("\"");i++) {
						str = str.concat(args[i] + " ");
					}
					plugin.editTopic(args[2], str, Integer.valueOf(args[i+1]));
					player.sendMessage(ChatColor.GREEN + "Topic changed.");
					return true;
				}
				notFound(sender, args[2]);
				return true;
			}
			if(args[1].equalsIgnoreCase("perm")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!player.hasPermission("Veto.edit.perm")) { noPerms(player, "Veto.edit.perm"); return false; }
					if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(player, "Veto.survey." + u.getPerm()); return false; }
					plugin.editPerm(args[2], args[3]);
					player.sendMessage(ChatColor.GREEN + "Neue Rechte gesetzt.");
					return true;
				}
				notFound(player, args[2]);
				return true;
			}
			if(args[1].equalsIgnoreCase("end")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!player.hasPermission("Veto.edit.end")) { noPerms(player, "Veto.edit.end"); return false; }
					if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					String str = "";
					int i=0;
					if(!args[4].equals(args[5])) {
						for(i=4;!args[i].equals("\"");i++) {
							str = str.concat(args[i] + " ");
						}
					}
					plugin.editEnde(args[2], str);
					player.sendMessage(ChatColor.GREEN + "Date changed.");
					return true;
				}
				notFound(player, args[2]);
				return true;
			}
			if(args.length > 3 &&args[1].equalsIgnoreCase("add")) {
				if(!player.hasPermission("Veto.edit.add")) { noPerms(player, "Veto.edit.add"); return false; }
				if(args[2].equalsIgnoreCase("vote")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!player.hasPermission("Veto.edit.add.vote")) { noPerms(player, "Veto.edit.add.vote"); return false; }
						if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						String str = "";
						int i=0;
						for(i=3;args.length>i;i++) {
							str = str.concat(args[i] + " ");
						}
						plugin.addVote(args[3], str);
						player.sendMessage(ChatColor.GREEN + "Antwort hinzugefügt.");
						return true;
					}
					notFound(sender, args[3]);
					return true;
				}
				if(args[2].equalsIgnoreCase("player")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!player.hasPermission("Veto.edit.add.player")) { noPerms(player, "Veto.edit.add.player"); return false; }
						if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						if(plugin.addPlayer(args[3],args[4])) {
							player.sendMessage(ChatColor.GREEN + "Spieler hinzugefügt.");
						} else {
							player.sendMessage(ChatColor.GREEN + "Spieler ist bereits in Liste vorhanden.");
						}
						return true;
					}
					notFound(player, args[3]);
					return true;
				}
			}
			if(args[1].equalsIgnoreCase("remove")) {
				if(!player.hasPermission("Veto.edit.remove")) { noPerms(player, "Veto.edit.remove"); return false; }
				if(args[2].equalsIgnoreCase("vote")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!player.hasPermission("Veto.edit.remove.vote")) { noPerms(player, "Veto.edit.remove.vote"); return false; }
						if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						String vote = "";
						int i=0;
						for(i=5;!args[i].equals("\"");i++) {
							vote = vote.concat(args[i] + " ");
						}
						plugin.removeVote(args[3], vote);
						player.sendMessage(ChatColor.GREEN + "Antwort entfernt.");
						return true;
					}
					notFound(player, args[3]);
					return true;
				}
				if(args[2].equalsIgnoreCase("player")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!player.hasPermission("Veto.edit.remove.player")) { noPerms(player, "Veto.edit.remove.player"); return false; }
						if(!player.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						if(plugin.removePlayer(args[3], args[4])) {
							player.sendMessage(ChatColor.GREEN + "Spieler entfernt.");
						} else {
							player.sendMessage(ChatColor.GREEN + "Spieler ist nicht in Liste vorhanden.");
						}
						return true;
					}
					notFound(player, args[3]);
					return true;
				}
			}
			return true;
		}
		return true;
	}
	
	public void notFound(CommandSender sender, String name) {
		sender.sendMessage(ChatColor.RED + "Die Umfrage " + name + " wurde nicht gefunden!");
	}
	
	public void noPerms(CommandSender sender, String perms) {
		sender.sendMessage(ChatColor.RED + "Die benötigten Rechte sind nicht vorhanden! (" + perms + ")");
	}
	
	public void notVoted(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Du musst erst abstimmen, bevor du die Statistik einsehen kannst!");
	}
	
	public void noConsole(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spieler benutzt werden!");
	}
	
	public void showHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "Dies ist die Hilfe für das Veto-Plugin");
		sender.sendMessage(ChatColor.GRAY + "(Es werden nur die Befehle für deine Rechte angezeigt)");
		sender.sendMessage(ChatColor.AQUA + "/veto:" + ChatColor.GOLD + " Basis Befehl.");
		if(sender.hasPermission("Veto.reload")) { sender.sendMessage(ChatColor.AQUA + "/veto reload:" + ChatColor.GOLD + " Lädt die config und die Umfragen aus dem angegebenen Ordner manuell neu."); }
		if(sender.hasPermission("Veto.info")) { sender.sendMessage(ChatColor.AQUA + "/veto info:" + ChatColor.GOLD + " Zeigt die aktuell geladenen Umfragen an. " + ChatColor.RED + "Rot" + ChatColor.GOLD +" = nicht gestartet, " + ChatColor.GREEN + "Grün" +ChatColor.GOLD + " = gestartet."); }
		if(sender.hasPermission("Veto.info.pList")) { sender.sendMessage(ChatColor.AQUA + "/veto info playerlist <Umfrage>:" + ChatColor.GOLD + " Zeigt die Spieler an, welche bereits agestimmt haben."); }
		if(sender.hasPermission("Veto.list")) { sender.sendMessage(ChatColor.AQUA + "/veto list:" + ChatColor.GOLD + " Zeigt eine Liste der Umfragen mit der ersten Zeile des Themas."); }
		if(sender.hasPermission("Veto.vote")) { sender.sendMessage(ChatColor.AQUA + "/veto vote <Umfrage> <Nummer>:" + ChatColor.GOLD + " Für eine Umfrage abstimmen. Die Nummer ist der Index der Frage."); }
		if(sender.hasPermission("Veto.start")) { sender.sendMessage(ChatColor.AQUA + "/veto start <Umfrage>:" + ChatColor.GOLD + " Startet eine Umfrage."); }
		if(sender.hasPermission("Veto.end")) { sender.sendMessage(ChatColor.AQUA + "/veto end <Umfrage>:" + ChatColor.GOLD + " Beendet eine Umfrage."); }
		if(sender.hasPermission("Veto.create")) { sender.sendMessage(ChatColor.AQUA + "/veto create <Umfrage> <Permission>:" + ChatColor.GOLD + " Erstellt eine Umfrage mit dem Name und den Rechten."); }
		if(sender.hasPermission("Veto.delete")) { sender.sendMessage(ChatColor.AQUA + "/veto delete <Umfrage>:" + ChatColor.GOLD + " Löscht eine Umfrage."); }
		if(sender.hasPermission("Veto.edit.topic")) { sender.sendMessage(ChatColor.AQUA + "/veto edit topic <Umfrage> \" <Thema> \" <Zeile>:" + ChatColor.GOLD + " Ändert das Thema in der gewünschten Zeile."); }
		if(sender.hasPermission("Veto.edit.perm")) { sender.sendMessage(ChatColor.AQUA + "/veto edit perm <Umfrage> <Permission>" + ChatColor.GOLD + " Ändert die benötigten Permissions"); }
		if(sender.hasPermission("Veto.edit.end")) { sender.sendMessage(ChatColor.AQUA + "/veto edit end <Umfrage> \" <Datum> \":" + ChatColor.GOLD + " Ändert das Datum für das Automatische Beenden."); }
		if(sender.hasPermission("Veto.edit.autooff")) { sender.sendMessage(ChatColor.AQUA + "/veto edit autooff <Umfrage> <true/false>:" + ChatColor.GOLD + " Automatisches Ende ein bzw. ausschalten."); }
		if(sender.hasPermission("Veto.edit.add.vote")) { sender.sendMessage(ChatColor.AQUA + "/veto edit add vote <Umfrage> <Antowrt>:" + ChatColor.GOLD + " Erstellt eine Antwort in einer Umfrage."); }
		if(sender.hasPermission("Veto.edit.remove.vote")) { sender.sendMessage(ChatColor.AQUA + "/veto edit remove vote <Umfrage> \" <Antwort> \":" + ChatColor.GOLD + " Löscht eine Anwort."); }
		if(sender.hasPermission("Veto.edit.add.player")) { sender.sendMessage(ChatColor.AQUA + "/veto edit add player <Umfrage> <Name>:" + ChatColor.GOLD + " Fügt einen Spieler zur Spielerliste einer Umfrage hinzu."); }
		if(sender.hasPermission("Veto.edit.remove.player")) { sender.sendMessage(ChatColor.AQUA + "/veto edit remove player <Umfrage> <Name>:" + ChatColor.GOLD + " Löscht einene Spieler aus der Spielerliste einer Umfrage."); }
	}
}

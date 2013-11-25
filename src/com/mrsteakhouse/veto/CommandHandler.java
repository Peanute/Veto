package com.mrsteakhouse.veto;

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
			showHelp(sender, "all");
			return true;
		}
		
		if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
			if(!sender.hasPermission("Veto.help")) {
				noPerms(sender, "Veto.help");
				return false;
			}
			showHelp(sender, "all");
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
			showHelp(sender, "info");
			return true;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("playerlist")) {
			if(!sender.hasPermission("Veto.info.pList")) {
				noPerms(sender, "Veto.info.pList");
				return false;
			}
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
				u.printPlayerlist(sender);
				return true;
			}
			notFound(sender, args[1]);
			showHelp(sender, "pl");
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
			showHelp(sender, "start");
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
			showHelp(sender, "end");
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
			showHelp(sender, "stat");
			return true;
		}
		
		if(args.length>=3 && args[0].equalsIgnoreCase("vote")) {
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
				if(!sender.hasPermission("Veto.survey." + u.getPerm())) {
					noPerms(sender, "Veto.survey." + u.getPerm());
					return false;
				}
				if(!u.getStarted()) {
					sender.sendMessage(ChatColor.RED + "Umfrage ist nicht gestartet!");
					return false;
				}
				if(u.getMulChoice()) {
					Integer[] answers = new Integer[args.length-2];
					for(int i = 0; i<answers.length; i++) { answers[i] = Integer.valueOf(args[i+2]); }
					
					if(u.addMulVote(answers, sender.getName())) {
						sender.sendMessage(ChatColor.AQUA + "Viele Dank für deine Stimme.");
					} else {
						sender.sendMessage(ChatColor.RED + "Du hast bereits abgestimmt.");
					}
				} else {
					if(u.addVote(Integer.valueOf(args[2]), sender.getName())) {
						sender.sendMessage(ChatColor.AQUA + "Viele Dank für deine Stimme.");
					} else {
						sender.sendMessage(ChatColor.RED + "Du hast bereits abgestimmt.");
					}
				}
				return true;
			}
			notFound(sender, args[1]);
			showHelp(sender, "vote");
			return false;
		}
		
		if(args.length == 2 && args[0].equalsIgnoreCase("create")) {
			if(!sender.hasPermission("Veto.create")) { noPerms(sender, "Veto.create"); return false; }
			plugin.createUmfrage(args[1], args[2]);
			sender.sendMessage(ChatColor.GREEN + "Die Umfrage " + args[1] + " wurde erstellt.");
			return true;
			
		}
		if(args.length == 2 && args[0].equalsIgnoreCase("delete")) {
			if(!sender.hasPermission("Veto.delete")) { noPerms(sender, "Veto.delete"); return false; }
			Umfrage u = plugin.getUmfrage(args[1]);
			if(u != null) {
				if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
				plugin.deleteUmfrage(args[1]);
				sender.sendMessage(ChatColor.RED + args[1] + " wurde gelöscht!");
				return true;
			}
			notFound(sender, args[1]);
			showHelp(sender, "delete");
			return false;
		}
		if(args.length > 2 && args[0].equalsIgnoreCase("-e")) {
			if(!sender.hasPermission("Veto.edit")) { noPerms(sender, "Veto.edit"); return false; }
			if(args[1].equalsIgnoreCase("topic")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(isInteger(args[3])) {
						u.editTopic("", Integer.valueOf(args[3]));
						sender.sendMessage(ChatColor.GREEN + "Thema aus Zeile entfernt");
						return true;
					}
					if(!sender.hasPermission("Veto.edit.topic")) { noPerms(sender, "Veto.edit.topic"); return false; }
					if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					String str = "";
					int i=0;
					try {
						for(i=3;!isInteger(args[i]);i++) {
							str = str.concat(args[i] + " ");
						}
						u.editTopic(str, Integer.valueOf(args[i]));
					} catch(Exception e) {
						sender.sendMessage(ChatColor.RED + "Das letzte Argument muss eine Zahl sein!");
						showHelp(sender, "edit-topic");
						return false;
					}
					sender.sendMessage(ChatColor.GREEN + "Thema geändert.");
					return true;
				}
				notFound(sender, args[2]);
				showHelp(sender, "edit-topic");
				return true;
			}
			if(args[1].equalsIgnoreCase("perm")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!sender.hasPermission("Veto.edit.perm")) { noPerms(sender, "Veto.edit.perm"); return false; }
					if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					u.setPerm(args[3]);
					sender.sendMessage(ChatColor.GREEN + "Neue Rechte gesetzt.");
					return true;
				}
				notFound(sender, args[2]);
				showHelp(sender, "edit-perm");
				return true;
			}
			
			if(args[1].equalsIgnoreCase("mc")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!sender.hasPermission("Veto.edit.mchoice")) { noPerms(sender, "Veto.edit.mchoice"); return false; }
					if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					u.setMulChoice(Boolean.valueOf(args[3]));
					sender.sendMessage(ChatColor.GREEN + "Multiple Choice wurde geändert.");
					return true;
				}
				notFound(sender, args[2]);
				showHelp(sender, "edit-mc");
				return true;
			}
			
			if(args[1].equalsIgnoreCase("ao")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!sender.hasPermission("Veto.edit.autooff")) { noPerms(sender, "Veto.edit.autooff"); return false; }
					if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					u.setAutoEnd(Boolean.valueOf(args[3]));
					return true;
				}
				notFound(sender, args[2]);
				showHelp(sender, "edit-ao");
				return true;
			}
			
			if(args[1].equalsIgnoreCase("end")) {
				Umfrage u = plugin.getUmfrage(args[2]);
				if(u != null) {
					if(!sender.hasPermission("Veto.edit.end")) { noPerms(sender, "Veto.edit.end"); return false; }
					if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
					String str = "";
					int i=0;
					for(i=3;i<args.length;i++) {
						str = str.concat(args[i] + " ");
					}
					u.setEnde(str);
					sender.sendMessage(ChatColor.GREEN + "Datum geändert.");
					return true;
				}
				notFound(sender, args[2]);
				showHelp(sender, "edit-enddate");
				return true;
			}
			if(args.length > 3 &&args[1].equalsIgnoreCase("player")) {
				if(!sender.hasPermission("Veto.edit.add")) { noPerms(sender, "Veto.edit.add"); return false; }
				if(args[2].equalsIgnoreCase("-a")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!sender.hasPermission("Veto.edit.player.add")) { noPerms(sender, "Veto.edit.player.add"); return false; }
						if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						if(u.addPlayer(args[4])) {
							sender.sendMessage(ChatColor.GREEN + "Spieler hinzugefügt.");
						} else {
							sender.sendMessage(ChatColor.GREEN + "Spieler ist bereits in Liste vorhanden.");
						}
						return true;
					}
					notFound(sender, args[3]);
					showHelp(sender, "edit-player-a");
					return true;
				}
				if(args[2].equalsIgnoreCase("-r")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!sender.hasPermission("Veto.edit.player.remove")) { noPerms(sender, "Veto.edit.player.remove"); return false; }
						if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						if(u.editPlayerRemove(args[3], args[4])) {
							sender.sendMessage(ChatColor.GREEN + "Spieler entfernt.");
						} else {
							sender.sendMessage(ChatColor.GREEN + "Spieler ist nicht in Liste vorhanden.");
						}
						return true;
					}
					notFound(sender, args[3]);
					showHelp(sender, "edit-player-r");
					return true;
				}
			}
			if(args[1].equalsIgnoreCase("vote")) {
				if(!sender.hasPermission("Veto.edit.vote")) { noPerms(sender, "Veto.edit.vote"); return false; }
				if(args[2].equalsIgnoreCase("-a")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!sender.hasPermission("Veto.edit.vote.add")) { noPerms(sender, "Veto.edit.vote.add"); return false; }
						if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						String str = "";
						int i=0;
						for(i=4;args.length>i;i++) {
							str = str.concat(args[i] + " ");
						}
						u.editVoteAdd(str, 0);
						sender.sendMessage(ChatColor.GREEN + "Antwort hinzugefügt.");
						return true;
					}
					notFound(sender, args[3]);
					showHelp(sender, "edit-vote-a");
					return true;
				}
				if(args[2].equalsIgnoreCase("-r")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!sender.hasPermission("Veto.edit.vote.remove")) { noPerms(sender, "Veto.edit.vote.remove"); return false; }
						if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						if(u.editVoteRemove(Integer.valueOf(args[4]))) {
							sender.sendMessage(ChatColor.GREEN + "Antwort entfernt.");
						}
						else {
							sender.sendMessage(ChatColor.RED + "Index ist nicht vorhanden!");
							showHelp(sender, "edit-vote-r");
						}
						return true;
					}
					notFound(sender, args[3]);
					showHelp(sender, "edit-vote-r");
					return true;
				}
				
				if(args[2].equalsIgnoreCase("-c")) {
					Umfrage u = plugin.getUmfrage(args[3]);
					if(u != null) {
						if(!sender.hasPermission("Veto.edit.vote.count")) { noPerms(sender, "Veto.edit.vote.count"); return false; }
						if(!sender.hasPermission("Veto.survey." + u.getPerm())) { noPerms(sender, "Veto.survey." + u.getPerm()); return false; }
						if(u.editVoteCount(Integer.valueOf(args[4]), Integer.valueOf(args[5]))) {
							sender.sendMessage(ChatColor.GREEN + "Antwort geändert.");
						}
						else {
							sender.sendMessage(ChatColor.RED + "Index ist nicht vorhanden!");
							showHelp(sender, "edit-vote-c");
						}
						return true;
					}
					notFound(sender, args[3]);
					showHelp(sender, "edit-vote-c");
					return true;
				}
			}
		}
		sender.sendMessage(ChatColor.RED + "Benutze \"/veto help\" um die Befehle und deren Syntax anzuzeigen.");
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
		sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern benutzt werden!");
	}
	
	public void showHelp(CommandSender sender, String cmd) {
		if(cmd.equals("all")) { sender.sendMessage(ChatColor.BOLD + "Dies ist die Hilfe für das Veto-Plugin"); }
		if(cmd.equals("all")) { sender.sendMessage(ChatColor.GRAY + "(Es werden nur die Befehle für deine Rechte angezeigt)"); }
		if(cmd.equals("all")) { sender.sendMessage(ChatColor.AQUA + "/veto:" + ChatColor.GOLD + " Basis Befehl."); }
		if(sender.hasPermission("Veto.reload") && cmd.equals("all")) { sender.sendMessage(ChatColor.AQUA + "/veto reload:" + ChatColor.GOLD + " Lädt die config und die Umfragen aus dem angegebenen Ordner manuell neu."); }
		if(sender.hasPermission("Veto.info") && cmd.equals("all")) { sender.sendMessage(ChatColor.AQUA + "/veto info:" + ChatColor.GOLD + " Zeigt die aktuell geladenen Umfragen an. " + ChatColor.RED + "Rot" + ChatColor.GOLD +" = nicht gestartet, " + ChatColor.GREEN + "Grün" +ChatColor.GOLD + " = gestartet."); }
		if(sender.hasPermission("Veti.info") && (cmd.equals("all") || cmd.equals("info"))) { sender.sendMessage(ChatColor.AQUA + "/veto info <Umfrage>:" + ChatColor.GOLD + " Zeigt die Details der Umfrage an."); }
		if(sender.hasPermission("Veto.info.pList") && (cmd.equals("all") || cmd.equals("pl"))) { sender.sendMessage(ChatColor.AQUA + "/veto playerlist <Umfrage>:"  + ChatColor.GOLD + " Zeigt die Spieler an, welche bereits agestimmt haben."); }
		if(sender.hasPermission("Veto.list") && cmd.equals("all")) { sender.sendMessage(ChatColor.AQUA + "/veto list:" + ChatColor.GOLD + " Zeigt eine Liste der Umfragen mit der ersten Zeile des Themas."); }
		if(sender.hasPermission("Veto.stat") && (cmd.equals("all") || cmd.equals("stat"))) { sender.sendMessage(ChatColor.AQUA + "/veto stat <Umfrage>:" + ChatColor.GOLD + " Zeigt die Statistik der Umfrage."); }
		if(sender.hasPermission("Veto.vote") && (cmd.equals("all") || cmd.equals("vote"))) { sender.sendMessage(ChatColor.AQUA + "/veto vote <Umfrage> <Nummer>:" + ChatColor.GOLD + " Für eine Umfrage abstimmen. Die Nummer ist der Index der Frage. Wenn Multiple Choice an ist, dann müssen mehere Indizes angegeben werden."); }
		if(sender.hasPermission("Veto.start") && (cmd.equals("all") || cmd.equals("start"))) { sender.sendMessage(ChatColor.AQUA + "/veto start <Umfrage>:" + ChatColor.GOLD + " Startet eine Umfrage."); }
		if(sender.hasPermission("Veto.end") && (cmd.equals("all") || cmd.equals("end"))) { sender.sendMessage(ChatColor.AQUA + "/veto end <Umfrage>:" + ChatColor.GOLD + " Beendet eine Umfrage."); }
		if(sender.hasPermission("Veto.create") && (cmd.equals("all") || cmd.equals("create"))) { sender.sendMessage(ChatColor.AQUA + "/veto create <Umfrage> <Permission>:" + ChatColor.GOLD + " Erstellt eine Umfrage mit dem Name und den Rechten."); }
		if(sender.hasPermission("Veto.delete") && (cmd.equals("all") || cmd.equals("delete"))) { sender.sendMessage(ChatColor.AQUA + "/veto delete <Umfrage>:" + ChatColor.GOLD + " Löscht eine Umfrage."); }
		if(sender.hasPermission("Veto.edit.mchoice") && (cmd.equals("all") || cmd.equals("edit-mc"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e mc <Umfrage> <true/false>:" + ChatColor.GOLD + " Ändert die multiple choice Funktion einer Umfrage."); }
		if(sender.hasPermission("Veto.edit.topic") && (cmd.equals("all") || cmd.equals("edit-topic"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e topic <Umfrage> <Thema> <Zeile>:" + ChatColor.GOLD + " Ändert oder löscht das Thema in der gewünschten Zeile. Zum löschen nur die Zeile angeben."); }
		if(sender.hasPermission("Veto.edit.perm") && (cmd.equals("all") || cmd.equals("edit-perm"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e perm <Umfrage> <Permission>" + ChatColor.GOLD + " Ändert die benötigten Permissions"); }
		if(sender.hasPermission("Veto.edit.end") && (cmd.equals("all") || cmd.equals("edit-enddate"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e end <Umfrage> <Datum>:" + ChatColor.GOLD + " Ändert das Datum für das Automatische Beenden."); }
		if(sender.hasPermission("Veto.edit.autooff") && (cmd.equals("all") || cmd.equals("edit-ao"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e ao <Umfrage> <true/false>:" + ChatColor.GOLD + " Automatisches Ende ein bzw. ausschalten."); }
		if(sender.hasPermission("Veto.edit.vote.add") && (cmd.equals("all") || cmd.equals("edit-vote-a"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e vote -a <Umfrage> <Antowrt>:" + ChatColor.GOLD + " Erstellt eine Antwort in einer Umfrage."); }
		if(sender.hasPermission("Veto.edit.vote.remove") && (cmd.equals("all") || cmd.equals("edit-vote-r"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e vote -r <Umfrage> <Index>:" + ChatColor.GOLD + " Löscht eine Anwort mit dem Index."); }
		if(sender.hasPermission("Veto.edit.player.add") && (cmd.equals("all") || cmd.equals("edit-player-a"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e player -a <Umfrage> <Name>:" + ChatColor.GOLD + " Fügt einen Spieler zur Spielerliste einer Umfrage hinzu."); }
		if(sender.hasPermission("Veto.edit.vote.count") && (cmd.equals("all") || cmd.equals("edit-vote-c"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e vote -c <Umfrage> <Index> <Anzahl>" + ChatColor.GOLD + " Ändert die Anzahl die auf diese Antwort bereits abegstimmt wurden"); }
		if(sender.hasPermission("Veto.edit.player.remove") && (cmd.equals("all") || cmd.equals("edit-player-r"))) { sender.sendMessage(ChatColor.AQUA + "/veto -e player -r <Umfrage> <Name>:" + ChatColor.GOLD + " Löscht einene Spieler aus der Spielerliste einer Umfrage."); }
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}

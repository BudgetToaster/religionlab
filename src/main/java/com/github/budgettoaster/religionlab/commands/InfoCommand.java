package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.perks.PerkType;
import com.github.budgettoaster.religionlab.perks.base.Perk;
import com.github.budgettoaster.religionlab.Religion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.List;

public class InfoCommand extends SubCommand {
    public InfoCommand() {
        super(true,
                "info",
                "/religion info (Name)",
                "religion.basic.info",
                "Shows the sender basic religion information.");
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        if(args.size() == 0)
            return executeNoArgs(sender);
        else
            return executeWithArgs(sender, String.join(" ", args));
    }

    private boolean executeNoArgs(CommandSender sender) {
        Religion religion = Religion.getReligion((OfflinePlayer) sender);
        if(religion == null) {
            sender.sendMessage(ChatColor.RED + "You are not a follower of any religion.");
            return true;
        }

        StringBuilder str = new StringBuilder();
        str.append(MessageFormat.format("{0}--------- {1}{2} {3}---------", ChatColor.YELLOW, ChatColor.RESET, religion.getName(), ChatColor.YELLOW))
                .append(MessageFormat.format("\n{0}Founder: {1}{2}", ChatColor.GOLD, ChatColor.RESET, religion.getFounder().getName()))
                .append(MessageFormat.format("\n{0}Founder Perk: {1}{2}", ChatColor.GOLD, ChatColor.RESET, religion.getFounderPerk().getInGameName(PerkType.FOUNDER)))
                .append(MessageFormat.format("\n{0}Follower Perks:", ChatColor.GOLD));
        if(religion.getFollowerPerks().isEmpty()) {
            str.append(ChatColor.RESET).append(" None");
        }
        else {
            for(Perk perk : religion.getFollowerPerks())
                str.append(MessageFormat.format("\n - {0}", perk.getInGameName(PerkType.FOLLOWER)));
        }
        sender.sendMessage(str.toString().split("\n"));
        return true;
    }

    private boolean executeWithArgs(CommandSender sender, String name) {
        if(!sender.hasPermission("religionlab.basic.info.other")) {
            sender.sendMessage(ChatColor.RED + "You dont have permission to use that command!");
            return true;
        }
        Religion religion = null;
        for(Religion r : Religion.getReligions())
            if(r.getName().equalsIgnoreCase(name)) {
                religion = r;
                break;
            }

        if(religion == null) {
            sender.sendMessage(ChatColor.RED + "That religion doesnt exist.");
            return true;
        }

        StringBuilder str = new StringBuilder();
        str.append(MessageFormat.format("{0}--------- {1}{2} {3}---------", ChatColor.YELLOW, ChatColor.RESET, religion.getName(), ChatColor.YELLOW))
                .append(MessageFormat.format("\n{0}Founder: {1}{2}", ChatColor.GOLD, ChatColor.RESET, religion.getFounder().getName()))
                .append(MessageFormat.format("\n{0}Founder Perk: {1}{2}", ChatColor.GOLD, ChatColor.RESET, religion.getFounderPerk().getInGameName(PerkType.FOUNDER)))
                .append(MessageFormat.format("\n{0}Follower Perks:", ChatColor.GOLD));
        if(religion.getFollowerPerks().isEmpty()) {
            str.append(ChatColor.RESET).append(" None");
        }
        else {
            for(Perk perk : religion.getFollowerPerks())
                str.append(MessageFormat.format("\n - {0}", perk.getInGameName(PerkType.FOLLOWER)));
        }
        sender.sendMessage(str.toString().split("\n"));
        return true;
    }
}

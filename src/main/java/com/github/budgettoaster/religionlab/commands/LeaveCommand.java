package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand extends SubCommand {
    private final boolean founderCanLeave;

    public LeaveCommand() {
        super(true,
                "leave",
                "/religion leave",
                "religionlab.basic.leave",
                "Leave a religion.");
        founderCanLeave = ReligionLab.get().getConfig().getBoolean("founder can leave", false);
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        if(args.size() != 0) return false;

        Religion old = Religion.getReligion((OfflinePlayer) sender);

        if(old == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a religion.");
            return true;
        }

        if(old.getFounder().equals(sender) && !founderCanLeave) {
            sender.sendMessage(ChatColor.RED + "The founder of a religion cannot leave!");
            return true;
        }

        Religion.setReligion((OfflinePlayer) sender, null);
        sender.sendMessage(ChatColor.YELLOW + "You have left " + old.getName() + ".");

        if(old.getNumFollowers() == 0) {
            Player founder = old.getFounder().getPlayer();
            if(founder != null) {
                founder.sendMessage(ChatColor.YELLOW + "Your religion has been forgotten because nobody is following it.");
                old.unregister();
            }
        }

        return true;
    }
}

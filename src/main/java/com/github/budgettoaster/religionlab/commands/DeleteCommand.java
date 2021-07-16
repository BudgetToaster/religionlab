package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.Religion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DeleteCommand extends SubCommand {
    public DeleteCommand() {
        super(false,
                "delete",
                "/religion delete [Name]",
                "religionlab.admin.delete",
                "Deletes a religion");
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        if(args.size() == 0) return false;
        String name = String.join(" ", args);

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

        for(UUID id : religion.getFollowers()) {
            Player player = Bukkit.getPlayer(id);
            if(player != null)
                player.sendMessage(ChatColor.RED + "The religion you are in has been forcibly deleted by an admin.");
        }
        religion.unregister();
        sender.sendMessage(ChatColor.YELLOW + "Religion " + religion.getName() + " has been deleted.");
        return true;
    }
}

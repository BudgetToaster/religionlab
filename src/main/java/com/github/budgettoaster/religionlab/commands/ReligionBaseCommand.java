package com.github.budgettoaster.religionlab.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReligionBaseCommand implements CommandExecutor {
    private static final List<SubCommand> subCommands = new ArrayList<>();

    public static Iterable<SubCommand> getSubCommands() {
        return subCommands;
    }

    static {
        subCommands.add(new CreateCommand());
        subCommands.add(new CreateTextCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new PerkListCommand());
        subCommands.add(new InfoCommand());
        subCommands.add(new EnhanceCommand());
        subCommands.add(new JoinCommand());
        subCommands.add(new LeaveCommand());
        subCommands.add(new ListCommand());
    }

    public void init() {
        for(SubCommand c : subCommands) {
            if(!c.init()) return;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String secondaryCommand;
        if(args.length > 0) secondaryCommand = args[0].toLowerCase();
        else secondaryCommand = "help";
        List<String> otherArgs = Arrays.asList(args).subList(1, args.length);

        for(SubCommand subCommand : subCommands) {
            if(!secondaryCommand.equals(subCommand.getLabel()) && !subCommand.getAliases().contains(secondaryCommand))
                continue;

            if(subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission()))
                sender.sendMessage(ChatColor.RED + "You don't have permission to use that command.");
            else if(subCommand.isPlayerOnly() && !(sender instanceof Player))
                sender.sendMessage(ChatColor.RED + "Only players can execute that command!");
            else if(!subCommand.execute(sender, otherArgs))
                sender.sendMessage(MessageFormat.format("{0}Usage: {1}", ChatColor.RED, subCommand.getUsage()));
            return true;
        }
        sender.sendMessage("Unknown command. Type /religion help for help.");

        return true;
    }



}

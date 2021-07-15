package com.github.budgettoaster.religionlab.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.List;

public class HelpCommand extends SubCommand {
    public HelpCommand() {
        super(
                false,
                "help",
                "/religion help",
                null,
                "List of /religion subcommands."
        );
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        StringBuilder msg = new StringBuilder();
        msg.append(MessageFormat.format("{0}--------- {1}ReligionLab Help {2}---------", ChatColor.YELLOW, ChatColor.RESET, ChatColor.YELLOW));
        for(SubCommand command : ReligionBaseCommand.getSubCommands()) {
            if(command.getPermission() == null || sender.hasPermission(command.getPermission()))
                msg.append(MessageFormat.format("\n{0}/religion {1}: {2}{3}",
                        ChatColor.GOLD, command.getLabel(), ChatColor.RESET, command.getDescription()));
        }
        sender.sendMessage(msg.toString().split("\n"));
        return true;
    }
}

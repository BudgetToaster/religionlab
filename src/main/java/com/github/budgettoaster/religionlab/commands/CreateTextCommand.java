package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.ReligionLab;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateTextCommand extends SubCommand {
    public CreateTextCommand() {
        super(
                true,
                "createtext",
                "/religion createtext",
                "religion.admin.createtext",
                "Creates an ancient text for the sender."
        );
    }

    public boolean execute(CommandSender sender, List<String> args) {
        ((Player) sender).getInventory().addItem(ReligionLab.get().getAncientTextGenerator().createAncientText());
        return true;
    }
}

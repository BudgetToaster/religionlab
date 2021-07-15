package com.github.budgettoaster.religionlab.perks.base;

import com.github.budgettoaster.religionlab.perks.PerkType;
import org.bukkit.event.Listener;

public interface Perk extends Listener {
    String getInGameName(PerkType type);

    String getDescription();

    @Override
    String toString();
}

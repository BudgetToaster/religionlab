package com.github.budgettoaster.religionlab.perks;

import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.perks.base.SimplePerk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.math.BigDecimal;

public class StrengthPerk extends SimplePerk {
    StrengthPerk() {
        super("strength", "bonus damage", VarType.SCALAR);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent ev) {
        if(!(ev.getDamager() instanceof Player))
            return;

        Player player = (Player) ev.getDamager();
        Religion religion = Religion.getReligion(player);
        if(religion == null) return;

        double damageMultiplier = 0.2 + 0.8 * Math.pow(player.getAttackCooldown(), 2);

        BigDecimal numFollowers = BigDecimal.valueOf(religion.getNumFollowers());
        if(isFounderEnabled() && getFounderExpression() != null &&
                religion.getFounder().equals(player) && religion.getFounderPerk() == this) {
            double extraDmg = getFounderExpression().with("x", numFollowers).eval().doubleValue();
            ev.setDamage(ev.getDamage() + damageMultiplier * extraDmg);
        }

        if(isFollowerEnabled() && getFollowerExpression() != null &&
                religion.getFollowerPerks().contains(this)) {
            double extraDmg = getFollowerExpression().with("x", numFollowers).eval().doubleValue();
            ev.setDamage(ev.getDamage() + damageMultiplier * extraDmg);
        }
    }

    @Override
    public String getDescription() {
        return "Do extra damage to enemies.";
    }
}

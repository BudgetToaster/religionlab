package com.github.budgettoaster.religionlab.perks;

import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.perks.base.SimplePerk;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.math.BigDecimal;

public class PrecisionPerk extends SimplePerk {
    PrecisionPerk() {
        super("precision", "bonus damage", VarType.SCALAR);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent ev) {
        if(!(ev.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) ev.getDamager();
        if(!(arrow.getShooter() instanceof Player)) return;

        Player player = (Player) arrow.getShooter();
        Religion religion = Religion.getReligion(player);
        if(religion == null) return;

        BigDecimal numFollowers = BigDecimal.valueOf(religion.getNumFollowers());
        if(isFounderEnabled() && getFounderExpression() != null &&
                religion.getFounder().equals(player) && religion.getFounderPerk() == this) {
            double extraDmg = getFounderExpression().with("x", numFollowers).eval().doubleValue();
            ev.setDamage(ev.getDamage() + extraDmg);
        }

        if(isFollowerEnabled() && getFollowerExpression() != null &&
                religion.getFollowerPerks().contains(this)) {
            double extraDmg = getFollowerExpression().with("x", numFollowers).eval().doubleValue();
            ev.setDamage(ev.getDamage() + extraDmg);
        }
    }

    @Override
    public String getDescription() {
        return "Do extra damage to enemies.";
    }
}

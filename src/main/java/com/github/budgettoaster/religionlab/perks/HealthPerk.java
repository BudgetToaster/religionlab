package com.github.budgettoaster.religionlab.perks;

import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import com.github.budgettoaster.religionlab.perks.base.SimplePerk;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class HealthPerk extends SimplePerk {
    HealthPerk() {
        super("health", "bonus", VarType.SCALAR);
        ReligionLab.get().getServer().getScheduler().scheduleSyncRepeatingTask(ReligionLab.get(), this::update, 12, 40);
    }

    private void update() {
        for(Player player : ReligionLab.get().getServer().getOnlinePlayers()) {
            Religion religion = Religion.getReligion(player);

            AttributeInstance attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert attr != null;
            // Remove old modifiers
            for(AttributeModifier mod : attr.getModifiers()) {
                if(mod.getName().equals("ReligionLab.HealthPerk.Founder")) {
                    attr.removeModifier(mod);
                }
                else if(mod.getName().equals("ReligionLab.HealthPerk.Follower")) {
                    attr.removeModifier(mod);
                }
            }

            if(religion == null) continue;

            BigDecimal numFollowers = BigDecimal.valueOf(religion.getNumFollowers());
            if(isFounderEnabled() && getFounderExpression() != null &&
                    religion.getFounder().equals(player) && religion.getFounderPerk() == this) {
                double extraHealth = getFounderExpression().with("x", numFollowers).eval().doubleValue() / 10;
                attr.addModifier(new AttributeModifier("ReligionLab.HealthPerk.Founder", extraHealth, AttributeModifier.Operation.ADD_SCALAR));
            }
            if(isFollowerEnabled() && getFollowerExpression() != null &&
                    religion.getFollowerPerks().contains(this)) {
                double extraHealth = getFollowerExpression().with("x", numFollowers).eval().doubleValue() / 10;
                attr.addModifier(new AttributeModifier("ReligionLab.HealthPerk.Follower", extraHealth, AttributeModifier.Operation.ADD_SCALAR));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Increases max health.";
    }
}

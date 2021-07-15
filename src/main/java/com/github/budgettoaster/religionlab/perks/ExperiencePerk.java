package com.github.budgettoaster.religionlab.perks;

import com.github.budgettoaster.religionlab.perks.base.SimplePerk;
import com.github.budgettoaster.religionlab.Religion;
import dev.xanhub.religionlab.perks.base.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.math.BigDecimal;
import java.util.Random;

public class ExperiencePerk extends SimplePerk {
    private final Random random = new Random();

    ExperiencePerk() {
        super("experience", "multiplier", VarType.SCALAR);
    }

    @EventHandler
    public void onXpGain(PlayerExpChangeEvent ev) {
        Player player = ev.getPlayer();
        Religion religion = Religion.getReligion(player);
        if(religion == null) return;

        BigDecimal numFollowers = BigDecimal.valueOf(religion.getNumFollowers());
        if(isFounderEnabled() && getFounderExpression() != null &&
                religion.getFounder().equals(player) && religion.getFounderPerk() == this) {
            double amt = ev.getAmount();
            double multiplier = getFounderExpression().with("x", numFollowers).eval().doubleValue();
            amt *= multiplier;
            ev.setAmount((int)(amt + random.nextDouble()));
        }
        if(isFollowerEnabled() && getFollowerExpression() != null &&
                religion.getFollowerPerks().contains(this)) {
            double amt = ev.getAmount();
            double multiplier = getFollowerExpression().with("x", numFollowers).eval().doubleValue();
            amt *= multiplier;
            ev.setAmount((int)(amt + random.nextDouble()));
        }
    }

    @Override
    public String getDescription() {
        return "Increases your experience gain.";
    }
}

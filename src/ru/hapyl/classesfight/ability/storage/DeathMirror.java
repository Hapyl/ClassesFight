package ru.hapyl.classesfight.ability.storage;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TakerClass;
import ru.hapyl.classesfight.classes.iclass.extra.TakerData;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;

public class DeathMirror extends Ability {
    public DeathMirror() {
        super("Mirror of Death", "Instantly consume one Spiritual Bone and create shield that absorbs any damage for &b5s&7.");
        this.setItem(Material.DRIED_KELP);
        this.setCooldown(200);
    }

    @Override
    public Response useAbility(Player player) {

        final TakerData data = ((TakerClass)ClassManager.TAKER.getTheClass()).getData(player);

        if (data.getBoneCharges() <= 0) {
            return Response.error("Not enough bones!");
        }

        data.removeBone();
        GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, 5 * 20);

        return Response.OK;
    }
}

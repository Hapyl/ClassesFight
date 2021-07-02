package ru.hapyl.classesfight.gamemode.mode;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.gamemode.CFGameMode;
import ru.hapyl.classesfight.gamemode.wc.WinConditionEnum;

public class Normal extends CFGameMode {

	public Normal() {
		super("Normal", 600, WinConditionEnum.LAST_MAN_STANDING);
	}

	@Override
	public void processBeforeGameStart() {

	}

	@Override
	public void processPlayerBeforeGameStart(Player player) {

	}

	@Override
	public void processAfterGameEnd() {

	}

	@Override
	public void processPlayerAfterGameEnd(Player player) {

	}

	@Override
	public void processPlayerDeath(Player player, Player killer) {
		final Database database = Database.getDatabase(player);
		final EnumEffect effect = database.getCosmetics().getCurrentEffect(EnumEffect.Type.DEATH);

		if (effect != null && effect.isEnabled(player)) {
			effect.display(player);
		}

		GameManager.current().getPlayers().remove(player);

	}
}

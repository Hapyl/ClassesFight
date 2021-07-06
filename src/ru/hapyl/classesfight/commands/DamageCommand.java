/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;

public class DamageCommand extends SimplePlayerAdminCommand {

	public DamageCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(Player player, String[] strings) {
		// damage dmg STRENGTH DEFENSE
		if (strings.length == 3) {
			final double damage = Validate.getDouble(strings[0]);
			final EnumInfo attack = Validate.getEnumValue(EnumInfo.class, strings[1]);
			final EnumInfo defense = Validate.getEnumValue(EnumInfo.class, strings[2]);
			if (attack == null || defense == null) {
				sendInvalidUsageMessage(player);
				return;
			}

			final double finalDamage = DamageFeature.calculateDamage(damage, attack, defense);
			Chat.sendMessage(player, "&aYou would deal %s damage. (Str: %s, Def: %s)", finalDamage, attack.name(), defense.name());

			return;
		}
		sendInvalidUsageMessage(player);
	}

}
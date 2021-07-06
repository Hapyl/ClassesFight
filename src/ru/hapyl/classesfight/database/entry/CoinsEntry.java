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

package ru.hapyl.classesfight.database.entry;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import ru.hapyl.classesfight.database.Database;

public class CoinsEntry extends DatabaseEntry {

	public CoinsEntry(Database database) {
		super(database);
	}

	public void grantCoins(long amount, boolean checkForBooster, @Nullable String reason) {
		final boolean hasBooster = this.database.getBoosterEntry().hasBooster(1);
		final long finalAmount = checkForBooster ? (hasBooster ? amount * 2 : amount) : amount;
		addCoins(amount);
		if (reason != null) {
			Chat.sendMessage(this.database.getPlayer(), "&6&lCOINS! &e+%s &eCoins %s&7(%s)", finalAmount, Chat.format(hasBooster ? "&e&lBOOSTED " : ""), reason);
		}
	}

	public long getCoins() {
		return this.database.getYaml().getLong("coins", 0L);
	}

	public void setCoins(long toSet) {
		this.database.getYaml().set("coins", toSet);
	}

	public void addCoins(long toAdd) {
		setCoins(getCoins() + toAdd);
	}

	public void removeCoins(long toRemove) {
		setCoins(getCoins() - toRemove);
	}

	public boolean hasCoins(long atLeast) {
		final long coins = Math.max(0L, getCoins());
		return coins >= atLeast;
	}

}

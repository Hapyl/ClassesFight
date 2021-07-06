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

package ru.hapyl.classesfight.feature;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;

public class RedeemableCode {

	private final String name;
	private final Code code;
	private final long validUntil;

	public RedeemableCode(String name, Code code, long validUntil) {
		this.name = name;
		this.code = code;
		this.validUntil = -1L;
	}

	public Code getCode() {
		return code;
	}

	public void redeem(Player player) {

	}

	public boolean haveRedeemed(Player player) {
		return Database.getDatabase(player).getBooleanEntry().getBoolean("code." + this.code.getCode(), false);
	}


}

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

package ru.hapyl.classesfight.ability;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class Response {

	public static final Response OK = new Response(null, ResponseStatus.OK);
	public static final Response ERROR = new Response(null, ResponseStatus.ERROR);

	private final String about;
	private final ResponseStatus status;

	public Response(String about, ResponseStatus status) {
		this.about = about;
		this.status = status;
	}

	public static Response error(String why) {
		return new Response(why, ResponseStatus.ERROR);
	}

	public boolean isOk() {
		return this.status == ResponseStatus.OK;
	}

	public boolean isError() {
		return this.status == ResponseStatus.ERROR;
	}

	public void sendMessageIfError(Player player) {
		if (this.isError()) {
			if (this.getReason() == null) {
				return;
			}
			Chat.sendMessage(player, "&cCannot use this! " + this.getReason());
		}
	}

	public String getReason() {
		return about;
	}

	public enum ResponseStatus {
		OK, ERROR
	}

}

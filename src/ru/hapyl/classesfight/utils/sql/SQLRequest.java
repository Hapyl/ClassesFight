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

package ru.hapyl.classesfight.utils.sql;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public final class SQLRequest {

	/**
	 * SELECT, INSERT, UPDATE, DELETE, BACKUP LOG, BACKUP DATABASE, DBCC, FOR,
	 */

	static {

		new SQLRequest.Select().From("");

	}

	public SQLRequest() {
	}

	public static class Create implements Supplier {

		@Override
		public void Into(String str) {

		}

		@Override
		public SQLData From(String str) {
			return null;
		}
	}

	public static class Select implements Supplier {

		@Override
		public void Into(String str) {

		}

		@Override
		public SQLData From(String str) {
			return null;
		}
	}

	private interface Supplier {

		default boolean In(String str) {
			return false;
		}

		default void Into(String str) {

		}

		default SQLData From(String str) {
			return new SQLData() {
				@Override
				public String getSQLTypeName() throws SQLException {
					return "null";
				}

				@Override
				public void readSQL(SQLInput stream, String typeName) throws SQLException {

				}

				@Override
				public void writeSQL(SQLOutput stream) throws SQLException {

				}
			};
		}
	}

}

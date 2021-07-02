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

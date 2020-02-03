package live.dobbie.core.dictionary.sql;

public class SQL {

    public static final String
            TABLE_NAME = "primitives",
            KEY_COLUMN = "key",
            VALUE_COLUMN = "value";

    public static final String
            SELECT = "SELECT " + VALUE_COLUMN + " FROM " + TABLE_NAME + " WHERE " + KEY_COLUMN + " = ?;",
            EXISTS = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + KEY_COLUMN + " = ?;",
            INSERT = "INSERT INTO " + TABLE_NAME + "(" + KEY_COLUMN + ", " + VALUE_COLUMN + ") VALUES (?, ?);",
            UPDATE = "UPDATE " + TABLE_NAME + " SET " + VALUE_COLUMN + " = ? WHERE " + KEY_COLUMN + " = ?;",
            DELETE = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_COLUMN + " = ?;";

    private SQL() {
    }
}

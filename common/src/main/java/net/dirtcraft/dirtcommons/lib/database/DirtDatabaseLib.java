package net.dirtcraft.dirtcommons.lib.database;

import java.nio.file.Path;
import java.sql.Connection;

public class DirtDatabaseLib {

    public static Connection getConnection(String database, Path directory) {
        return SQLManager.getConnection(database, directory);
    }
}
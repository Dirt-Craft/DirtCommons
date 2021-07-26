package net.dirtcraft.dirtcommons.api;

import net.dirtcraft.dirtcommons.lib.database.SQLManager;

import java.nio.file.Path;
import java.sql.Connection;

public interface DatabaseProvider {
    static Connection getConnection(String database, Path directory) {
        return SQLManager.getConnection(database, directory);
    }

    static Connection getConnection(String database) {
        return SQLManager.getConnection(database, null);
    }
}

package net.dirtcraft.dirtcommons.lib.database;
import net.dirtcraft.dirtcommons.lib.config.DatabaseConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLManager {

    private static SqlService sqlService = new SqlService();

    static {
        sqlService.buildConnectionCache();
    }

    public static void close(){
        try {
            sqlService.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public static Connection getConnection(String database, Path directory) {
        try {
            return sqlService.getDataSource(getURI(database, directory)).getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static String getURI(String database, Path directory) {
        return "jdbc:mariadb://" + DatabaseConfig.IP +
                ":" + DatabaseConfig.PORT +
                "/" + database +
                "?user=" + DatabaseConfig.USER +
                "&password=" + DatabaseConfig.PASS;
    }

}
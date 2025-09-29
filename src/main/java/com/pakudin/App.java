package com.pakudin;

import org.flywaydb.core.Flyway;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            //Получаем конфиг
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/application.properties"));
            //Запускаем миграцию
            System.out.println("Запуск миграции...");
            Flyway flyway = Flyway.configure()
                    .dataSource(props.getProperty("db.url"),
                            props.getProperty("db.username"),
                            props.getProperty("db.password")
                            )
                    .driver("org.postgresql.Driver")
                    .defaultSchema(props.getProperty("db.schema"))
                    .locations(props.getProperty("flyway.locations"))
                    .baselineOnMigrate(Boolean.parseBoolean(props.getProperty("flyway.baselineOnMigrate")))
                    .load();
            flyway.migrate();
            System.out.println("Миграция завершена!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
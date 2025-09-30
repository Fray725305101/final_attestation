package com.pakudin;

import org.flywaydb.core.Flyway;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            //Получаем конфиг
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/application.properties"));
            if (askForMigration()) {
                runMigration(props);
            } else {
                System.out.println("Миграция пропущена");
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean askForMigration() {
        //Получаем поток байтов, преобразуем в символы, буферизуем для эффективного чтения
        //В нашем случае (1 символ) роли особой не сыграет, но пусть будет
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Приступить к миграции? Y/N: ");
            String input = reader.readLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("Д")) {
                return true;
            } else if (input.equals("N") || input.equals("Н")) {
                return false;
            } else {
                System.err.println("Ошибка ввода. Попробуйте снова");
                return askForMigration(); //Рекурсим до получения внятного ответа
            }
        } catch (Exception e) {
            System.err.println("Ошибка ввода: "+e.getMessage());
            return false;
        }
    }

    public static void runMigration(Properties props) {
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
    }
}
package com.pakudin;

import org.flywaydb.core.Flyway;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

public class App {
    private static Connection connection;

    public static void main(String[] args) {
        try {
            //Получаем конфиг
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/application.properties"));
            System.out.println("Начинаем миграцию");
            System.out.println("Внимание! При миграции все данные в схеме будут удалены!");
            if (askForExec()) {
                runMigration(props); //Мигрируем
            } else {
                System.out.println("Миграция пропущена");
            }
            System.out.println("Демонстрация работы CRUD операций");
            if (askForExec()) {
                crudsOps(props); //Работаем с CRUD операциями
            } else {
                System.out.println("Демонстрация работы CRUD операций пропущена");
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean askForExec() {
        //Получаем поток байтов, преобразуем в символы, буферизуем для эффективного чтения
        //В нашем случае (1 символ) роли особой не сыграет, но пусть будет
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {

            System.out.print("Приступить к выполнению? Y/N: ");
            String input = reader.readLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("Д")) {
                return true;
            } else if (input.equals("N") || input.equals("Н")) {
                return false;
            } else {
                System.err.println("Ошибка ввода. Попробуйте снова");
                return askForExec(); //Рекурсим до получения внятного ответа
            }
        } catch (Exception e) {
            System.err.println("Ошибка ввода: "+e.getMessage());
            return false;
        }
    }

    private static void runMigration(Properties props) {
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
                    .cleanDisabled(Boolean.parseBoolean(props.getProperty("flyway.cleanDisabled")))
                    .load();
            flyway.clean();
            flyway.migrate();
            System.out.println("Миграция завершена!");
    }

    private static void crudsOps(Properties props) {
        Connection conn = null;
        try {
            // Подключаемся к БД через DriverManager
            conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.username"),
                    props.getProperty("db.password")
            );

            // Начинаем транзакцию
            conn.setAutoCommit(false);
            System.out.println("CRUD Операции:");

            // 1. Вставка нового товара и покупателя
            System.out.println("1) Добавление нового товара и нового покупателя");
            int newCustomerId = insertNewCustomer(conn);
            int newProductId = insertNewProduct(conn);

            // 2. Создание заказа для покупателя
            System.out.println("2) Создаём новый заказ");
            int newOrderId = createOrderForCustomer(conn, newCustomerId, newProductId);

            // 3. Чтение и вывод последних 5 заказов
            System.out.println("3) Выбираем последние 5 заказов");
            displayLast5Orders(conn);

            // 4. Обновление цены товара и количества на складе
            System.out.println("4) Обновляем данные товара");
            updateProductPriceAndQuantity(conn, newProductId);

            // 5. Удаление тестовых записей
            System.out.println("5) Удаляем тестовые данные");
            deleteTestRecords(conn, newOrderId, newCustomerId, newProductId);

            // Фиксируем транзакцию
            conn.commit();
            System.out.println("Все CRUD операции выполнены успешно!");

        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Транзакция откатана из-за ошибки");
                }
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате транзакции: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    private static int insertNewCustomer(Connection conn) throws SQLException {
        String sql = "INSERT INTO final_attestation_pakudin.customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Test");
            pstmt.setString(2, "User");
            pstmt.setString(3, "+123234345");
            pstmt.setString(4, "test.user@example.com");
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int customerId = rs.getInt(1);
                        System.out.println("Создан новый покупатель с ID: " + customerId);
                        return customerId;
                    }
                }
            }
            throw new SQLException("Не удалось создать покупателя");
        }
    }

    private static int insertNewProduct(Connection conn) throws SQLException {
        String sql = "INSERT INTO final_attestation_pakudin.product (product_name, price, quantity, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Test Product SSD");
            pstmt.setDouble(2, 7500.00);
            pstmt.setInt(3, 50);
            pstmt.setInt(4, 3);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int productId = rs.getInt(1);
                        System.out.println("Создан новый товар с ID: " + productId);
                        return productId;
                    }
                }
            }
            throw new SQLException("Не удалось создать товар");
        }
    }

    private static int createOrderForCustomer(Connection conn, int customerId, int productId) throws SQLException {
        // Создаем заголовок заказа
        String orderHeadSql = "INSERT INTO final_attestation_pakudin.order_head (customer_id, order_date, status_id) VALUES (?, CURRENT_TIMESTAMP, ?)";
        int orderId;
        try (PreparedStatement pstmt = conn.prepareStatement(orderHeadSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, 1); // Created status
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                        System.out.println("Создан заказ с ID: " + orderId);
                    } else {
                        throw new SQLException("Не удалось получить ID заказа");
                    }
                }
            } else {
                throw new SQLException("Не удалось создать заказ");
            }
        }
        // Добавляем товар в заказ
        String orderBodySql = "INSERT INTO final_attestation_pakudin.order_body (head_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(orderBodySql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, 2);
            pstmt.setDouble(4, 7500.00);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Товар добавлен в заказ");
            }
        }
        return orderId;
    }

    private static void displayLast5Orders(Connection conn) throws SQLException {
        String sql = """
            SELECT 
                oh.id as order_id,
                c.first_name || ' ' || c.last_name as customer_name,
                oh.order_date,
                os.status_name,
                string_agg(p.product_name || ' (x' || ob.quantity || ')', ', ') as products,
                c.phone
            FROM final_attestation_pakudin.order_head oh
            JOIN final_attestation_pakudin.customer c ON c.id = oh.customer_id
            JOIN final_attestation_pakudin.order_status os ON os.id = oh.status_id
            JOIN final_attestation_pakudin.order_body ob ON ob.head_id = oh.id
            JOIN final_attestation_pakudin.product p ON p.id = ob.product_id
            GROUP BY oh.id, c.first_name, c.last_name, oh.order_date, os.status_name, c.phone
            ORDER BY oh.order_date DESC
            LIMIT 5
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("┌─────────┬──────────────────────┬─────────────────────┬──────────────────┬───────────────────────────────────────────┬────────────────┐");
            System.out.println("│ Order ID│ Customer Name        │ Order Date          │ Status           │ Products                                  │ Phone          │");
            System.out.println("├─────────┼──────────────────────┼─────────────────────┼──────────────────┼───────────────────────────────────────────┼────────────────┤");

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String customerName = rs.getString("customer_name");
                Timestamp orderDate = rs.getTimestamp("order_date");
                String status = rs.getString("status_name");
                String products = rs.getString("products");
                String phone = rs.getString("phone");

                // Форматируем вывод в виде таблицы
                System.out.printf("│ %-7d │ %-20s │ %-19s │ %-16s │ %-43s │ %-14s │%n",
                        orderId,
                        truncate(customerName, 20),
                        orderDate.toLocalDateTime().toString().replace('T', ' '),
                        truncate(status, 16),
                        truncate(products, 43),
                        truncate(phone, 14)
                );
            }
            System.out.println("└─────────┴──────────────────────┴─────────────────────┴──────────────────┴───────────────────────────────────────────┴────────────────┘");
        }
    }

    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}
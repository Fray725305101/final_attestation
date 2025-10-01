package com.pakudin;

import org.flywaydb.core.Flyway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
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
            System.out.println("Выполнение test-queries.sql");
            if (askForExec()) {
                executeTestQueries(props); //Работаем с CRUD операциями
            } else {
                System.out.println("Выполнение test-queries.sql пропущено");
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
            System.out.println("Предварительная очистка схемы");
            if (askForExec()) {
                flyway.clean(); //Затираем схему
            } else {
                System.out.println("Данные в схеме сохранены");
            }
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
            //Начинаем транзакцию
            conn.setAutoCommit(false);
            System.out.println("CRUD Операции:");
            //Вставка нового товара и покупателя
            System.out.println("1) Добавление нового товара и нового покупателя");
            int newCustomerId = insertNewCustomer(conn);
            int newProductId = insertNewProduct(conn);
            //Создание заказа для покупателя
            System.out.println("2) Создаём новый заказ");
            int newOrderId = createOrderForCustomer(conn, newCustomerId, newProductId);
            //Чтение и вывод последних 5 заказов
            System.out.println("3) Выбираем последние 5 заказов");
            displayLast5Orders(conn);
            //Обновление цены товара и количества на складе
            System.out.println("4) Обновляем данные товара");
            updateProductPriceAndQuantity(conn, newProductId);
            //Удаление тестовых записей
            System.out.println("5) Удаляем тестовые данные");
            //Коммитим
            conn.commit();
            //Делаем запрос на удаления для возможности проверки
            if (askForExec()) {
                deleteTestRecords(conn, newOrderId, newCustomerId, newProductId);
                conn.commit();
            } else {
                System.out.println("Тестовые данные сохранены");
            }
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

            System.out.println("┌─────────┬──────────────────────┬─────────────────────┬──────────────────┬─────────────────────────────────────────────┬────────────────┐");
            System.out.println("│ Order ID│ Customer Name        │ Order Date          │ Status           │ Products                                    │ Phone          │");
            System.out.println("├─────────┼──────────────────────┼─────────────────────┼──────────────────┼─────────────────────────────────────────────┼────────────────┤");

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String customerName = rs.getString("customer_name");
                Timestamp orderDate = rs.getTimestamp("order_date");
                String status = rs.getString("status_name");
                String products = rs.getString("products");
                String phone = rs.getString("phone");
                String formattedDate = formatDate(orderDate);

                // Форматируем вывод в виде таблицы
                System.out.printf("│ %-7d │ %-20s │ %-19s │ %-16s │ %-43s │ %-14s │%n",
                        orderId,
                        truncate(customerName, 20),
                        formattedDate,
                        truncate(status, 16),
                        truncate(products, 43),
                        truncate(phone, 14)
                );
            }
            System.out.println("└─────────┴──────────────────────┴─────────────────────┴──────────────────┴─────────────────────────────────────────────┴────────────────┘");
        }
    }

    //Метод для обрезки слишком длинных строк
    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    //Метод для форматирования даты
    private static String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        return dateTime.toString().replace('T', ' ').substring(0, 16); // "YYYY-MM-DD HH:mm"
    }

    private static void updateProductPriceAndQuantity(Connection conn, int productId) throws SQLException {
        //Обновляем цену
        String updatePriceSql = "UPDATE final_attestation_pakudin.product SET price = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updatePriceSql)) {
            pstmt.setDouble(1, 8000.00); //Новая цена
            pstmt.setInt(2, productId);
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Обновлена цена товара. Затронуто строк: " + rowsUpdated);
        }
        //Обновляем количество
        String updateQuantitySql = "UPDATE final_attestation_pakudin.product SET quantity = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuantitySql)) {
            pstmt.setInt(1, 45); //Новое количество
            pstmt.setInt(2, productId);
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Обновлено количество товара. Затронуто строк: " + rowsUpdated);
        }
    }

    private static void deleteTestRecords(Connection conn, int orderId, int customerId, int productId) throws SQLException {
        // Удаляем в правильном порядке из-за foreign keys
        System.out.println("Приступаем к удалению");
        //Удаляем тело заказа
        String deleteOrderBodySql = "DELETE FROM final_attestation_pakudin.order_body WHERE head_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteOrderBodySql)) {
            pstmt.setInt(1, orderId);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Удалено строк из order_body: " + rowsDeleted);
        }
        //Удаляем заголовок заказа
        String deleteOrderHeadSql = "DELETE FROM final_attestation_pakudin.order_head WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteOrderHeadSql)) {
            pstmt.setInt(1, orderId);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Удалено заказов: " + rowsDeleted);
        }
        //Удаляем покупателя (если у него нет других заказов)
        String deleteCustomerSql = "DELETE FROM final_attestation_pakudin.customer WHERE id = ? AND id NOT IN (SELECT customer_id FROM final_attestation_pakudin.order_head)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteCustomerSql)) {
            pstmt.setInt(1, customerId);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Удалено покупателей: " + rowsDeleted);
        }
        //Удаляем товар (если он не используется в заказах)
        String deleteProductSql = "DELETE FROM final_attestation_pakudin.product WHERE id = ? AND id NOT IN (SELECT product_id FROM final_attestation_pakudin.order_body)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteProductSql)) {
            pstmt.setInt(1, productId);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Удалено товаров: " + rowsDeleted);
        }
    }

    private static void executeTestQueries(Properties props) {
        try (Connection conn = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password"))) {
            conn.setAutoCommit(false);
            executeSQLFile(conn, "src/main/resources/db/queries/test-queries.sql");
            System.out.println("Коммит записей?");
            if (askForExec()) {
                conn.commit();
                System.out.println("Коммит выполнен");
            } else {
                conn.rollback();
                System.out.println("Роллбэк выполнен");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    private static void executeSQLFile(Connection conn, String filename) throws SQLException, IOException {
        //Читаем весь файл
        String content = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(filename)));
        //Разделяем на отдельные запросы по точке с запятой
        String[] queries = content.split(";");
        for (int i = 0; i < queries.length; i++) {
            String query = queries[i].trim();
            if (query.isEmpty()) continue;
            System.out.println("\n" + "─".repeat(60));
            System.out.println("Запрос " + (i + 1) + ":");
            System.out.println("─".repeat(60));
            //Показываем первую строку запроса для информации
            String firstLine = query.split("\n")[0].trim();
            System.out.println(firstLine + (query.length() > firstLine.length() ? "..." : ""));
            try (Statement stmt = conn.createStatement()) {
                if (query.trim().toUpperCase().startsWith("SELECT")) {
                    //SELECT запросы - выводим результаты
                    executeAndDisplaySelectQuery(stmt, query);
                } else {
                    //UPDATE/DELETE/INSERT запросы - показываем количество измененных строк
                    int affectedRows = stmt.executeUpdate(query);
                    System.out.println("Выполнено успешно. Затронуто строк: " + affectedRows);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при выполнении запроса " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private static void executeAndDisplaySelectQuery(Statement stmt, String query) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            //Выводим заголовок таблицы
            printTableHeader(metaData, columnCount);
            //Выводим данные
            int rowCount = 0;
            while (rs.next()) {
                printTableRow(rs, metaData, columnCount);
                rowCount++;
            }
            //Выводим итоговую строку
            printTableFooter(columnCount);
            System.out.println("Найдено строк: " + rowCount);
        }
    }



}
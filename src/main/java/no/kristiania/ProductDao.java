package no.kristiania;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ProductDao {

    private final DataSource dataSource;

    public ProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // metode for å  inserte data i db.
    public void insertProduct(Product product) throws SQLException {
        // connection i try
        try (Connection connection = dataSource.getConnection()) {
            // lage sql-spørring
            try (PreparedStatement statement = connection.prepareStatement("insert into products(name, price, in_stock) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                // Specify value of sql statement '?'
                statement.setString(1, product.getName());
                statement.setInt(2, product.getPrice());
                statement.setBoolean(3, product.getInStock());
                // execute
                statement.executeUpdate();
                // må hente ut PK som ble autogeneret
                try (ResultSet rsKeys = statement.getGeneratedKeys()) {
                    rsKeys.next();
                    product.setId(rsKeys.getLong("id"));
                }
            }
        }
    }

    // metoder for å hente ut data:
    ArrayList<Product> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from products")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Product> allProducts = new ArrayList<>();
                    while (rs.next()) {
                        // lager entitet for rad
                        allProducts.add(mapProductFromRs(rs));
                    }
                    return allProducts; // returner alle products lagt til i listen.
                }
            }
        }
    }
    public Product listById(Long id) throws SQLException {
        // connecte til database
        try (Connection connection = dataSource.getConnection()) {
            // skrive sql(spørring, parameter)
            try (PreparedStatement statement = connection.prepareStatement("select * from products where id = ?")) {
                statement.setLong(1,id);
                // execute sql, og lagre resultat
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return mapProductFromRs(resultSet);
                }
            }
        }
    }
    public ArrayList<Product> listByName(String searchTerm) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from products where name like ?")) {
                statement.setString(1, '%'+searchTerm +'%');

                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Product> allProducts = new ArrayList<>();
                    while (rs.next()) {
                        // lager entitet for rad
                        allProducts.add(mapProductFromRs(rs));
                    }
                    return allProducts; // returner alle products lagt til i listen.
                }
            }
        }
    }



    public ArrayList<Product> listByStock(boolean inStock) throws SQLException {
        // connecte til database
        try (Connection connection = dataSource.getConnection()) {
            // skrive sql(spørring, parameter)
            try (PreparedStatement statement = connection.prepareStatement("select * from products where in_stock = ?")) {
                statement.setBoolean(1,inStock);
                // execute sql, og lagre resultat
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Product> resultArray = new ArrayList<>();
                    while(rs.next()) {
                        resultArray.add(mapProductFromRs(rs));
                    }
                    return resultArray;
                }
            }
        }
    }

    public ArrayList<Product> listByMaxPrice(int price) throws SQLException {
        // connecte til database
        try (Connection connection = dataSource.getConnection()) {
            // skrive sql(spørring, parameter)
            try (PreparedStatement statement = connection.prepareStatement("select * from products where price <= ?")) {
                statement.setInt(1,price);
                // execute sql, og lagre resultat
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Product> resultArray = new ArrayList<>();
                    while(rs.next()) {
                        resultArray.add(mapProductFromRs(rs));
                    }
                    return resultArray;
                }
            }
        }
    }



    /* *************                HelpMethods        ****************************/

    private Product mapProductFromRs(ResultSet rs) throws SQLException {
        Product product = new Product();
        // leser verdienene fra raden inn i entiteten.
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getInt("price"));
        product.setInStock(rs.getBoolean("in_stock"));
        return product;
    }

    private static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/product_db");
        dataSource.setUser("product_dbuser");
        dataSource.setPassword("k%3'`(?Qu?");
        // før vi gir fra oss dataSources, så ber vi flyway migrere til siste versjonen av tabellene mine.
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();
        return dataSource;
    }

    public static void main(String[] args) throws SQLException {
        ProductDao dao = new ProductDao(createDataSource() );
        Scanner scanner = new Scanner(System.in);
        // System.out.println(dao.listAll());

        System.out.println("Please enter a name: ");
        String searchTerm = scanner.nextLine().trim();
        System.out.println(dao.listByName(searchTerm));

        System.out.println("Listing products by stock.  Select true or false:  ");
        Boolean inStock = scanner.nextBoolean();
        System.out.println(dao.listByStock(inStock));

        System.out.println("Listing products by max price.  Enter a max price:  ");
        int maxPrice = scanner.nextInt();
        System.out.println(dao.listByMaxPrice(maxPrice));

    }



}

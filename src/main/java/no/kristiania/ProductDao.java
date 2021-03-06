package no.kristiania;

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

    public void insert(Product product) throws SQLException {
        // connection i try
        try (Connection connection = dataSource.getConnection()) {
            // lage sql-spørring
            try (PreparedStatement statement = connection.prepareStatement("insert into products(name, category, in_stock) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                // Specify value of sql statement '?'
                statement.setString(1, product.getName());
                statement.setString(2, product.getCategory());
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

    /* *************                HelpMethods        ****************************/

    private Product mapProductFromRs(ResultSet rs) throws SQLException {
        Product product = new Product();
        // leser verdienene fra raden inn i entiteten.
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setCategory(rs.getString("category"));
        product.setInStock(rs.getBoolean("in_stock"));
        return product;
    }

    private static DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/product_db");
        dataSource.setUser("product_dbuser");
        dataSource.setPassword("k%3'`(?Qu?");
        return dataSource;
    }

    public static void main(String[] args) throws SQLException {
        ProductDao dao = new ProductDao(createDataSource() );

        // System.out.println(dao.listAll());

        /*
        System.out.println("Please enter a name: ");
        Scanner scanner = new Scanner(System.in);
        String searchTerm = scanner.nextLine().trim();
        System.out.println(dao.listByName(searchTerm));
         */
    }



}

package no.kristiania;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class CategoryDao {

    private final DataSource dataSource;

    public CategoryDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertCategory(Category category) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // lage sql-spørring
            try (PreparedStatement statement = connection.prepareStatement("insert into category(category_name) values (?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                // Specify value of sql statement '?'
                statement.setString(1, category.getCategoryName());
                // execute
                statement.executeUpdate();
                // må hente ut PK som ble autogeneret
                try (ResultSet rsKeys = statement.getGeneratedKeys()) {
                    rsKeys.next();
                    category.setId(rsKeys.getLong("id"));
                }
            }
        }
    }

    public ArrayList<Category> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from category")) {
                ResultSet resultSet = statement.executeQuery();
                ArrayList<Category> resultList = new ArrayList<>();
                while(resultSet.next()){
                     Category cat = new Category();
                     cat.setId(resultSet.getLong("id"));
                     cat.setCategoryName(resultSet.getString("category_name"));
                     resultList.add(cat);
                }
                return resultList;
            }
        }


    }


    // må ha insertMetode



    // må ha retrieveAll
}

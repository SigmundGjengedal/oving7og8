package no.kristiania;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDaoTest {
    ProductDao dao = new ProductDao(testDataSource());

    // in memory test with h2
    private DataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:persondb;DB_CLOSE_DELAY=-1")   ;
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    @Test
    void shouldInsertAndRetrieveProducts() throws SQLException {
        Product p1 = sampleProduct();
        Product p2 = sampleProduct();
        dao.insertProduct(p1);
        dao.insertProduct(p2);

        assertThat(dao.listAll())
                .extracting((Product::getId))
                .contains(p1.getId(),p2.getId())
        ;
    }

    @Test
    void shouldRetrieveSingleProduct() throws SQLException {

        Product product = sampleProduct();
        dao.insertProduct(product);
        assertThat(dao.listById(product.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(product)
        ;
    }

    @Test
    void shouldRetrieveProductByName() throws SQLException {
        Product matchingProduct = sampleProduct();
        matchingProduct.setName("Burton");
        Product nonMatchingProduct = sampleProduct();

        dao.insertProduct(matchingProduct);
        dao.insertProduct(nonMatchingProduct);

        assertThat(dao.listByName(matchingProduct.getName()))
                .extracting(Product::getId)
                .contains(matchingProduct.getId())
                .doesNotContain(nonMatchingProduct.getId())
        ;
    }

    @Test
    void shouldRetrieveProductByNameWithWildCard() throws SQLException {
        Product matchingProduct = sampleProduct();
        matchingProduct.setName("Burton");
        Product nonMatchingProduct = sampleProduct();

        dao.insertProduct(matchingProduct);
        dao.insertProduct(nonMatchingProduct);

        assertThat(dao.listByName("Bur"))
                .extracting(Product::getId)
                .contains(matchingProduct.getId())
                .doesNotContain(nonMatchingProduct.getId())
        ;
    }

    // hm
    private Product sampleProduct() {
        Product product = new Product();
        product.setInStock(pickOneBoolean(true,false));
        product.setName(pickOne("Samsung Galaxy", "Macbook Pro","Iphone 12", "Logitech SmartType","Iphone Case Metal", "Dell ThinkFast"));
        product.setPrice(pickOnePrice(789,2499,3999,5499,7999,23999));
        return product;
    }

    private String pickOne(String... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }
    private int pickOnePrice(int... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }

    private boolean pickOneBoolean(boolean... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }

    // før h2
    public static DataSource createTestDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/product_db");
        dataSource.setUser("product_dbuser");
        dataSource.setPassword("k%3'`(?Qu?");
        // før vi gir fra oss dataSources, så ber vi flyway migrere til siste versjonen av tabellene mine.
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }




}

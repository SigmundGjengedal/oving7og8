package no.kristiania;

import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDaoTest {
    ProductDao dao = new ProductDao(createTestDataSource());


    @Test
    void shouldInsertAndRetrieveProducts() throws SQLException {
        Product p1 = sampleProduct();
        Product p2 = sampleProduct();
        dao.insert(p1);
        dao.insert(p2);

        assertThat(dao.listAll())
                .extracting((Product::getId))
                .contains(p1.getId(),p2.getId())
        ;
    }

    @Test
    void shouldRetrieveSingleProduct() throws SQLException {

        Product product = sampleProduct();
        dao.insert(product);
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

        dao.insert(matchingProduct);
        dao.insert(nonMatchingProduct);

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

        dao.insert(matchingProduct);
        dao.insert(nonMatchingProduct);

        assertThat(dao.listByName("Bur"))
                .extracting(Product::getId)
                .contains(matchingProduct.getId())
                .doesNotContain(nonMatchingProduct.getId())
        ;
    }

    // hm
    private Product sampleProduct() {
        Product product = new Product();
        product.setInStock(true);
        product.setName(pickOne("Samsung Galaxy", "Macbook Pro","Iphone 12", "Logitech SmartType"));
        product.setCategory(pickOne("Smart Phone","Computer","keyboard"));
        return product;
    }

    private String pickOne(String... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }
    private DataSource createTestDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/product_db");
        dataSource.setUser("product_dbuser");
        dataSource.setPassword("k%3'`(?Qu?");

        return dataSource;
    }


}

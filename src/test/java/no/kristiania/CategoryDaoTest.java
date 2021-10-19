package no.kristiania;

import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.Random;

import static no.kristiania.ProductDaoTest.createTestDataSource;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryDaoTest {
    private CategoryDao dao = new CategoryDao(createTestDataSource());


    @Test
    void shouldListInsertedCategory() throws SQLException {
          Category category1 = sampleCategory();
          Category category2 = sampleCategory();
          dao.insertCategory(category1);
          dao.insertCategory(category2);

          assertThat(dao.listAll())
                  .extracting((Category::getId))
                  .contains(category1.getId(),category2.getId())
                  ;
    }

    // hm
    private Category sampleCategory() {
            Category category = new Category();
            category.setCategoryName(pickOne("Smart Phone", "Computer","Extras"));
            return category;
    }

    private String pickOne(String... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }


}


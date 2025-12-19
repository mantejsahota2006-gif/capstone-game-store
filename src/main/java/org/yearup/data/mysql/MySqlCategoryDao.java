package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();

        try (Connection connection = getConnection()) {
            PreparedStatement categoriesStatement = connection.prepareStatement("""
                    SELECT *
                    FROM categories
                    """);

            ResultSet resultSet = categoriesStatement.executeQuery();
            while (resultSet.next()) {

                Category category = mapRow(resultSet);
                categoryList.add(category);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categoryList;
    }

    @Override
    public Category getById(int categoryId) {
        try (Connection connection = getConnection()) {
            PreparedStatement StatementId = connection.prepareStatement("""
                    SELECT *
                    FROM categories WHERE category_id = ?
                    """);
            StatementId.setInt(1, categoryId);
            ResultSet resultSetId = StatementId.executeQuery();
            if (resultSetId.next()) {
                return mapRow(resultSetId);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        // create a new category

        try (Connection connection = getConnection()) {
            PreparedStatement createStatement = connection.prepareStatement("""
                    
                    INSERT INTO categories (name, description) VALUES (?, ?)
                    """, PreparedStatement.RETURN_GENERATED_KEYS);

            createStatement.setString(1, category.getName());
            createStatement.setString(2, category.getDescription());

            int rowAffected = createStatement.executeUpdate();
            if (rowAffected > 0) {
                ResultSet generatedKeys = createStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int categoryID = generatedKeys.getInt(1);
                    category.setCategoryId(categoryID);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return category;
    }

    @Override
    public void update(int categoryId, Category category) {
        try (Connection connection = getConnection()) {
            PreparedStatement updateStatement = connection.prepareStatement("""
                    
                    UPDATE categories SET name = ?, description = ?
                    WHERE category_id = ?
                    """);
            updateStatement.setString(1, category.getName());
            updateStatement.setString(2, category.getDescription());
            updateStatement.setInt(3, categoryId);

            int rows = updateStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Update failed, no rows affected!");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(int categoryId) {
        // delete category
        try (Connection connection = getConnection()) {
            PreparedStatement deleteStatement = connection.prepareStatement("""
                    
                    DELETE FROM categories
                    WHERE category_id = ?
                    """);
            deleteStatement.setInt(1, categoryId);
            int rows = deleteStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Delete failed, no rows affected!");

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}

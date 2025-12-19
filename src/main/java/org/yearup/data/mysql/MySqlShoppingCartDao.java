package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    private ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT * FROM shopping_cart
                     WHERE user_id = ?""")) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSetId = preparedStatement.executeQuery()) {
                while (resultSetId.next()) {
                    ShoppingCartItem item = mapRow(resultSetId);
                    shoppingCart.add(item);

                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }


    @Override
    public void addingItems(int userId, int productId) {
        ShoppingCart existsItem = getByUserId(userId);

        if (existsItem.contains(productId)) {
            ShoppingCartItem items = existsItem.get(productId);
            int addQuantity = items.getQuantity() + 1;
            Update(userId, productId, addQuantity);
        } else {
            try {
                Connection connection = getConnection();
                PreparedStatement addingItemStatement = connection.prepareStatement(""" 
                        INSERT INTO shopping_cart (user_id, product_id) VALUES (?, ?)
                        """);
                addingItemStatement.setInt(1, userId);
                addingItemStatement.setInt(2, productId);
                addingItemStatement.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void Update(int userId, int productId, int quantity) {
        try {
            Connection connection = getConnection();
            PreparedStatement updateStatement = connection.prepareStatement("""
                  UPDATE shopping_cart SET quantity = ?
                  WHERE user_id = ? AND product_id = ?
                  """);
            updateStatement.setInt(1,quantity);
            updateStatement.setInt(2,userId);
            updateStatement.setInt(3,productId);
            updateStatement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(int userId) {
        try {
            Connection connection = getConnection();
            PreparedStatement deleStatement = connection.prepareStatement("""
                   
                   DELETE FROM shopping_cart WHERE user_id = ?""");
            deleStatement.setInt(1,userId);
            deleStatement.executeUpdate();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ShoppingCartItem mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        int quantity = row.getInt("quantity");

        ShoppingCartItem cartItem = new ShoppingCartItem();
        Product newProduct = productDao.getById(productId);
        cartItem.setProduct(newProduct);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}
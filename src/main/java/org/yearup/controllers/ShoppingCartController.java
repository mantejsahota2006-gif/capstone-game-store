package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("cart")
@PreAuthorize("hasAnyRole('ROLE_USER')")

public class ShoppingCartController {
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;


    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            return shoppingCartDao.getByUserId(userId);

        } catch (ResponseStatusException e) {
            throw (e);
        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... server error");
        }
    }

    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal) {
        try {

            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            shoppingCartDao.addingItems(userId, productId);
            return shoppingCartDao.getByUserId(userId);

        } catch (ResponseStatusException e) {
            throw (e);
        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... server error.");

        }
    }

    @PutMapping("/products/{productId}")
    public ShoppingCart Update(@PathVariable int productId, @RequestBody ShoppingCartItem cartItem, Principal principal) {
        try {


            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            shoppingCartDao.Update(userId, productId, cartItem.getQuantity());
            return shoppingCartDao.getByUserId(userId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... server error.");
        }

    }

    @DeleteMapping
    public ShoppingCart delete(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            shoppingCartDao.delete(userId);
            return shoppingCartDao.getByUserId(userId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... server error.");
        }

    }
}

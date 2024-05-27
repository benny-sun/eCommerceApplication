package com.example.demo.controllers;

import com.example.demo.EcommerceApplication;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = EcommerceApplication.class)
public class CartControllerTest {

    @Autowired
    private CartController cartController;

    @Autowired
    private UserController userController;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DirtiesContext
    public void testCartOperation() {
        // arrange
        User user = createUser();
        ModifyCartRequest cartRequest = createCartRequest(user);
        int quantity5 = 5;
        cartRequest.setQuantity(quantity5);

        // test addToCart
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNotNull(response.getBody());
        assertEquals(
                cartRequest.getQuantity(),
                response.getBody().getItems().stream()
                    .filter(item -> item.getId().equals(cartRequest.getItemId()))
                    .count()
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // test removeFromCart
        int quantity1 = 1;
        cartRequest.setQuantity(quantity1); // remove one item
        response = cartController.removeFromcart(cartRequest);
        assertNotNull(response.getBody());
        assertEquals(
                quantity5 - quantity1,
                response.getBody().getItems().stream()
                        .filter(item -> item.getId().equals(cartRequest.getItemId()))
                        .count()
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void testTargetNotFound() {
        // arrange
        User user = createUser();
        ModifyCartRequest cartRequest = createCartRequest(user);
        cartRequest.setUsername("nobody");

        // test user not found
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // test item not found
        cartRequest = createCartRequest(user);
        cartRequest.setItemId(999);
        response = cartController.addTocart(cartRequest);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private User createUser() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Benny Test");
        userRequest.setPassword("abcd1234");
        userRequest.setConfirmPassword("abcd1234");
        when(bCryptPasswordEncoder.encode(userRequest.getPassword())).thenReturn("hashedPassword");
        return userController.createUser(userRequest).getBody();
    }

    private ModifyCartRequest createCartRequest(User user) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(1);
        cartRequest.setQuantity(4);
        return cartRequest;
    }
}

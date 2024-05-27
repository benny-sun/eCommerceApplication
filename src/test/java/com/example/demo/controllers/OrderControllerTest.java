package com.example.demo.controllers;

import com.example.demo.EcommerceApplication;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = EcommerceApplication.class)
public class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    @Autowired
    private CartController cartController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testOrder() {
        /* test order submit */
        // order not found
        ResponseEntity<UserOrder> submitResponse = orderController.submit("nobody");
        assertNull(submitResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND, submitResponse.getStatusCode());

        // arrange
        User user = createUser();
        Long itemId = 1L;
        int quantity = 4;
        runAddToCart(user, itemId, quantity);
        Item item = itemController.getItemById(itemId).getBody();
        refreshPersistence();

        // actual
        submitResponse = orderController.submit(user.getUsername());

        //assert
        assertNotNull(submitResponse.getBody());
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(quantity)), submitResponse.getBody().getTotal());
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        /* test get order */
        // order not found
        ResponseEntity<List<UserOrder>> queryResponse = orderController.getOrdersForUser("nobody");
        assertNull(queryResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND, queryResponse.getStatusCode());

        // actual
        queryResponse = orderController.getOrdersForUser(user.getUsername());

        // assert
        assertNotNull(queryResponse.getBody());
        assertThat(queryResponse.getBody().get(0), samePropertyValuesAs(submitResponse.getBody()));
    }

    private User createUser() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Benny Test");
        userRequest.setPassword("abcd1234");
        userRequest.setConfirmPassword("abcd1234");
        when(bCryptPasswordEncoder.encode(userRequest.getPassword())).thenReturn("hashedPassword");
        return userController.createUser(userRequest).getBody();
    }

    private void runAddToCart(User user, Long itemId, int quantity) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(itemId);
        cartRequest.setQuantity(quantity);
        cartController.addTocart(cartRequest);
    }

    private void refreshPersistence() {
        entityManager.flush();
        entityManager.clear();
    }
}

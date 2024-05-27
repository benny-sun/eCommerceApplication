package com.example.demo.controllers;

import com.example.demo.EcommerceApplication;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EcommerceApplication.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DirtiesContext
    public void testCreateUserSuccess() {
        // arrange
        CreateUserRequest userRequest = createUserRequest();
        when(bCryptPasswordEncoder.encode(userRequest.getPassword())).thenReturn("hashedPassword");

        // actual
        ResponseEntity<User> response = userController.createUser(userRequest);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(userRequest.getUsername(), user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    @DirtiesContext
    public void testCreateUserPasswordMismatch() {
        CreateUserRequest userRequest = createUserRequest();
        userRequest.setConfirmPassword("4321dcba");

        ResponseEntity<User> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DirtiesContext
    public void testCreateUserPasswordTooShort() {
        CreateUserRequest userRequest = createUserRequest();
        userRequest.setPassword("abc");
        userRequest.setConfirmPassword("abc");

        ResponseEntity<User> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DirtiesContext
    public void testFindUser() {
        // arrange
        CreateUserRequest userRequest = createUserRequest();
        when(bCryptPasswordEncoder.encode(userRequest.getPassword())).thenReturn("hashedPassword");
        User user = userController.createUser(userRequest).getBody();

        // test findById
        ResponseEntity<User> response = userController.findById(user.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getUsername(), response.getBody().getUsername());

        // test findByUserName
        response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getId(), response.getBody().getId());
    }

    private static CreateUserRequest createUserRequest() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Benny Test");
        userRequest.setPassword("abcd1234");
        userRequest.setConfirmPassword("abcd1234");
        return userRequest;
    }
}

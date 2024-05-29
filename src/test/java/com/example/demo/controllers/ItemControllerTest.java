package com.example.demo.controllers;

import com.example.demo.EcommerceApplication;
import com.example.demo.model.persistence.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * All initial test data in the file src/main/resources/data.sql
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EcommerceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @Test
    public void testGetItemById() {
        // test getItemById
        Long itemId = 1L;
        ResponseEntity<Item> response = itemController.getItemById(itemId);
        assertNotNull(response.getBody());
        assertEquals(itemId, response.getBody().getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetItemByItemName() {
        // test item name exists
        String itemName = "Round Widget";
        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        assertNotNull(response);
        List<Item> items = response.getBody();
        assertFalse(items.isEmpty());
        assertEquals(itemName, items.get(0).getName());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // test item name not exists
        itemName = "fake item name";
        response = itemController.getItemsByName(itemName);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetItemList() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().stream().count());
    }
}

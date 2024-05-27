package com.example.demo;

import com.example.demo.controllers.CartControllerTest;
import com.example.demo.controllers.ItemControllerTest;
import com.example.demo.controllers.OrderControllerTest;
import com.example.demo.controllers.UserControllerTest;
import com.example.demo.security.JwtAuthTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		JwtAuthTest.class,
		UserControllerTest.class,
		CartControllerTest.class,
		ItemControllerTest.class,
		OrderControllerTest.class
})
public class EcommerceApplicationTests {
}

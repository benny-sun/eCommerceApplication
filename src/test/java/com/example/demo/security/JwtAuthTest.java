package com.example.demo.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.demo.EcommerceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EcommerceApplication.class)
@AutoConfigureMockMvc
public class JwtAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAccessWithValidToken() throws Exception {
        signup();
        String token = loginAndFetchToken();
        mockMvc.perform(get("/api/user/id/1")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test(expected = JWTDecodeException.class)
    public void testAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/user/id/1")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAccessPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/item"))
                .andExpect(status().isOk());
    }

    private void signup() throws Exception {
        String payload = "{\"username\":\"benny\",\"password\":\"123123123\",\"confirmPassword\":\"123123123\"}";
        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }

    private String loginAndFetchToken() throws Exception {
        String payload = "{\"username\":\"benny\",\"password\":\"123123123\"}";
        ResultActions loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
        return loginResult.andReturn().getResponse().getHeader("Authorization");
    }
}

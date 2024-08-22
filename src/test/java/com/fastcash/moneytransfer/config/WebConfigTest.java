package com.fastcash.moneytransfer.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;

@WebMvcTest
@ContextConfiguration(classes = {WebConfig.class, WebConfigTest.AnnotatedController.class, WebConfigTest.NonAnnotatedController.class})
@TestPropertySource(properties = {"api.base.url=/api"})
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {})
    void testAnnotatedControllerPathPrefix() throws Exception {
        mockMvc.perform(get("/api/annotated"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void testNonAnnotatedControllerPathPrefix() throws Exception {
        mockMvc.perform(get("/non-annotated"))
                .andExpect(status().isOk());
    }

    @ApiBaseUrlPrefix
    @RestController
    @RequestMapping("/annotated")
    static class AnnotatedController {
        @GetMapping
        public String get() {
            return "Annotated";
        }
    }

    @RestController
    @RequestMapping("/non-annotated")
    static class NonAnnotatedController {
        @GetMapping
        public String get() {
            return "Non-Annotated";
        }
    }
    
}

package org.example.unittest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorizationEndPointTest {
    @Autowired
    private MockMvc mockMvc;

    // Первые 2 теста - тесты с WeatherApi на получение погоды с внешнего апи и сохранением в бд
    // Оставшиеся 2 теста - тесты с Weather на сохранение и полуение данных юез бд
    @Test
    @WithMockUser(roles = "ADMIN")
    public void saveCurrentTemperature_notRegion_jdbc_authorize() throws Exception {
        this.mockMvc.perform(get("/external/jdbc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Parameter q is missing.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/v1/current.json")));
    }

    @Test
    @WithMockUser(roles = {"USER", "ANONYMOUS"})
    public void saveCurrentTemperature_notRegion_jdbc_NotAuthorize() throws Exception {
        this.mockMvc.perform(get("/external/jdbc"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("authentication_exception")));
    }


    // Тесты без бд

    @Test
    @WithMockUser(roles = {"USER", "ANONYMOUS", "ADMIN"})
    public void externalCurrentTemperature() throws Exception {
        mockMvc.perform(get("/api/wheather/external")
                        .param("q", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void add_test_authorize() throws Exception {
        mockMvc.perform(post("/api/wheather/{regionName}", "Minsk")
                        .param("dateTime", "01/01/2020")
                        .param("temperature", "11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.regionName", is("Minsk")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature", is(11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", is("2020-01-01")));
    }

    @Test
    @WithMockUser(roles = {"USER", "ANONYMOUS"})
    public void add_test_NotAuthorize() throws Exception {
        mockMvc.perform(post("/api/wheather/{regionName}", "Minsk")
                        .param("dateTime", "01/01/2020")
                        .param("temperature", "11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("authentication_exception")));
    }
}

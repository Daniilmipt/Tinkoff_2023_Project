package org.example.unittest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public final class WeatherTest {
    @Autowired
    private MockMvc mockMvc;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    public void externalCurrentTemperature() throws Exception {
        mockMvc.perform(get("/api/wheather/external")
                        .param("q", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    public void externalCurrentTemperature_incorrectRegion() throws Exception {
        mockMvc.perform(get("/api/wheather/external")
                        .param("q", "l")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("No matching location found.")))
                .andExpect(jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.path", is("/v1/current.json")));
    }

    @Test
    public void externalCurrentTemperature_notRegion() throws Exception {
        mockMvc.perform(get("/api/wheather/external"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Parameter q is missing.")))
                .andExpect(jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.path", is("/v1/current.json")));
    }

    @Test
    public void get_test() throws Exception {
        mockMvc.perform(get("/api/wheather/{regionName}", "Chimki")
                        .param("dateTime", "01/01/2014")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is(-2)));
    }

    @Test
    public void get_test_notRegion() throws Exception {
        mockMvc.perform(get("/api/wheather/{regionName}", "M")
                        .param("dateTime", "01/01/2014")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void get_test_incorrectDate() throws Exception {
        mockMvc.perform(get("/api/wheather/{regionName}", "Moscow")
                        .param("dateTime", "0114")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Incorrect dateTime format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Moscow")));
    }

    @Test
    public void get_test_notData() throws Exception {
        mockMvc.perform(get("/api/wheather/{regionName}", "Moscow"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("DateTime can not be null")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Moscow")));
    }

    @Test
    public void add_test() throws Exception {
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
    public void add_test_incorrectTemperature() throws Exception {
        mockMvc.perform(post("/api/wheather/{regionName}", "Minsk")
                        .param("dateTime", "01/01/2020")
                        .param("temperature", "nothing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Temperature must be integer")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Minsk")));
    }

    @Test
    public void add_test_incorrectDate() throws Exception {
        mockMvc.perform(post("/api/wheather/{regionName}", "Minsk")
                        .param("dateTime", "01/01/20")
                        .param("temperature", "14")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Incorrect dateTime format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Minsk")));
    }

    @Test
    public void add_test_noDate() throws Exception {
        mockMvc.perform(post("/api/wheather/{regionName}", "Minsk")
                        .param("temperature", "nothing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("DateTime can not be null")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Minsk")));
    }

    @Test
    public void add_test_noTemperature() throws Exception {
        mockMvc.perform(post("/api/wheather/{regionName}", "Minsk")
                        .param("dateTime", "01/01/2020")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Temperature can not be null")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Minsk")));
    }


    @Test
    public void put_test_notExist() throws Exception {
        mockMvc.perform(put("/api/wheather/{regionName}", "Minsk")
                        .param("dateTime", "01/01/2020")
                        .param("temperature", "11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    public void put_test_exist() throws Exception {
        mockMvc.perform(put("/api/wheather/{regionName}", "Moscow")
                        .param("dateTime", "01/01/2014")
                        .param("temperature", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    public void put_test_incorrectData() throws Exception {
        mockMvc.perform(put("/api/wheather/{regionName}", "Moscow")
                        .param("dateTime", "01/01/204")
                        .param("temperature", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Incorrect dateTime format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Moscow")));
    }

    @Test
    public void put_test_incorrectTemperature() throws Exception {
        mockMvc.perform(put("/api/wheather/{regionName}", "Moscow")
                        .param("dateTime", "01/01/2014")
                        .param("temperature", "nothing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Temperature must be integer")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Moscow")));
    }

    @Test
    public void put_test_notData() throws Exception {
        mockMvc.perform(put("/api/wheather/{regionName}", "Moscow")
                        .param("temperature", "14")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("DateTime can not be null")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Moscow")));
    }

    @Test
    public void put_test_notTemperature() throws Exception {
        mockMvc.perform(put("/api/wheather/{regionName}", "Moscow")
                        .param("dateTime", "01/01/2014")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Temperature can not be null")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/api/wheather/Moscow")));
    }


    @Test
    public void delete_test_exist() throws Exception {
        mockMvc.perform(delete("/api/wheather/{regionName}", "Moscow"))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_test_noExist() throws Exception {
        mockMvc.perform(delete("/api/wheather/{regionName}", "Minsk"))
                .andExpect(status().isOk());
    }

}

package org.example.container;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class WeatherTest {
    @Autowired
    private MockMvc mockMvc;

    @Container
    public static GenericContainer h2Container = new GenericContainer(DockerImageName.parse("oscarfonts/h2"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:tcp://localhost:" + h2Container.getMappedPort(1521) + "/test");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    public void saveCurrentTemperature_Jdbc() throws Exception {
        System.out.println("----------------");
        System.out.println(h2Container.getMappedPort(81));
        System.out.println("----------------");
        this.mockMvc.perform(get("/external/jdbc")
                        .param("q", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.region_id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.type_id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").isString());
    }

    @Test
    public void saveCurrentTemperature_incorrectRegion_jdbc() throws Exception {
        this.mockMvc.perform(get("/external/jdbc")
                        .param("q", "q")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("No matching location found.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/v1/current.json")));
    }

    @Test
    public void saveCurrentTemperature_notRegion_jdbc() throws Exception {
        this.mockMvc.perform(get("/external/jdbc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Parameter q is missing.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/v1/current.json")));
    }

    @Test
    public void saveCurrentTemperature_hiber() throws Exception {
        this.mockMvc.perform(get("/external/hibernate")
                        .param("q", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.region_id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.type_id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").isString());
    }

    @Test
    public void saveCurrentTemperature_incorrectRegion_hiber() throws Exception {
        this.mockMvc.perform(get("/external/hibernate")
                        .param("q", "q")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("No matching location found.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/v1/current.json")));
    }

    @Test
    public void saveCurrentTemperature_noRegion_hiber() throws Exception {
        this.mockMvc.perform(get("/external/hibernate"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", is("Parameter q is missing.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", is(LocalDate.now().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", is("/v1/current.json")));
    }
}

package com.nk0shey.springboot1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:engineers;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class SpringBoot1ApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired SoftwareEngineerRepository repository;
    @Autowired ObjectMapper mapper;
    private static final String API = "/api/v1/software-engineers";
    @BeforeEach void clear() { repository.deleteAll(); }

    @Test void emptyList() throws Exception {
        mvc.perform(get(API)).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
    @Test void createAndRead() throws Exception {
        String body = mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Demo\",\"techStack\":\"Java\"}"))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andReturn().getResponse().getContentAsString();
        int id = mapper.readTree(body).get("id").asInt();
        mvc.perform(get(API + "/" + id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Demo"));
    }
    @Test void missingIs404() throws Exception {
        mvc.perform(get(API + "/999999")).andExpect(status().isNotFound());
    }
    @Test void invalidIdIs400() throws Exception {
        mvc.perform(get(API + "/0")).andExpect(status().isBadRequest());
    }
    @Test void blankNameIs400() throws Exception {
        mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\" \",\"techStack\":\"Java\"}"))
            .andExpect(status().isBadRequest());
    }
    @Test void suppliedIdCannotOverwrite() throws Exception {
        mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\":1,\"name\":\"Demo\",\"techStack\":\"Java\"}"))
            .andExpect(status().isBadRequest());
    }
    @Test void malformedJsonIs400() throws Exception {
        mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content("not json"))
            .andExpect(status().isBadRequest());
    }
    @Test void stackWithinDocumentedLimitPersists() throws Exception {
        String body = mapper.writeValueAsString(new SoftwareEngineer(0, "Demo", "x".repeat(500)));
        mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.techStack").value("x".repeat(500)));
    }
    @Test void stackBeyondLimitIs400() throws Exception {
        String body = mapper.writeValueAsString(new SoftwareEngineer(0, "Demo", "x".repeat(501)));
        mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }
}

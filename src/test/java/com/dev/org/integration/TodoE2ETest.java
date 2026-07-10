package com.dev.org.integration;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Full E2E BDD Test with MockRestServiceServer (Option 2).
 * This tests the Controller -> Service -> Client flow, but mocks the external HTTP response.
 */
@Tag("e2e")
@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=none"
        })
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class TodoE2ETest {

    @Autowired private MockMvc mockMvc;

    @Autowired private MockRestServiceServer mockServer;

    @Test
    @DisplayName(
            "Given external API returns todos, When requesting GET /todos, Then return 200 OK with"
                    + " formatted JSON")
    void givenExternalApiReturnsTodosWhenGetAllTodosThenReturnFormattedTodos() throws Exception {
        // Given
        // This is exactly what the external JSONPlaceholder API would return
        String externalApiResponseJson =
                """
                [
                  {
                    "userId": 1,
                    "id": 1,
                    "title": "delectus aut autem",
                    "completed": false
                  },
                  {
                    "userId": 1,
                    "id": 2,
                    "title": "quis ut nam facilis et officia qui",
                    "completed": true
                  }
                ]
                """;

        // Intercept the outgoing RestClient call and provide the mock JSON response
        mockServer
                .expect(requestTo("https://jsonplaceholder.typicode.com/todos"))
                .andRespond(withSuccess(externalApiResponseJson, MediaType.APPLICATION_JSON));

        // When & Then
        // We perform the request against our controller just like a real client would
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("delectus aut autem"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("quis ut nam facilis et officia qui"))
                .andExpect(jsonPath("$[1].completed").value(true));

        // Verify that the external API was actually called behind the scenes
        mockServer.verify();
    }
}

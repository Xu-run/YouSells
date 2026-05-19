package com.yousells.modules.dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Clock clock;

    @Test
    void shouldReturnDashboardOverviewAfterLogin() throws Exception {
        org.mockito.Mockito.when(clock.instant()).thenReturn(Instant.parse("2026-05-19T04:00:00Z"));
        org.mockito.Mockito.when(clock.getZone()).thenReturn(ZoneId.of("Asia/Hong_Kong"));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", not(emptyOrNullString())))
                .andReturn();

        String accessToken = com.yousells.modules.auth.JsonTestUtils.readJsonPath(
                loginResult.getResponse().getContentAsString(),
                "$.data.accessToken"
        );

        mockMvc.perform(get("/api/dashboard/overview")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.todayPendingFollowCount").value(1))
                .andExpect(jsonPath("$.data.overdueCustomerCount").value(0))
                .andExpect(jsonPath("$.data.recentNewCustomerCount").value(3))
                .andExpect(jsonPath("$.data.highIntentCustomerCount").value(1))
                .andExpect(jsonPath("$.data.todayTasks.length()").value(2));
    }
}

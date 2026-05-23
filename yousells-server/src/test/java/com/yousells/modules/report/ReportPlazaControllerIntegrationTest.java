package com.yousells.modules.report;

import com.yousells.modules.auth.JsonTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReportPlazaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String memberToken;
    private Long adminDailyReportId;
    private Long adminWeeklyReportId;
    private Long memberDailyReportId;

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @BeforeAll
    void loginBothUsers() throws Exception {
        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        adminToken = JsonTestUtils.readJsonPath(
                adminResult.getResponse().getContentAsString(), "$.data.accessToken");

        MvcResult memberResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"member\",\"password\":\"member123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();
        memberToken = JsonTestUtils.readJsonPath(
                memberResult.getResponse().getContentAsString(), "$.data.accessToken");
    }

    @Test
    @Order(10)
    void shouldCreateDailyReportForPlaza() throws Exception {
        String today = LocalDate.now().toString();
        MvcResult result = mockMvc.perform(post("/api/reports/daily")
                        .header("Authorization", bearer(adminToken))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "reportDate": "%s",
                                  "summary": "admin日报内容",
                                  "issues": "无",
                                  "tomorrowPlan": "继续推进"
                                }
                                """.formatted(today)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();
        adminDailyReportId = JsonTestUtils.readJsonPathAsLong(
                result.getResponse().getContentAsString(), "$.data.id");
    }

    @Test
    @Order(11)
    void shouldCreateWeeklyReportForPlaza() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/reports/weekly")
                        .header("Authorization", bearer(adminToken))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "weekKey": "2026-W21",
                                  "summary": "admin周报内容",
                                  "issues": "无",
                                  "nextWeekPlan": "下周计划"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();
        adminWeeklyReportId = JsonTestUtils.readJsonPathAsLong(
                result.getResponse().getContentAsString(), "$.data.id");
    }

    @Test
    @Order(12)
    void shouldCreateAnotherDailyReportAsMember() throws Exception {
        String today = LocalDate.now().toString();
        MvcResult result = mockMvc.perform(post("/api/reports/daily")
                        .header("Authorization", bearer(memberToken))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "reportDate": "%s",
                                  "summary": "member日报内容",
                                  "issues": "无",
                                  "tomorrowPlan": "继续学习"
                                }
                                """.formatted(today)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();
        memberDailyReportId = JsonTestUtils.readJsonPathAsLong(
                result.getResponse().getContentAsString(), "$.data.id");
    }

    @Test
    @Order(20)
    void shouldPageDailyPlazaWithoutFilter() throws Exception {
        mockMvc.perform(get("/api/reports/plaza")
                        .header("Authorization", bearer(adminToken))
                        .param("type", "daily")
                        .param("page", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].userRealName").isString())
                .andExpect(jsonPath("$.data.records[0].likeCount").isNumber())
                .andExpect(jsonPath("$.data.records[0].commentCount").isNumber())
                .andExpect(jsonPath("$.data.records[0].likedByMe").isBoolean());
    }

    @Test
    @Order(21)
    void shouldPageDailyPlazaWithUserFilter() throws Exception {
        // member user id in H2 seed is 2
        mockMvc.perform(get("/api/reports/plaza")
                        .header("Authorization", bearer(adminToken))
                        .param("type", "daily")
                        .param("userId", "2")
                        .param("page", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].summary").value("member日报内容"));
    }

    @Test
    @Order(22)
    void shouldPageWeeklyPlaza() throws Exception {
        mockMvc.perform(get("/api/reports/plaza")
                        .header("Authorization", bearer(adminToken))
                        .param("type", "weekly")
                        .param("page", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.records[0].type").value("weekly"));
    }

    @Test
    @Order(30)
    void shouldToggleLikeDaily() throws Exception {
        mockMvc.perform(post("/api/reports/plaza/daily/{reportId}/like", adminDailyReportId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.liked").value(true));
    }

    @Test
    @Order(31)
    void shouldToggleUnlikeDaily() throws Exception {
        mockMvc.perform(post("/api/reports/plaza/daily/{reportId}/like", adminDailyReportId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.liked").value(false));
    }

    @Test
    @Order(32)
    void shouldToggleLikeWeekly() throws Exception {
        mockMvc.perform(post("/api/reports/plaza/weekly/{reportId}/like", adminWeeklyReportId)
                        .header("Authorization", bearer(memberToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.liked").value(true));
    }

    @Test
    @Order(40)
    void shouldCreateCommentOnDaily() throws Exception {
        mockMvc.perform(post("/api/reports/plaza/daily/{reportId}/comments", adminDailyReportId)
                        .header("Authorization", bearer(memberToken))
                        .contentType(APPLICATION_JSON)
                        .content("{\"content\":\"这篇日报写得很详细\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @Order(41)
    void shouldPageCommentsOnDaily() throws Exception {
        mockMvc.perform(get("/api/reports/plaza/daily/{reportId}/comments", adminDailyReportId)
                        .header("Authorization", bearer(adminToken))
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records", hasSize(1)))
                .andExpect(jsonPath("$.data.records[0].content").value("这篇日报写得很详细"))
                .andExpect(jsonPath("$.data.records[0].userId").isNumber());
    }

    @Test
    @Order(42)
    void shouldSeeLikeAndCommentCountsInPlaza() throws Exception {
        // member liked weekly report; member commented on admin daily report
        // So from admin's view: daily should have commentCount=1, likeCount=0, likedByMe=0
        // From member's view: weekly should have likeCount=1, likedByMe=1
        mockMvc.perform(get("/api/reports/plaza")
                        .header("Authorization", bearer(adminToken))
                        .param("type", "daily")
                        .param("page", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[?(@.id == %d)].commentCount".formatted(adminDailyReportId)).value(1))
                .andExpect(jsonPath("$.data.records[?(@.id == %d)].likeCount".formatted(adminDailyReportId)).value(0))
                .andExpect(jsonPath("$.data.records[?(@.id == %d)].likedByMe".formatted(adminDailyReportId)).value(false));

        mockMvc.perform(get("/api/reports/plaza")
                        .header("Authorization", bearer(memberToken))
                        .param("type", "weekly")
                        .param("page", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[?(@.id == %d)].likeCount".formatted(adminWeeklyReportId)).value(1))
                .andExpect(jsonPath("$.data.records[?(@.id == %d)].likedByMe".formatted(adminWeeklyReportId)).value(true));
    }
}

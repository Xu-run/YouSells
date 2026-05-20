package com.yousells.modules;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class P0FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private Long customerId;
    private Long taskId;
    private Long scriptId;

    private String bearer() {
        return "Bearer " + accessToken;
    }

    @BeforeAll
    void login() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username": "admin", "password": "admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        accessToken = JsonTestUtils.readJsonPath(
                result.getResponse().getContentAsString(), "$.data.accessToken");
    }

    // ═══════════ AUTH ═══════════

    @Test @Order(1)
    void shouldReadCurrentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test @Order(2)
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4003));
    }

    // ═══════════ DASHBOARD ═══════════

    @Test @Order(3)
    void shouldReturnEmptyDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.todayTasks").isArray());
    }

    // ═══════════ CUSTOMER ═══════════

    @Test @Order(10)
    void shouldCreateCustomer() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/customers")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "测试客户A",
                                  "contactValue": "微信: testA",
                                  "sourcePlatform": "微信",
                                  "customerType": "OLD_GRADE1",
                                  "intentLevel": "A",
                                  "currentStage": "FIRST_COMMUNICATION",
                                  "ownerUserId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        customerId = JsonTestUtils.readJsonPathAsLong(
                result.getResponse().getContentAsString(), "$.data.id");
    }

    @Test @Order(11)
    void shouldGetCustomerDetail() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", customerId).header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("测试客户A"))
                .andExpect(jsonPath("$.data.customerCode", not(emptyOrNullString())));
    }

    @Test @Order(12)
    void shouldReturn404ForNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", 99999L).header("Authorization", bearer()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4004));
    }

    @Test @Order(13)
    void shouldUpdateCustomer() throws Exception {
        mockMvc.perform(put("/api/customers/{id}", customerId)
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "测试客户A改",
                                  "contactValue": "QQ: 99999",
                                  "sourcePlatform": "QQ",
                                  "intentLevel": "B",
                                  "currentStage": "NURTURING",
                                  "ownerUserId": 1
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/customers/{id}", customerId).header("Authorization", bearer()))
                .andExpect(jsonPath("$.data.nickname").value("测试客户A改"))
                .andExpect(jsonPath("$.data.currentStage").value("NURTURING"));
    }

    @Test @Order(14)
    void shouldListCustomersWithFilters() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .header("Authorization", bearer())
                        .param("intentLevel", "B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test @Order(15)
    void shouldUpdateAndClearTags() throws Exception {
        // Set tags (seeded tag id=1 exists in H2 via test-schema seeding)
        mockMvc.perform(put("/api/customers/{id}/tags", customerId)
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("{\"tagIds\": [1]}"))
                .andExpect(status().isOk());

        // Clear tags
        mockMvc.perform(put("/api/customers/{id}/tags", customerId)
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("{\"tagIds\": []}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/customers/{id}", customerId).header("Authorization", bearer()))
                .andExpect(jsonPath("$.data.tags", hasSize(0)));
    }

    @Test @Order(16)
    void shouldUpdateNextFollow() throws Exception {
        mockMvc.perform(put("/api/customers/{id}/next-follow", customerId)
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"nextAction": "电话回访", "nextFollowAt": "2026-06-15T14:00:00"}
                                """))
                .andExpect(status().isOk());
    }

    // ═══════════ FOLLOW-UP ═══════════

    @Test @Order(20)
    void shouldCreateFollowUpAndSyncCustomer() throws Exception {
        mockMvc.perform(post("/api/follow-ups")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content(("""
                                {
                                  "customerId": %d,
                                  "followType": "CHAT",
                                  "communicatedContent": "深入沟通了课程需求",
                                  "customerFeedback": "对进阶课程感兴趣",
                                  "currentConcern": "时间安排",
                                  "nextAction": "发课程大纲",
                                  "nextFollowAt": "2026-06-20T10:00:00"
                                }
                                """).formatted(customerId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/customers/{id}", customerId).header("Authorization", bearer()))
                .andExpect(jsonPath("$.data.latestFeedback").value("深入沟通了课程需求"))
                .andExpect(jsonPath("$.data.currentConcern").value("时间安排"));
    }

    @Test @Order(21)
    void shouldListFollowUps() throws Exception {
        mockMvc.perform(get("/api/follow-ups")
                        .header("Authorization", bearer())
                        .param("customerId", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test @Order(22)
    void shouldFailFollowUpForNonExistentCustomer() throws Exception {
        mockMvc.perform(post("/api/follow-ups")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"customerId": 99999, "followType": "CHAT", "communicatedContent": "x"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4004));
    }

    // ═══════════ TASK ═══════════

    @Test @Order(30)
    void shouldCreateTask() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "taskTitle": "联调首页看板",
                                  "taskType": "DASHBOARD",
                                  "priority": "HIGH",
                                  "ownerUserId": 1,
                                  "dueAt": "2026-05-25T18:00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        taskId = JsonTestUtils.readJsonPathAsLong(
                result.getResponse().getContentAsString(), "$.data.id");
    }

    @Test @Order(31)
    void shouldListTaskBoard() throws Exception {
        mockMvc.perform(get("/api/tasks/board").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.data[0].status").value("TODO"))
                .andExpect(jsonPath("$.data[0].items", hasSize(1)));
    }

    @Test @Order(32)
    void shouldUpdateTaskStatus() throws Exception {
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "taskTitle": "联调首页看板",
                                  "status": "IN_PROGRESS",
                                  "priority": "HIGH",
                                  "ownerUserId": 1
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/board").header("Authorization", bearer()))
                .andExpect(jsonPath("$.data[1].items", hasSize(1)));
    }

    // ═══════════ REPORT ═══════════

    @Test @Order(40)
    void shouldCreateAndListDailyReport() throws Exception {
        mockMvc.perform(post("/api/reports/daily")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "reportDate": "2026-05-20",
                                  "todayWork": "完成P0客户模块联调",
                                  "tomorrowPlan": "开始跟进模块"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reports/daily").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test @Order(41)
    void shouldRejectDuplicateDailyReport() throws Exception {
        mockMvc.perform(post("/api/reports/daily")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "reportDate": "2026-05-20",
                                  "todayWork": "重复提交",
                                  "tomorrowPlan": "测试"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(4009));
    }

    @Test @Order(42)
    void shouldCreateWeeklyReport() throws Exception {
        mockMvc.perform(post("/api/reports/weekly")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "weekKey": "2026-W21",
                                  "weeklySummary": "本周完成P0后端全部真实化",
                                  "nextWeekPlan": "启动前端联调"
                                }
                                """))
                .andExpect(status().isOk());
    }

    // ═══════════ SCRIPT ═══════════

    @Test @Order(50)
    void shouldCreateAndGetScript() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/scripts")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "测试话术-开场白",
                                  "content": "你好，我是学长，最近在整理技术学习资料。",
                                  "applicableScene": "初次接触",
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        scriptId = JsonTestUtils.readJsonPathAsLong(
                result.getResponse().getContentAsString(), "$.data.id");
    }

    @Test @Order(51)
    void shouldGetScriptDetail() throws Exception {
        mockMvc.perform(get("/api/scripts/{id}", scriptId).header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("测试话术-开场白"));
    }

    @Test @Order(52)
    void shouldFilterScriptsByKeyword() throws Exception {
        mockMvc.perform(get("/api/scripts")
                        .header("Authorization", bearer())
                        .param("keyword", "学长"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1));

        mockMvc.perform(get("/api/scripts")
                        .header("Authorization", bearer())
                        .param("keyword", "不存在"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test @Order(53)
    void shouldUpdateScript() throws Exception {
        mockMvc.perform(put("/api/scripts/{id}", scriptId)
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "测试话术-更新版",
                                  "content": "更新后的内容",
                                  "applicableScene": "群聊邀请",
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/scripts/{id}", scriptId).header("Authorization", bearer()))
                .andExpect(jsonPath("$.data.title").value("测试话术-更新版"));
    }

    // ═══════════ FINAL DASHBOARD ═══════════

    @Test @Order(60)
    void shouldShowDashboardWithRealData() throws Exception {
        // Create high-intent customer so dashboard reflects it
        mockMvc.perform(post("/api/customers")
                        .header("Authorization", bearer())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "高意向客户B",
                                  "contactValue": "微信: highB",
                                  "sourcePlatform": "微信",
                                  "customerType": "OLD_GRADE1",
                                  "intentLevel": "A",
                                  "currentStage": "HIGH_INTENT",
                                  "ownerUserId": 1
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/overview").header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.todayTasks.length()", greaterThan(0)))
                .andExpect(jsonPath("$.data.highIntentCustomerCount", greaterThan(0)));
    }
}

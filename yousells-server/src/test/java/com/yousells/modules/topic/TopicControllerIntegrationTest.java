package com.yousells.modules.topic;

import com.yousells.common.security.JwtTokenProvider;
import com.yousells.common.security.LoginUser;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.topic.entity.TopicEntity;
import com.yousells.modules.topic.entity.TopicReplyEntity;
import com.yousells.modules.topic.mapper.TopicMapper;
import com.yousells.modules.topic.mapper.TopicReplyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TopicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicReplyMapper topicReplyMapper;

    @Autowired
    private UserMapper userMapper;

    private String authorToken;
    private Long authorId;
    private Long topicId;
    private Long replyId;

    @BeforeEach
    void setUp() {
        UserEntity author = new UserEntity();
        author.setUsername("author");
        author.setPasswordHash("hash");
        author.setRealName("秦梓源");
        author.setLevel("T2");
        author.setStatus("ACTIVE");
        userMapper.insert(author);
        authorId = author.getId();

        UserEntity other = new UserEntity();
        other.setUsername("other");
        other.setPasswordHash("hash");
        other.setRealName("小赵");
        other.setLevel("T0");
        other.setManagerUserId(authorId);
        other.setStatus("ACTIVE");
        userMapper.insert(other);

        authorToken = jwtTokenProvider.createAccessToken(new LoginUser(authorId, "author", "秦梓源", "T2", null));

        TopicEntity topic = new TopicEntity();
        topic.setTitle("测试问题");
        topic.setDescription("问题描述");
        topic.setCategory("邀约");
        topic.setAuthorUserId(authorId);
        topic.setSolved(0);
        topicMapper.insert(topic);
        topicId = topic.getId();

        TopicReplyEntity reply = new TopicReplyEntity();
        reply.setTopicId(topicId);
        reply.setUserId(other.getId());
        reply.setContent("测试回答");
        reply.setIsSolution(0);
        topicReplyMapper.insert(reply);
        replyId = reply.getId();
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldPageTopics() throws Exception {
        mockMvc.perform(get("/api/topics?page=1&pageSize=10")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].title").value("测试问题"))
                .andExpect(jsonPath("$.data.list[0].authorName").value("秦梓源"))
                .andExpect(jsonPath("$.data.list[0].replyCount").value(1));
    }

    @Test
    void shouldFilterByCategory() throws Exception {
        mockMvc.perform(get("/api/topics?category=邀约")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list", hasSize(1)));

        mockMvc.perform(get("/api/topics?category=沟通")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list", hasSize(0)));
    }

    @Test
    void shouldSearchByKeyword() throws Exception {
        mockMvc.perform(get("/api/topics?keyword=测试")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list", hasSize(1)));

        mockMvc.perform(get("/api/topics?keyword=不存在")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list", hasSize(0)));
    }

    @Test
    void shouldGetTopicDetail() throws Exception {
        mockMvc.perform(get("/api/topics/" + topicId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("测试问题"))
                .andExpect(jsonPath("$.data.authorName").value("秦梓源"))
                .andExpect(jsonPath("$.data.replies", hasSize(1)))
                .andExpect(jsonPath("$.data.replies[0].content").value("测试回答"))
                .andExpect(jsonPath("$.data.replies[0].userName").value("小赵"));
    }

    @Test
    void shouldReturn404ForNonExistentTopic() throws Exception {
        mockMvc.perform(get("/api/topics/9999")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateTopic() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType("application/json")
                        .content("{\"title\":\"新问题\",\"description\":\"描述\",\"category\":\"沟通\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    void shouldRejectTopicCreationWithoutTitle() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType("application/json")
                        .content("{\"title\":\"\",\"category\":\"沟通\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateReply() throws Exception {
        mockMvc.perform(post("/api/topics/" + topicId + "/replies")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType("application/json")
                        .content("{\"content\":\"我的新回答\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    void shouldRejectReplyWithoutContent() throws Exception {
        mockMvc.perform(post("/api/topics/" + topicId + "/replies")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType("application/json")
                        .content("{\"content\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldMarkSolved() throws Exception {
        mockMvc.perform(patch("/api/topics/" + topicId + "/solved")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/topics/" + topicId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(jsonPath("$.data.solved").value(true));
    }

    @Test
    void shouldMarkBestSolution() throws Exception {
        mockMvc.perform(patch("/api/topics/" + topicId + "/replies/" + replyId + "/solution")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/topics/" + topicId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(jsonPath("$.data.replies[0].isSolution").value(true));
    }
}

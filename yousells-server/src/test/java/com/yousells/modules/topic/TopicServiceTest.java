package com.yousells.modules.topic;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.LoginUser;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.topic.dto.TopicCreateRequest;
import com.yousells.modules.topic.dto.TopicQueryRequest;
import com.yousells.modules.topic.dto.TopicReplyCreateRequest;
import com.yousells.modules.topic.entity.TopicEntity;
import com.yousells.modules.topic.entity.TopicReplyEntity;
import com.yousells.modules.topic.mapper.TopicMapper;
import com.yousells.modules.topic.mapper.TopicReplyMapper;
import com.yousells.modules.topic.service.TopicService;
import com.yousells.modules.topic.vo.TopicDetailVo;
import com.yousells.modules.topic.vo.TopicListItemVo;
import com.yousells.modules.topic.vo.TopicReplyVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class TopicServiceTest {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicReplyMapper topicReplyMapper;

    @Autowired
    private UserMapper userMapper;

    private Long authorId;
    private Long otherUserId;
    private Long topicId1;
    private Long topicId2;
    private Long topicId3;
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
        otherUserId = other.getId();

        LoginUser loginUser = new LoginUser(authorId, "author", "秦梓源", "T2", null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));

        TopicEntity t1 = new TopicEntity();
        t1.setTitle("如何加到新生？");
        t1.setDescription("食堂地推怎么开口比较好");
        t1.setCategory("邀约");
        t1.setAuthorUserId(authorId);
        t1.setSolved(0);
        topicMapper.insert(t1);
        topicId1 = t1.getId();

        TopicEntity t2 = new TopicEntity();
        t2.setTitle("技术栈怎么聊？");
        t2.setDescription("学生不知道自己适合什么方向");
        t2.setCategory("沟通");
        t2.setAuthorUserId(otherUserId);
        t2.setSolved(0);
        topicMapper.insert(t2);
        topicId2 = t2.getId();

        TopicEntity t3 = new TopicEntity();
        t3.setTitle("已解决的问题");
        t3.setDescription("这个问题已经解决了");
        t3.setCategory("跟进");
        t3.setAuthorUserId(authorId);
        t3.setSolved(1);
        topicMapper.insert(t3);
        topicId3 = t3.getId();

        TopicReplyEntity reply = new TopicReplyEntity();
        reply.setTopicId(topicId1);
        reply.setUserId(otherUserId);
        reply.setContent("可以先从社团切入，或者公众号投放");
        reply.setIsSolution(0);
        topicReplyMapper.insert(reply);
        replyId = reply.getId();
    }

    // ── pageTopics ──

    @Test
    void shouldPageAllTopics() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, null, 1, 10));
        assertThat(result.total()).isEqualTo(3);
        assertThat(result.list()).hasSize(3);
    }

    @Test
    void shouldPaginateCorrectly() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, null, 1, 1));
        assertThat(result.list()).hasSize(1);
        assertThat(result.total()).isEqualTo(3);
    }

    @Test
    void shouldFilterByCategory() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest("邀约", null, 1, 10));
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().get(0).category()).isEqualTo("邀约");
    }

    @Test
    void shouldSearchByKeyword() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, "新生", 1, 10));
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().get(0).title()).isEqualTo("如何加到新生？");
    }

    @Test
    void shouldReturnEmptyForNoMatch() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, "不存在关键词", 1, 10));
        assertThat(result.total()).isEqualTo(0);
        assertThat(result.list()).isEmpty();
    }

    @Test
    void shouldIncludeAuthorNameInList() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, null, 1, 10));
        TopicListItemVo first = result.list().get(0);
        assertThat(first.authorName()).isNotBlank();
    }

    @Test
    void shouldIncludeReplyCountInList() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, null, 1, 10));
        TopicListItemVo topicWithReply = result.list().stream()
                .filter(t -> t.id().equals(topicId1))
                .findFirst().orElseThrow();
        assertThat(topicWithReply.replyCount()).isEqualTo(1);
    }

    @Test
    void shouldReturnSolvedTrueForSolvedTopic() {
        PageResponse<TopicListItemVo> result = topicService.pageTopics(
                new TopicQueryRequest(null, null, 1, 10));
        TopicListItemVo solved = result.list().stream()
                .filter(t -> t.id().equals(topicId3))
                .findFirst().orElseThrow();
        assertThat(solved.solved()).isTrue();
    }

    // ── getTopicDetail ──

    @Test
    void shouldGetTopicDetail() {
        TopicDetailVo detail = topicService.getTopicDetail(topicId1);
        assertThat(detail.id()).isEqualTo(topicId1);
        assertThat(detail.title()).isEqualTo("如何加到新生？");
        assertThat(detail.category()).isEqualTo("邀约");
        assertThat(detail.authorName()).isEqualTo("秦梓源");
        assertThat(detail.replies()).hasSize(1);
        assertThat(detail.replies().get(0).content()).isEqualTo("可以先从社团切入，或者公众号投放");
        assertThat(detail.replies().get(0).userName()).isEqualTo("小赵");
    }

    @Test
    void shouldReturnEmptyRepliesForTopicWithoutReplies() {
        TopicDetailVo detail = topicService.getTopicDetail(topicId2);
        assertThat(detail.replies()).isEmpty();
    }

    @Test
    void shouldThrowNotFoundWhenTopicNotExists() {
        assertThatThrownBy(() -> topicService.getTopicDetail(9999L))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }

    // ── createTopic ──

    @Test
    void shouldCreateTopic() {
        TopicCreateRequest request = new TopicCreateRequest("新的问题", "详细描述", "转化");
        Long newId = topicService.createTopic(request);
        assertThat(newId).isNotNull().isPositive();

        TopicDetailVo created = topicService.getTopicDetail(newId);
        assertThat(created.title()).isEqualTo("新的问题");
        assertThat(created.category()).isEqualTo("转化");
        assertThat(created.authorUserId()).isEqualTo(authorId);
        assertThat(created.solved()).isFalse();
    }

    // ── createReply ──

    @Test
    void shouldCreateReply() {
        TopicReplyCreateRequest request = new TopicReplyCreateRequest("我的回答");
        Long newReplyId = topicService.createReply(topicId1, request);
        assertThat(newReplyId).isNotNull().isPositive();

        TopicDetailVo detail = topicService.getTopicDetail(topicId1);
        assertThat(detail.replies()).hasSize(2);
    }

    @Test
    void shouldThrowNotFoundWhenReplyToNonExistentTopic() {
        TopicReplyCreateRequest request = new TopicReplyCreateRequest("回答");
        assertThatThrownBy(() -> topicService.createReply(9999L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }

    // ── markSolved ──

    @Test
    void shouldMarkTopicAsSolved() {
        topicService.markSolved(topicId1);

        TopicDetailVo detail = topicService.getTopicDetail(topicId1);
        assertThat(detail.solved()).isTrue();
    }

    @Test
    void shouldThrowBadRequestWhenNonAuthorMarksSolved() {
        LoginUser otherUser = new LoginUser(otherUserId, "other", "小赵", "T0", authorId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(otherUser, null, List.of()));

        assertThatThrownBy(() -> topicService.markSolved(topicId1))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.BAD_REQUEST);
    }

    @Test
    void shouldThrowNotFoundWhenMarkingSolvedForNonExistentTopic() {
        assertThatThrownBy(() -> topicService.markSolved(9999L))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }

    // ── markBestSolution ──

    @Test
    void shouldMarkBestSolution() {
        topicService.markBestSolution(topicId1, replyId);

        TopicDetailVo detail = topicService.getTopicDetail(topicId1);
        TopicReplyVo solution = detail.replies().stream()
                .filter(r -> r.id().equals(replyId))
                .findFirst().orElseThrow();
        assertThat(solution.isSolution()).isTrue();
    }

    @Test
    void shouldOnlyHaveOneBestSolution() {
        TopicReplyEntity reply2 = new TopicReplyEntity();
        reply2.setTopicId(topicId1);
        reply2.setUserId(authorId);
        reply2.setContent("另一个回答");
        reply2.setIsSolution(0);
        topicReplyMapper.insert(reply2);

        topicService.markBestSolution(topicId1, replyId);
        topicService.markBestSolution(topicId1, reply2.getId());

        TopicDetailVo detail = topicService.getTopicDetail(topicId1);
        long solutionCount = detail.replies().stream()
                .filter(TopicReplyVo::isSolution)
                .count();
        assertThat(solutionCount).isEqualTo(1);

        TopicReplyVo newSolution = detail.replies().stream()
                .filter(r -> r.id().equals(reply2.getId()))
                .findFirst().orElseThrow();
        assertThat(newSolution.isSolution()).isTrue();
    }

    @Test
    void shouldThrowBadRequestWhenNonAuthorMarksSolution() {
        LoginUser otherUser = new LoginUser(otherUserId, "other", "小赵", "T0", authorId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(otherUser, null, List.of()));

        assertThatThrownBy(() -> topicService.markBestSolution(topicId1, replyId))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.BAD_REQUEST);
    }

    @Test
    void shouldThrowNotFoundWhenMarkingSolutionForNonExistentTopic() {
        assertThatThrownBy(() -> topicService.markBestSolution(9999L, replyId))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }

    @Test
    void shouldThrowNotFoundWhenMarkingNonExistentReplyAsSolution() {
        assertThatThrownBy(() -> topicService.markBestSolution(topicId1, 9999L))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }
}

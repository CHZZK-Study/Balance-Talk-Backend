package balancetalk.comment.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.comment.dto.CommentDto;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private static final int BEST_COMMENTS_SIZE = 3;
    private static final int MIN_COUNT_FOR_BEST_COMMENT = 10;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final VoteRepository voteRepository;

    @Value("${comments.max-depth}")
    private int maxDepth;

    public CommentDto.CommentResponse createComment(CommentDto.CreateCommentRequest createCommentRequest, Long talkPickId) {
        Member member = getCurrentMember(memberRepository);
        TalkPick talkPick = validateTalkPickId(talkPickId);
        //BalanceOption balanceOption = validateBalanceOptionId(request, post); TODO : Vote 구현 완료 후 작업
        //voteRepository.findByMemberIdAndBalanceOption_PostId(member.getId(), postId)
        //.orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE));

        Comment comment = createCommentRequest.toEntity(member, talkPick);
        commentRepository.save(comment);
        return CommentDto.CommentResponse.fromEntity(comment, false);
    }

    @Transactional
    public void createCommentReply(CommentDto.CreateCommentRequest createCommentRequest, Long talkPickId, Long commentId) {
        Member member = getCurrentMember(memberRepository);
        TalkPick talkPick = validateTalkPickId(talkPickId);
        Comment parentComment = validateCommentId(commentId);

        // 부모 댓글과 연결된 게시글이 아닌 경우 예외 처리
        if (!parentComment.getTalkPick().equals(talkPick)) {
            throw new BalanceTalkException(NOT_FOUND_PARENT_COMMENT_AT_THAT_TALK_PICK);
        }

        // 부모 댓글의 depth가 maxDepth를 초과하는 경우 예외 처리 (답글에 답글 불가)
        validateDepth(parentComment);


        Comment commentReply = createCommentRequest.toEntity(member, talkPick, parentComment);
        commentRepository.save(commentReply);
    }

    @Transactional(readOnly = true)
    public Page<CommentDto.CommentResponse> findAllComments(Long talkPickId, String token, Pageable pageable) {
        validateTalkPickId(talkPickId);

        Page<Comment> comments = commentRepository.findAllByTalkPickId(talkPickId, pageable);
        return comments.map(comment -> CommentDto.CommentResponse.fromEntity(comment, false));

        /*return comments.map(comment -> {
            Optional<Vote> voteForComment = voteRepository.findByMemberIdAndBalanceOption_PostId(
                    comment.getMember().getId(), talkPickId);

            Long balanceOptionId = voteForComment.map(Vote::getBalanceOption).map(BalanceOption::getId)
                    .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));

            if (token == null) {
                return CommentResponse.fromEntity(comment, balanceOptionId, false);
            } else {
                Member member = getCurrentMember(memberRepository);
                return CommentResponse.fromEntity(comment, balanceOptionId, member.hasLikedComment(comment));
            }
        });

         */
    }

    @Transactional(readOnly = true)
    public Page<CommentDto.CommentResponse> findAllBestComments(Long talkPickId, Pageable pageable) {
        validateTalkPickId(talkPickId);

        List<Comment> allComments = commentRepository.findByTalkPickIdOrderByLikesCountDescCreatedAtDesc(talkPickId);
        List<CommentDto.CommentResponse> bestComments = new ArrayList<>();
        List<CommentDto.CommentResponse> otherComments = new ArrayList<>();

        // 최대 좋아요 수 구하기
        int maxLikes = allComments.stream()
                .mapToInt(Comment::getLikesCount)
                .max()
                .orElse(0);

        // 좋아요 10개 이상인 댓글이 있는 경우
        if (maxLikes >= MIN_COUNT_FOR_BEST_COMMENT) {
            for (Comment comment : allComments) {
                boolean myLike = false;
                comment.setIsBest(comment.getLikesCount() >= MIN_COUNT_FOR_BEST_COMMENT);
                CommentDto.CommentResponse response = CommentDto.CommentResponse.fromEntity(comment, myLike);
                if (comment.getIsBest()) {
                    bestComments.add(response);
                } else {
                    otherComments.add(response);
                }
            }
        } else {
            for (Comment comment : allComments) {
                boolean myLike = false;
                comment.setIsBest(comment.getLikesCount() == maxLikes);
                CommentDto.CommentResponse response = CommentDto.CommentResponse.fromEntity(comment, myLike);
                if (comment.getIsBest()) {
                    bestComments.add(response);
                } else {
                    otherComments.add(response);
                }
            }
        }

        bestComments.sort(Comparator.comparing(CommentDto.CommentResponse::getCreatedAt).reversed());
        otherComments.sort(Comparator.comparing(CommentDto.CommentResponse::getCreatedAt).reversed());

        List<CommentDto.CommentResponse> result = new ArrayList<>();
        result.addAll(bestComments);
        result.addAll(otherComments);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());

        return new PageImpl<>(result.subList(start, end), pageable, result.size());
    }

    /*
    @Transactional(readOnly = true)
    public Page<MyPageResponse> findAllByCurrentMember(Pageable pageable) {
        Member currentMember = getCurrentMember(memberRepository);

        return commentRepository.findAllByMemberEmail(currentMember.getEmail(), pageable)
                .map(MyPageResponse::fromEntity);
    }

     */

    public Comment updateComment(Long commentId, Long talkPickId, String content) {
        Comment comment = validateCommentId(commentId);
        validateTalkPickId(talkPickId); // TODO: talkPickId를 굳이 클라이언트로 받아야하는가?

        if (!getCurrentMember(memberRepository).equals(comment.getMember())) {
            throw new BalanceTalkException(FORBIDDEN_COMMENT_MODIFY);
        }

        if (!comment.getTalkPick().getId().equals(talkPickId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_POST);
        }

        comment.updateContent(content);
        return comment;
    }

    public void deleteComment(Long commentId, Long talkPickId) {
        Comment comment = validateCommentId(commentId);
        Member commentMember = commentRepository.findById(commentId).get().getMember();
        validateTalkPickId(talkPickId);

        if (!getCurrentMember(memberRepository).equals(commentMember)) {
            throw new BalanceTalkException(FORBIDDEN_COMMENT_DELETE);
        }

        if (!comment.getTalkPick().getId().equals(talkPickId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_POST);
        }

        commentRepository.deleteById(commentId);
    }

    /*
    @Transactional
    public Comment createReply(Long postId, Long commentId, ReplyCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));

        Member member = getCurrentMember(memberRepository);

        // 부모 댓글과 연결된 게시글이 맞는지 확인
        if (!parentComment.getPost().equals(post)) {
            throw new BalanceTalkException(NOT_FOUND_PARENT_COMMENT);
        }

        validateDepth(parentComment);

        Comment reply = request.toEntity(member, post, parentComment);
        return commentRepository.save(reply);
    }

    public List<ReplyResponse> findAllReplies(Long postId, Long parentId, String token) {
        validatePostId(postId);

        List<Comment> replies = commentRepository.findAllByPostIdAndParentId(postId, parentId);

        if (token == null) {
            return replies.stream()
                    .map(reply -> ReplyResponse.fromEntity(reply, null, false))
                    .collect(Collectors.toList());
        } else {
            Member member = getCurrentMember(memberRepository);

            return replies.stream()
                    .map(reply -> {
                        boolean myLike = member.hasLikedComment(reply);

                        Optional<Vote> voteForComment = voteRepository.findByMemberIdAndBalanceOption_PostId(
                                reply.getMember().getId(), postId);

                        Long balanceOptionId = voteForComment.map(Vote::getBalanceOption).map(BalanceOption::getId)
                                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));

                        return ReplyResponse.fromEntity(reply, balanceOptionId, myLike);
                    })
                    .collect(Collectors.toList());
        }
    }

    private TalkPick validateTalkPickId(Long talkPickId) {
        return talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
    }

    /*
    private BalanceOption validateBalanceOptionId(CommentRequest request, Post post) {
        return post.getOptions().stream()
                // .filter(option -> option.getId().equals(request.getSelectedOptionId())) // TODO : 스웨거 변경 중 호환 안됨. 수정 필요
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));
    }

     */

    private TalkPick validateTalkPickId(Long talkPickId) {
        return talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK)); // TODO : 에러메시지 수정 필요
    }

    private Comment validateCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
    }

    private void validateDepth(Comment parentComment) {
        int depth = calculateDepth(parentComment);
        if (depth >= maxDepth) {
            throw new BalanceTalkException(EXCEED_MAX_DEPTH);
        }
    }

    private int calculateDepth(Comment comment) {
        int depth = 0;
        while (comment.getParent() != null) {
            depth++;
            comment = comment.getParent();
        }
        return depth;
    }

    /*
    public Long likeComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
        Member member = getCurrentMember(memberRepository);

        if (commentLikeRepository.existsByMemberAndComment(member, comment)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_LIKE_COMMENT);
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();
        commentLikeRepository.save(commentLike);

        return comment.getId();
    }

    public void cancelLikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
        Member member = getCurrentMember(memberRepository);

        commentLikeRepository.deleteByMemberAndComment(member, comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findBestComments(Long postId, String token) {
        Post post = validatePostId(postId);
        List<BalanceOption> options = post.getOptions();

        List<CommentResponse> responses = new ArrayList<>();
        for (BalanceOption option : options) {
            List<Long> memberIdsBySelectedOptionId =
                    memberRepository.findMemberIdsBySelectedOptionId(option.getId());

            List<Comment> bestComments = commentRepository.findBestCommentsByPostId(postId,
                    memberIdsBySelectedOptionId, MIN_COUNT_FOR_BEST_COMMENT, PageRequest.of(0, BEST_COMMENTS_SIZE));

            if (token == null) {
                responses.addAll(bestComments.stream()
                        .map(comment -> CommentResponse.fromEntity(comment, option.getId(), false)).toList());
            } else {
                Member member = getCurrentMember(memberRepository);
                responses.addAll(bestComments.stream()
                        .map(comment ->
                                CommentResponse.fromEntity(comment, option.getId(), member.hasLikedComment(comment)))
                        .toList());
            }
        }
        return responses;
    }

    public void reportComment(Long postId, Long commentId, ReportRequest reportRequest) {
        Comment comment = validateCommentId(commentId);
        Member member = getCurrentMember(memberRepository);
        if (reportRepository.existsByReporterAndComment(member, comment)) {

            throw new BalanceTalkException(ALREADY_REPORTED_COMMENT);
        }
        if (comment.getMember().equals(member)) {
            throw new BalanceTalkException(FORBIDDEN_COMMENT_REPORT);
        }
        if (!comment.getPost().getId().equals(postId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_POST);
        }
        Report report = Report.builder()
                .content(reportRequest.getDescription())
                .reporter(member)
                .comment(comment)
                .category(reportRequest.getCategory())
                .build();
        reportRepository.save(report);
    }

     */
}

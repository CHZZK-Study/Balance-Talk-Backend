package balancetalk.member.application;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.bookmark.domain.BookmarkType;
import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.dto.GameDto.GameMyPageResponse;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickMyPageResponse;
import balancetalk.vote.domain.TalkPickVote;
import balancetalk.vote.domain.TalkPickVoteRepository;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TalkPickVoteRepository talkPickVoteRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final GameRepository gameRepository;

    public Page<TalkPickMyPageResponse> findAllBookmarkedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<Bookmark> bookmarks = bookmarkRepository.findActivatedByMemberOrderByDesc(member, BookmarkType.TALK_PICK, pageable);

        List<TalkPickMyPageResponse> responses = bookmarks.stream()
                .map(bookmark -> { TalkPick talkPick = talkPickRepository.findById(bookmark.getResourceId()).get();
                    return TalkPickMyPageResponse.from(talkPick, bookmark);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<TalkPickMyPageResponse> findAllVotedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<TalkPickVote> votes = talkPickVoteRepository.findAllByMemberIdAndTalkPickDesc(member.getId(), pageable);

        List<TalkPickMyPageResponse> responses = votes.stream()
                .map(vote -> TalkPickMyPageResponse.from(vote.getTalkPick(), vote))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<TalkPickMyPageResponse> findAllCommentedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<Comment> comments = commentRepository.findAllLatestCommentsByMemberIdAndOrderByDesc(member.getId(), pageable);

        List<TalkPickMyPageResponse> responses = comments.stream()
                .map(comment -> TalkPickMyPageResponse.from(comment.getTalkPick(), comment))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<TalkPickMyPageResponse> findAllTalkPicksByMember(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<TalkPick> talkPicks = talkPickRepository.findAllByMemberIdOrderByEditedAtDesc(member.getId(), pageable);

        List<TalkPickMyPageResponse> responses = talkPicks.stream()
                .map(TalkPickMyPageResponse::fromMyTalkPick)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<GameMyPageResponse> findAllBookmarkedGames(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<Bookmark> bookmarks = bookmarkRepository.findActivatedByMemberOrderByDesc(member, BookmarkType.GAME, pageable);

        List<GameMyPageResponse> responses = bookmarks.stream()
                .map(bookmark -> { Game game = gameRepository.findById(bookmark.getResourceId()).get();
                    return GameMyPageResponse.from(game, bookmark);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<GameMyPageResponse> findAllVotedGames(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);

        Page<GameVote> votes = voteRepository.findAllByMemberIdAndGameDesc(member.getId(), pageable);

        List<GameMyPageResponse> responses = votes.stream()
                .map(vote -> GameMyPageResponse.from(vote.getGameOption().getGame(), vote))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }

    public Page<GameMyPageResponse> findAllGamesByMember(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<Game> games = gameRepository.findAllByMemberIdOrderByEditedAtDesc(member.getId(), pageable);

        List<GameMyPageResponse> responses = games.stream()
                .map(GameMyPageResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }
}
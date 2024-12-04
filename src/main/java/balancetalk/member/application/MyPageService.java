package balancetalk.member.application;

import balancetalk.bookmark.domain.GameBookmark;
import balancetalk.bookmark.domain.TalkPickBookmark;
import balancetalk.bookmark.domain.GameBookmarkRepository;
import balancetalk.bookmark.domain.TalkPickBookmarkRepository;
import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.file.domain.File;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameSetRepository;
import balancetalk.game.dto.GameDto.GameMyPageResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.MemberDto.MemberActivityResponse;
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

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final GameBookmarkRepository gameBookmarkRepository;
    private final TalkPickBookmarkRepository talkPickBookmarkRepository;
    private final TalkPickVoteRepository talkPickVoteRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final GameRepository gameRepository;
    private final GameSetRepository gameSetRepository;
    private final FileRepository fileRepository;

    public Page<TalkPickMyPageResponse> findAllBookmarkedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<TalkPickBookmark> bookmarks = talkPickBookmarkRepository.findActivatedByMemberOrderByDesc(member, pageable);

        List<TalkPickMyPageResponse> responses = bookmarks.stream()
                .map(bookmark -> {
                    TalkPick talkPick = bookmark.getTalkPick();
                    return TalkPickMyPageResponse.from(talkPick, bookmark);
                })
                .toList();

        return new PageImpl<>(responses, pageable, bookmarks.getTotalElements());
    }

    public Page<TalkPickMyPageResponse> findAllVotedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<TalkPickVote> votes = talkPickVoteRepository.findAllByMemberIdAndTalkPickDesc(member.getId(), pageable);

        List<TalkPickMyPageResponse> responses = votes.stream()
                .map(vote -> TalkPickMyPageResponse.from(vote.getTalkPick(), vote))
                .toList();

        return new PageImpl<>(responses, pageable, votes.getTotalElements());
    }

    public Page<TalkPickMyPageResponse> findAllCommentedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<Comment> comments = commentRepository.findAllLatestCommentsByMemberIdAndOrderByDesc(member.getId(), pageable);

        List<TalkPickMyPageResponse> responses = comments.stream()
                .map(comment -> TalkPickMyPageResponse.from(comment.getTalkPick(), comment))
                .toList();

        return new PageImpl<>(responses, pageable, comments.getTotalElements());
    }

    public Page<TalkPickMyPageResponse> findAllTalkPicksByMember(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<TalkPick> talkPicks = talkPickRepository.findAllByMemberIdOrderByEditedAtDesc(member.getId(), pageable);

        List<TalkPickMyPageResponse> responses = talkPicks.stream()
                .map(TalkPickMyPageResponse::fromMyTalkPick)
                .toList();

        return new PageImpl<>(responses, pageable, talkPicks.getTotalElements());
    }

    public Page<GameMyPageResponse> findAllBookmarkedGames(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        Page<GameBookmark> bookmarks = gameBookmarkRepository.findActivatedByMemberOrderByDesc(member, pageable);

        List<GameMyPageResponse> responses = bookmarks.stream()
                .map(bookmark -> {
                    Game game = gameRepository.findById(bookmark.getGameId()) // 사용자가 북마크한 위치의 밸런스게임을 찾음
                            .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME));

                    List<Long> resourceIds = getResourceIds(game);
                    List<File> files = fileRepository.findAllByResourceIdsAndFileType(resourceIds, FileType.GAME_OPTION);
                    String imgA = game.getImgA(files);
                    String imgB = game.getImgB(files);

                    return GameMyPageResponse.from(game, bookmark, imgA, imgB);
                })
                .toList();

        return new PageImpl<>(responses, pageable, bookmarks.getTotalElements());
    }

    private List<Long> getResourceIds(Game game) {
        return game.getGameOptions().stream()
                .filter(option -> option.getImgId() != null)
                .map(GameOption::getImgId)
                .toList();
    }


//    public Page<GameMyPageResponse> findAllVotedGames(ApiMember apiMember, Pageable pageable) {
//        Member member = apiMember.toMember(memberRepository);
//
//        Page<GameVote> votes = voteRepository.findAllByMemberIdAndGameDesc(member.getId(), pageable);
//
//        List<GameMyPageResponse> responses = votes.stream()
//                .map(vote -> GameMyPageResponse.from(vote.getGameOption().getGame(), vote))
//                .toList();
//
//        return new PageImpl<>(responses, pageable, votes.getTotalElements());
//    }

//    public Page<GameMyPageResponse> findAllGamesByMember(ApiMember apiMember, Pageable pageable) {
//        Member member = apiMember.toMember(memberRepository);
//        Page<GameSet> gameSets = gameSetRepository.findAllByMemberIdOrderByEditedAtDesc(member.getId(), pageable);
//
//        List<GameMyPageResponse> responses = gameSets.stream()
//                .map(GameMyPageResponse::from)
//                .toList();
//
//        return new PageImpl<>(responses, pageable, gameSets.getTotalElements());
//    }

    public MemberActivityResponse getMemberActivity(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        return MemberActivityResponse.fromEntity(member);
    }
}
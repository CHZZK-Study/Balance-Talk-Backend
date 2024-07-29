package balancetalk.member.dto;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiMember {

    private Long memberId;

    public Member toMember(MemberRepository memberRepository) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
    }
}

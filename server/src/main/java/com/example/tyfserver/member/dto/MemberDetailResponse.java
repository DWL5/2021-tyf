package com.example.tyfserver.member.dto;

import com.example.tyfserver.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetailResponse {

    private String email;
    private String nickname;
    private String pageName;

    public MemberDetailResponse(Member member) {
        this(member.getEmail(), member.getNickname(), member.getPageName());
    }

    public MemberDetailResponse(String email, String nickname, String pageName) {
        this.email = email;
        this.nickname = nickname;
        this.pageName = pageName;
    }
}

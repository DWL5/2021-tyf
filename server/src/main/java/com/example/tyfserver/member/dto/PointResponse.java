package com.example.tyfserver.member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointResponse {

    private long point;

    public PointResponse(long point) {
        this.point = point;
    }
}

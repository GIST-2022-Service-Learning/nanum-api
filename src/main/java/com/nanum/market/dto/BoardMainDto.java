package com.nanum.market.dto;

import com.nanum.market.model.Board;
import lombok.Getter;

@Getter
public class BoardMainDto {
    private Long id;
    private String title;
    private String content;
    private String imgUrl;
    private Long userId;

    public BoardMainDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.imgUrl = board.getImgUrl();
        this.userId = board.getUser().getId();
    }
}

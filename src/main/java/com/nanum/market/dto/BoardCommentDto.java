package com.nanum.market.dto;

import com.nanum.market.model.Board;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardCommentDto {
    private Long boardId;
    private List<CommentDto> commentDtoList;

    public BoardCommentDto(Long boardId, List<CommentDto> commentDtoList) {
        this.boardId = boardId;
        this.commentDtoList = commentDtoList;
    }
}

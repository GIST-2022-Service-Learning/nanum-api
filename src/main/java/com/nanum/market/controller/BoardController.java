package com.nanum.market.controller;

import com.nanum.market.config.auth.PrincipalDetails;
import com.nanum.market.dto.*;
import com.nanum.market.model.Board;

import com.nanum.market.model.Message;
import com.nanum.market.s3.S3Uploader;
import com.nanum.market.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final S3Uploader s3Uploader;

    //전체게시글 조회, 검색 (메인페이지)
    @GetMapping("/main")
    public List<BoardMainDto> getBoard(@RequestParam(value = "searchText", required = false) String searchText, @AuthenticationPrincipal PrincipalDetails userDetails){
        if (searchText == null){
            return boardService.getBoards(userDetails.getUser().getId());
        }else{
            return boardService.getSearchBoard(searchText);
        }
    }

    @GetMapping("/board/me/heart")
    public List<BoardCommentDto> getHeartBoard(@AuthenticationPrincipal PrincipalDetails userDetails){
        Long userId = userDetails.getUser().getId();
        return boardService.getMyHeartBoard(userId);
    }

    @GetMapping("/board/me")
    public List<BoardMainDto> getMyBoard(@AuthenticationPrincipal PrincipalDetails userDetails) {
        return boardService.getMyBoard(userDetails.getUser());
    }

    // 게시글 작성
    @PostMapping("/boards")
    public BoardPostDto createBoard(@RequestParam(value = "title") String title, @RequestParam(value = "content", required = false) String content,
                                      @RequestParam(value = "file") MultipartFile files, @AuthenticationPrincipal PrincipalDetails userDetails) throws IOException {

        String imgUrl = s3Uploader.upload(files);
        BoardRequestDto requestDto = new BoardRequestDto(title, content, false, imgUrl);

        return boardService.createBoard(requestDto, userDetails.getUser().getId());

    }

    // 게시글 수정
    @PutMapping("/boards/{boardId}")
    public BoardPostDto updateBoard(@PathVariable Long boardId, @RequestParam("title") String title, @RequestParam("content") String content,
                                      @RequestParam(value = "status") boolean status,
                                      @RequestParam(value = "file", required = false) MultipartFile files, @RequestParam(value = "imgUrl", required = false) String imgUrl, @AuthenticationPrincipal PrincipalDetails userDetails) throws IOException {

        // 이미지 수정없이 게시글 수정할 때는 s3에 업로드 할 필요 없으므로 imgUrl이 안넘어 올 경우에만 업로드를 시켜준다.
        if(imgUrl == null) {
            imgUrl = s3Uploader.upload(files);
        }
        // 이미지를 수정안한 상태에서 보낼경우 또 업로드 하지않게 만들어야 할듯
        BoardRequestDto requestDto = new BoardRequestDto(title, content,status, imgUrl);

        return boardService.updateBoard(boardId, requestDto
                ,userDetails.getUser().getId());
    }

    // 거래 완료 기능
    @PutMapping("/boards/{boardId}/complete")
    public boolean completeBoard(@PathVariable Long boardId, @AuthenticationPrincipal PrincipalDetails userDetails) throws IOException {
        Long userId = userDetails.getUser().getId();
        return boardService.completeBoard(boardId,userId);
    }

    // 게시글 삭제
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal PrincipalDetails userDetails){
        Board board = boardService.deleteBoard(boardId, userDetails.getUser().getId());
        if (board==null){
            Message message = new Message("자신이 작성한 게시글만 삭제할 수 있습니다.");
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return ResponseEntity.ok().build();

    }

    // 게시글 상세페이지
    @GetMapping("/boards/{boardId}/details")
    public BoardDetailDto getDetailBoard(@PathVariable Long boardId){
        return boardService.getDetailBoard(boardId);
    }

}

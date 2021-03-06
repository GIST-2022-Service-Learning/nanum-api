package com.nanum.market.service;

import com.nanum.market.dto.*;
import com.nanum.market.model.Board;
import com.nanum.market.model.Comment;
import com.nanum.market.model.Heart;
import com.nanum.market.model.User;
import com.nanum.market.repository.BoardRepository;
import com.nanum.market.repository.CommentRepository;
import com.nanum.market.repository.HeartRepository;
import com.nanum.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final HeartRepository heartRepository;
    private final CommentRepository commentRepository;
    private final static int size = 10;

    // 게시글 조회
    public List<BoardMainDto> getBoard() {
        List<Board> board = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardMainDto> mainDtoList = new ArrayList<>();
        // main에 필요한 값들만 Dto로 만들어서 보내준다.
        for(int i=0; i<board.size(); i++){
            BoardMainDto mainDto = new BoardMainDto(board.get(i));
            mainDtoList.add(mainDto);
        }
        return mainDtoList;
    }

    // 좋아요한 게시물 배제하여 get
    public List<BoardMainDto> getBoards(Long userId) {
        List<Board> board = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardMainDto> mainDtoList = new ArrayList<>();

        for(int i=0; i<board.size(); i++){
            Heart heart = heartRepository.findByBoardIdAndUserId(board.get(i).getId(), userId);;
            if (heart == null && board.get(i).isStatus() == false && board.get(i).getUser().getId() != userId ) {
                BoardMainDto mainDto = new BoardMainDto(board.get(i));
                mainDtoList.add(mainDto);
            }
        }
        return mainDtoList;
    }

    // 좋아요한 게시물만  get
    public List<BoardCommentDto> getMyHeartBoard(Long userId) {
        List<Board> board = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardCommentDto> boardCommentDtoList = new ArrayList<>();
        for(int i=0; i<board.size(); i++){
            Long boardId = board.get(i).getId();
            String imgurl = board.get(i).getImgUrl();
            Long ownerId = board.get(i).getUser().getId();
            Boolean owned = false;
            if(ownerId == userId){
                owned = true;
            }
            Heart heart = heartRepository.findByBoardIdAndUserId(boardId, userId);;
            if (heart != null) {
                List<Comment> comment = commentRepository.findByBoardId(boardId);
                List<CommentDto> commentDtoList = new ArrayList<>();
                for(int j=0; j<comment.size(); j++) {
                    CommentDto commentDto = new CommentDto(comment.get(j));
                    commentDtoList.add(commentDto);
                }
                boardCommentDtoList.add(new BoardCommentDto(boardId, imgurl, owned, commentDtoList));
            }
        }
        return boardCommentDtoList;
    }

    // 올린 게시글 조회

    public List<BoardMainDto> getMyBoard(User user) {
        List<Board> board = boardRepository.findByUser(user);
        List<BoardMainDto> mainDtoList = new ArrayList<>();
        // main에 필요한 값들만 Dto로 만들어서 보내준다.
        for(int i=0; i<board.size(); i++){
            BoardMainDto mainDto = new BoardMainDto(board.get(i));
            mainDtoList.add(mainDto);
        }
        return mainDtoList;
    }

    // 거래 완료 표시
    public boolean completeBoard(Long boardId,Long userId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()-> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if(board.getUser().getId() == userId ){
            board.setStatus(true);
            boardRepository.save(board);
            return true;
        }
        return false;
    }

    // 검색한 게시글 조회
    public List<BoardMainDto> getSearchBoard(String title) {
        List<Board> board = boardRepository.findByTitleContainingOrContentContainingOrderByModifiedAtDesc(title, title);
        List<BoardMainDto> mainDtoList = new ArrayList<>();
        // main에 필요한 값들만 Dto로 만들어서 보내준다.
        for(int i=0; i<board.size(); i++){
            BoardMainDto mainDto = new BoardMainDto(board.get(i));
            mainDtoList.add(mainDto);
        }
        return mainDtoList;
    }

    // 무한스크롤 적용한 메인페이지
//    public Page<BoardMainDto> getBoard(int page) {
//        Pageable pageable = PageRequest.of(page-1, size);
//        List<Board> board = boardRepository.findAllByOrderByModifiedAtDesc();
//        List<BoardMainDto> mainDtoList = new ArrayList<>();
//        // main에 필요한 값들만 Dto로 만들어서 보내준다.
//        for(int i=0; i<board.size(); i++){
//            BoardMainDto mainDto = new BoardMainDto(board.get(i));
//            mainDtoList.add(mainDto);
//        }
//        return new PageImpl<>(mainDtoList, pageable, mainDtoList.size());
//    }
//
//    public Page<BoardMainDto> getSearchBoard(String title, int page) {
//        Pageable pageable = PageRequest.of(page-1, size);
//        List<Board> board = boardRepository.findByTitleContainingOrContentContaining(title, title);
//        List<BoardMainDto> mainDtoList = new ArrayList<>();
//        // main에 필요한 값들만 Dto로 만들어서 보내준다.
//        for(int i=0; i<board.size(); i++){
//            BoardMainDto mainDto = new BoardMainDto(board.get(i));
//            mainDtoList.add(mainDto);
//        }
//        return new PageImpl<>(mainDtoList, pageable, mainDtoList.size());
//    }

    // 게시글 작성
    public BoardPostDto createBoard(BoardRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("계정이 존재하지 않습니다.")
        );
        Board board = new Board(requestDto);
        board.addUser(user);
        boardRepository.save(board);
        BoardPostDto boardPostDto = new BoardPostDto(board);
        return boardPostDto;

    }

    // 게시글 수정
    @Transactional
    public BoardPostDto updateBoard(Long boardId, BoardRequestDto requestDto,Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("계정이 존재하지 않습니다.")
        );
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()-> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        if (board.getUser().getId().equals(userId)){
            board.update(requestDto);
            BoardPostDto boardPostDto = new BoardPostDto(board);
            return boardPostDto;
        }
        else{
            return null;
        }

    }

    // 게시글 삭제
    @Transactional
    public Board deleteBoard(Long boardId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("계정이 존재하지 않습니다.")
        );
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()-> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        if (board.getUser().getId().equals(userId)) {
            boardRepository.deleteById(boardId);
            return board;
        }
        else{
            return null;
        }
    }


    // 게시글 상세조회
    public BoardDetailDto getDetailBoard(Long boardId) { // 게시글 작성한 사람의 아이디와 이메일을 보낸다.
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()-> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        BoardDetailDto boardDetailDto = new BoardDetailDto(board);
        return boardDetailDto;
    }


}

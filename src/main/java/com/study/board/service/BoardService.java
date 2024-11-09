package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    // 글 작성 처리
//    public void write(Board board, @RequestParam(name="file", required = false) MultipartFile file) throws Exception{
//
//        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
//
//        UUID uuid = UUID.randomUUID();
//
//        String fileName = uuid + "_" + file.getOriginalFilename();
//        File saveFile = new File(projectPath, fileName);
//
//        file.transferTo(saveFile);
//
//        board.setFilename(fileName);
//        board.setFilepath("/files/" + fileName);
//        boardRepository.save(board);
//    }

    public void write(Board board, @RequestParam(name="file", required = false) MultipartFile file) throws Exception {

        // 기본 파일 저장 경로 설정 (운영체제에 따라 경로 구분자를 자동으로 설정)
        Path projectPath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows인 경우
            projectPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "files");
        } else {
            // Linux인 경우
            projectPath = Paths.get("/home", "ec2-user", "files");
        }

        // 디렉터리 생성 (존재하지 않으면)
        File directory = projectPath.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // UUID를 사용하여 고유한 파일 이름 생성
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();

        // 최종 파일 경로 설정
        File saveFile = new File(directory, fileName);
        file.transferTo(saveFile);

        // 파일 정보 저장
        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);
        boardRepository.save(board);
    }

    public void write(Board board) throws Exception {
        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable){
        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 게시글 상세
    public Board boardView(Integer id){

        return boardRepository.findById(id).get();
    }

    // 게시글 삭제
    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }
}

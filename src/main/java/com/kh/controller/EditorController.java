package com.kh.controller;
import com.kh.dto.EditorDTO;
import com.kh.mapper.EditorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/editor")
public class EditorController {

    @Autowired
    private EditorMapper editorMapper;

    @PostMapping("/posts")
    public String createPost(@RequestBody EditorDTO editorDTO) {
//        editorMapper.insertEditor(editorDTO);
        System.out.println("글 저장 완료: " + editorDTO);
        return "저장 성공";
    }
}

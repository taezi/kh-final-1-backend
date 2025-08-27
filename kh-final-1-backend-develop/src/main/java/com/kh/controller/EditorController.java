package com.kh.controller;

import com.kh.dto.EditorDTO;
import com.kh.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/editor")
public class EditorController {

    @Autowired
    private EditorService editorService;

    //게시글 등록
    @PostMapping("/posts")
    public String createPost(@RequestBody EditorDTO editorDTO) {
        System.out.println("받은 데이터: " + editorDTO);
        System.out.println(editorDTO.getEditorcontent());
//        String content = editorDTO.getEditorcontent();
//
//        editorDTO.setEditortitle(content);

        editorService.insertEditor(editorDTO);
        return "저장 성공";
    }

    //게시글 전체 목록 조회
    @GetMapping("/list")
    public  Map<String, Object> list(){
        Map<String, Object> map = new HashMap<>();
        List<EditorDTO> elist = editorService.selectEditorAll();

        map.put("eList",elist);


        return map;

    }
}

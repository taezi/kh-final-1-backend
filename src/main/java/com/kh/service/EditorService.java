package com.kh.service;

import com.kh.dto.EditorDTO;
import com.kh.mapper.EditorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EditorService {

    @Autowired
    private EditorMapper editorMapper;

    //게시글 저장
    public void insertEditor(EditorDTO editorDTO) {
        editorMapper.insertEditor(editorDTO);
    }

    //게시글 전체 조회
    public List<EditorDTO> selectEditorAll() {
        return editorMapper.selectEditorAll();
    }

    //게시글 상세 조회
    public EditorDTO selectEditorById(long editorno) {
        return editorMapper.selectEditorById(editorno);
    }

    //게시글 삭제
    public void deleteEditor(long editorno) {
        editorMapper.deleteEditor(editorno);
    }

    //게시글 수정
    public boolean updateEditor(EditorDTO editorDTO) {
        int cnt = editorMapper.updateEditor(editorDTO);  // DB 업데이트 후 수정된 행(row) 수 반환
        return cnt > 0; // 1 이상이면 true → 수정 성공
    }
    // 검색
    public List<EditorDTO> findEditors(String search) {
        // null 처리: 검색어가 없으면 전체 조회
        if (search == null) search = "";

        Map<String, Object> params = new HashMap<>();
        params.put("search", search);

        return editorMapper.findEditors(params);
    }

}

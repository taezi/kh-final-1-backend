package com.kh.service;

import com.kh.dto.EditorDTO;
import com.kh.mapper.EditorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditorService {

    @Autowired
    private EditorMapper editorMapper;

    // 게시글 저장
    public void insertEditor(EditorDTO editorDTO) {
        // userno도 DB에 저장하도록 Mapper 수정 필요
        editorMapper.insertEditor(editorDTO);
    }

    public List<EditorDTO> selectEditorAll() {
        return editorMapper.selectEditorAll();
    }
}

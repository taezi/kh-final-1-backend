package com.kh.mapper;

import com.kh.dto.EditorDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EditorMapper {

    //게시글 등록
    void insertEditor(EditorDTO editorDTO);

    //게시글 전체 조회
    List<EditorDTO> selectEditorAll();

    //게시글 상세 조회
    EditorDTO selectEditorById(long editorno);

    //공지글 삭제
    int updateEditor(EditorDTO editorDTO);

    //게시글 삭제
    void deleteEditor(long editorno);

    //검색
    List<EditorDTO> findEditors(Map<String, Object> params);


    // 조회수 증가
    int incrementView(long editorno);

}
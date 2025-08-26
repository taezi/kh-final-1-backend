package com.kh.mapper;

import com.kh.dto.EditorDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EditorMapper {


    void insertEditor(EditorDTO editorDTO);

    List<EditorDTO> selectEditorAll();
}
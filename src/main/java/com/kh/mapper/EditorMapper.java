package com.kh.mapper;

import com.kh.dto.EditorDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EditorMapper {

    @Insert("INSERT INTO editor (editortitle, editorcontent) VALUES (#{editortitle}, #{editorcontent})")
    void insertEditor(EditorDTO editorDTO);
}
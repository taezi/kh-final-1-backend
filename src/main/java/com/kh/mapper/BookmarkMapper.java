package com.kh.mapper;

import com.kh.dto.BookmarkDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookmarkMapper {
    int addBookmark(BookmarkDTO bookmarkDTO);

    int removeBookmark(BookmarkDTO bookmarkDTO);

    List<BookmarkDTO> getBookmarksByType(Long userno, String type);

    List<BookmarkDTO> getAllBookmarks(Long userno);
}

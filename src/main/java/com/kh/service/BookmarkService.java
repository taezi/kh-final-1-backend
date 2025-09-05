package com.kh.service;

import com.kh.dto.BookmarkDTO;
import com.kh.mapper.BookmarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkMapper bookmarkMapper;


    public int addBookmark(BookmarkDTO bookmarkDTO) {
        return bookmarkMapper.addBookmark(bookmarkDTO);
    }

    public int removeBookmark(BookmarkDTO bookmarkDTO) {
        return bookmarkMapper.removeBookmark(bookmarkDTO);
    }

    public List<BookmarkDTO> getBookmarksByType(Long userno, String type) {

        return bookmarkMapper.getBookmarksByType(userno, type);
    }

    public List<BookmarkDTO> getAllBookmarks(Long userno) {

        return bookmarkMapper.getAllBookmarks(userno);
    }
}

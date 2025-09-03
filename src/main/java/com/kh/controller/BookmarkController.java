package com.kh.controller;

import com.kh.dto.BookmarkDTO;
import com.kh.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping("/{userno}")
    public List<BookmarkDTO> getUserBookmarks(
            @PathVariable Long userno,
            @RequestParam(required = false) String type
    ) {
        System.out.println("북마크 usrno : " + userno);
        System.out.println("북마크 type : " + type);
        if (type != null) {
            System.out.println("11111111 : " +  bookmarkService.getBookmarksByType(userno, type));
            return bookmarkService.getBookmarksByType(userno, type);
        }
        return bookmarkService.getAllBookmarks(userno);
    }



    @PostMapping
    public ResponseEntity<Void> addBookmark(@RequestBody BookmarkDTO bookmarkDTO){
        System.out.println("북마크 추가 정보 : " + bookmarkDTO);
        bookmarkService.addBookmark(bookmarkDTO);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        System.out.println("북마크 삭제 정보 : " + bookmarkDTO);
        bookmarkService.removeBookmark(bookmarkDTO);
        return ResponseEntity.ok().build();
    }
}

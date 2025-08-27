package com.kh.controller;

import com.kh.service.MovieService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//클라이언트의 요청을 받아 영화 관련 데이터를 제공하는 RESTful API 컨트롤러
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

}
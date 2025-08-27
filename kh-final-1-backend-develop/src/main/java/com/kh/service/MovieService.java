package com.kh.service;

import com.kh.dto.MovieDTO;
import com.kh.dto.MovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class MovieService {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/now_playing";

    public List<MovieDTO> getNowPlayingMovies() {
        URI uri = UriComponentsBuilder.fromUriString(TMDB_BASE_URL)
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "ko-KR")
                .build()
                .toUri();

        MovieResponse response = restTemplate.getForObject(uri, MovieResponse.class);

        return response != null ? response.getResults() : null;
    }
}
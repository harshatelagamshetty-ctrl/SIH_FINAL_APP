package com.agribid.nexus.controller;

import com.agribid.nexus.domain.crop.Category;
import com.agribid.nexus.dto.response.CategoryResponse;
import com.agribid.nexus.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public and read-only — the full category taxonomy is not
 * sensitive data, and every client (mobile app, web console) needs
 * a real list to build a picker against instead of hardcoding one
 * that can silently drift out of sync with the actual seed data.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getCode(), c.getName()))
                .toList();
        return ResponseEntity.ok(categories);
    }
}
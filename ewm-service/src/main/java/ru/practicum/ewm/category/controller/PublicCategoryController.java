package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/categories")
@AllArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(required = false, defaultValue = "0")
                                                              @PositiveOrZero int from,
                                                              @RequestParam(required = false, defaultValue = "10")
                                                              @Positive int size) {
        log.info("GET request for path /categories with from={}, size={}", from, size);
        return ResponseEntity.ok(categoryService.getAllCategories(PageRequest.of(from, size)));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("catId") Long id) {
        log.info("GET request for path /categories/{catId} with id={}", id);
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exeption.ValidationException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventService eventService;

    @Override
    public List<CategoryDto> getAllCategories(Pageable pageable) {
        log.info("Getting all category");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        try {
            log.info("Getting category with id={}", id);
            return categoryMapper.toCategoryDto(categoryRepository.findById(id).get());
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Category with id %s is not found", id));
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info("Updating category: {}", categoryDto);
        Category category = categoryMapper.toCategory(getCategoryById(categoryDto.getId()));
        if (categoryRepository.existsCategoriesByName(categoryDto.getName()))
            throw new ValidationException(String.format("Category name %s is already exist", categoryDto.getName()));
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        try {
            log.info("Creating category: {}", categoryDto);
            return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException(String.format("Category name %s is already exist", categoryDto.getName()));
        }
    }

    @Override
    public void deleteCategory(Long id) {
        try {
            log.info("Deleting category with id={}", id);
            Boolean haveEvent = eventService.existsByCategoryId(id);
            if (haveEvent) throw new ValidationException(String.format("Category with id %s have events", id));
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException(String.format("Category with id %s is not found", id));
        }
    }
}

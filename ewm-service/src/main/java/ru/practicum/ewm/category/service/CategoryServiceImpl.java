package ru.practicum.ewm.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.mapper.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

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
    //TODO: проверить на то, какая ошибка должна выпасть
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        getCategoryById(categoryDto.getId());
        try {
            log.info("Updating category: {}", categoryDto);
            return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("Category %s is not found", categoryDto));
        }
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    //TODO: проверить на то, что ни одно событие не связано с категорией и на то, какая ошибка должна выпасть
    @Override
    public void deleteCategory(Long id) {
        try {
            log.info("Deleting category with id={}", id);
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException(String.format("Category with id %s is not found", id));
        }
    }
}

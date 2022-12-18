package ru.practicum.ewm.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public List<CategoryDto> getAllCategories(Pageable pageable) {
        return null;
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
}

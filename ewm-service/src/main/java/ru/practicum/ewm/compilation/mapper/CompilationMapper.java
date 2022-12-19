package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.dto.CompilationDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CompilationMapper {
    Compilation toCompilation(CompilationDto compilationDto);

    CompilationDto toCompilationDto(Compilation compilation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Compilation partialUpdate(CompilationDto compilationDto, @MappingTarget Compilation compilation);
}
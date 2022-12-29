package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CompilationMapper {
    Compilation toCompilation(CompilationDto compilationDto);

    CompilationDto toCompilationDto(Compilation compilation);
    List<CompilationDto> toCompilationDtoList(List<Compilation> compilations);
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);
    }
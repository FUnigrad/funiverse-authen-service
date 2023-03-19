package com.unigrad.funiverseauthenservice.util;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DTOConverter {

    private final ModelMapper modelMapper;

    public <T, U> U convert(T source, Class<U> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    public <T, U> U convert(List<T> source, Class<U> targetClass) {
        return modelMapper.map(source, targetClass);
    }
}
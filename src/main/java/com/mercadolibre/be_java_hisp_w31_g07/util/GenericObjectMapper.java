package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenericObjectMapper {
    
    private final ObjectMapper objectMapper;

    public <T, R> R map(T source, Class<R> targetClass) {
        return objectMapper.convertValue(source, targetClass);
    }

    public <T> List<T> mapList(List<?> source, Class<T> targetClass) {
        return source.stream()
                .map(object -> objectMapper.convertValue(object, targetClass))
                .toList();
    }
}

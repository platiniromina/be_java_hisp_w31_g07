package com.mercadolibre.be_java_hisp_w31_g07.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GenericObjectMapper {
    private static ObjectMapper objectMapper;

    public static <T, R> R map(T source, Class<R> targetClass) {
        return objectMapper.convertValue(source, targetClass);
    }

    public static <T> List<T> mapList(List<?> source, Class<T> targetClass) {
        return source.stream()
                .map(object -> objectMapper.convertValue(object, targetClass))
                .toList();
    }
}

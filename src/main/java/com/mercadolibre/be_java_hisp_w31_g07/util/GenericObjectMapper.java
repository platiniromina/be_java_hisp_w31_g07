package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class GenericObjectMapper {
    private final ObjectMapper objectMapper;

    public <T, R> R map(T source, Class<R> targetClass) {
        return objectMapper.convertValue(source, targetClass);
    }

    public <T, R> R mapWithExtraFields(T source, Class<R> targetClass, Map<String, Object> extraFields) {
        Map<String, Object> baseMap = objectMapper.convertValue(source, new TypeReference<>() {});
        baseMap.putAll(extraFields);
        return objectMapper.convertValue(baseMap, targetClass);
    }

    public <T, R> R mapWithNestedExtraFields(
            T source,
            Class<R> targetClass,
            Map<String, Object> topLevelExtraFields,
            Map<String, Map<String, Object>> nestedListExtrasByField
    ) {
        Map<String, Object> baseMap = objectMapper.convertValue(source, new TypeReference<>() {});

        // Extra a nivel raíz
        baseMap.putAll(topLevelExtraFields);

        // Extra a listas internas
        for (Map.Entry<String, Map<String, Object>> entry : nestedListExtrasByField.entrySet()) {
            String listFieldName = entry.getKey();
            Map<String, Object> extraForEachItem = entry.getValue();

            Object listRaw = baseMap.get(listFieldName);
            if (listRaw instanceof List<?>) {
                List<?> originalList = (List<?>) listRaw;
                List<Map<String, Object>> updatedList = new ArrayList<>();

                for (Object item : originalList) {
                    Map<String, Object> itemMap = objectMapper.convertValue(item, new TypeReference<>() {});
                    itemMap.putAll(extraForEachItem);
                    updatedList.add(itemMap);
                }

                baseMap.put(listFieldName, updatedList);
            }
        }

        return objectMapper.convertValue(baseMap, targetClass);
    }
}

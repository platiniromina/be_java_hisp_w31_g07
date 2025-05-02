package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class ValidationUtil {

    public static <T> void throwIfEmpty(Collection<T> collection, String errorMessage) {
        if (collection == null || collection.isEmpty()) {
            throw new BadRequest(errorMessage);
        }
    }
    
}

package com.mercadolibre.be_java_hisp_w31_g07.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class IdUtils {

    public static UUID generateId() {
        return UUID.randomUUID();
    }

}

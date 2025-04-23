package com.mercadolibre.be_java_hisp_w31_g07.util;

import java.util.UUID;

public abstract class IdUtils {

    public static UUID generateId() {
        return UUID.randomUUID();
    }
}

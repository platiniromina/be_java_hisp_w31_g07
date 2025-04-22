package com.mercadolibre.be_java_hisp_w31_g07.utils;

import java.util.UUID;

public abstract class Utils {

    public static UUID generateId() {
        return UUID.randomUUID();
    }
}

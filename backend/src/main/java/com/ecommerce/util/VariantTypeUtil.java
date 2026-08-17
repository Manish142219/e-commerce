package com.ecommerce.util;

public final class VariantTypeUtil {

    public static final String CLOTHING = "CLOTHING";
    public static final String FOOTWEAR = "FOOTWEAR";
    public static final String BEAUTY = "BEAUTY";
    public static final String ACCESSORY = "ACCESSORY";

    private VariantTypeUtil() {}

    public static String labelFor(String variantType) {
        if (variantType == null) return "SELECT SIZE";
        return switch (variantType.toUpperCase()) {
            case FOOTWEAR -> "SELECT SIZE (UK)";
            case BEAUTY -> "SELECT QUANTITY / VOLUME";
            case ACCESSORY -> "SELECT OPTION";
            default -> "SELECT SIZE";
        };
    }

    public static String requiredMessage(String variantType) {
        if (variantType == null) return "Please select a size";
        return switch (variantType.toUpperCase()) {
            case FOOTWEAR -> "Please select a shoe size";
            case BEAUTY -> "Please select quantity / volume";
            case ACCESSORY -> "Please select an option";
            default -> "Please select a size";
        };
    }

    public static String chartLabel(String variantType) {
        if (variantType == null) return "SIZE CHART >";
        return switch (variantType.toUpperCase()) {
            case FOOTWEAR -> "SIZE CHART >";
            case BEAUTY -> "VOLUME GUIDE >";
            case ACCESSORY -> "DETAILS >";
            default -> "SIZE CHART >";
        };
    }
}

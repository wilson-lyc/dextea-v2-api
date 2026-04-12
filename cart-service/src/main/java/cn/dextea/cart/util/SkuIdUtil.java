package cn.dextea.cart.util;

import java.util.List;
import java.util.stream.Collectors;

public class SkuIdUtil {

    private SkuIdUtil() {}

    public static String buildSkuId(Long productId, List<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return productId + ":";
        }
        String options = optionIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return productId + ":" + options;
    }
}

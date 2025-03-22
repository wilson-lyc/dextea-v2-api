package cn.dextea.product.dto.product;

import lombok.Data;

/**
 * @author Lai Yongchao
 */
@Data
public class ProductImgDTO {
    private String key;
    private String url;
    private String name;
    private String action;

    public ProductImgDTO(String key, String name,String url,String action) {
        this.key = key;
        this.url = url;
        this.name = name;
        this.action = action;
    }
}

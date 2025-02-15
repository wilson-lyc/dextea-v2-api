package cn.dextea.product.dto;

import com.alibaba.fastjson2.JSONArray;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuBindProductDTO {
    @NotNull
    private Long typeId;
    private List<Long> productList;
}

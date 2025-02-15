package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCreateDTO;
import cn.dextea.product.dto.SearchProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    ApiResponse create(ProductCreateDTO data);
    ApiResponse getProductById(Long id);
    ApiResponse getProductListByFilter(@Valid @Min(value = 1,message = "current不能小于1") int current, @Valid @Min(value = 1,message = "size不能小于1") int size, @Valid SearchProductDTO filter);
    ResponseEntity<ApiResponse> uploadCover(Long id, MultipartFile file);
    ApiResponse getProductOption(Integer status);
}

package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCreateDTO;
import cn.dextea.product.dto.ProductQueryDTO;
import cn.dextea.product.dto.ProductUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    ApiResponse createProduct(ProductCreateDTO data);
    ApiResponse getProductBaseById(Long id);
    ResponseEntity<ApiResponse> uploadCover(Long id, MultipartFile file);
    ApiResponse getProductOption(Integer status);
    ApiResponse getProductList(int current,int size, ProductQueryDTO filter);
    ApiResponse updateProduct(Long id, @Valid ProductUpdateDTO data);
}

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
    // 管理端
    ApiResponse createProduct(ProductCreateDTO data);
    ApiResponse getProductList(int current,int size, ProductQueryDTO filter);
    ApiResponse getProductOption(Integer status);
    ApiResponse getProductBaseById(Long id);
    ApiResponse getProductImgById(Long id);
    ApiResponse updateProductBase(Long id, @Valid ProductUpdateDTO data);
    // 顾客端
    ApiResponse getProductForCustomer(Long id, Long storeId);
}

package cn.dextea.product.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.ImageModel;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.model.product.ProductCreateRequest;
import cn.dextea.product.model.product.ProductFilter;
import cn.dextea.product.model.product.ProductUpdateBaseRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.bouncycastle.dvcs.VPKCRequestBuilder;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    // 管理端
    // 创建
    DexteaApiResponse<Void> createProduct(ProductCreateRequest data);
    // 列表
    DexteaApiResponse<IPage<ProductModel>> getProductList(int current, int size, ProductFilter filter);
    DexteaApiResponse<IPage<ProductModel>> getProductList(Long storeId, int current, int size, ProductFilter filter);
    DexteaApiResponse<List<SelectOptionModel>> getProductOption();
    // 单项
    DexteaApiResponse<ProductModel> getProductBase(Long id);
    DexteaApiResponse<List<ImageModel>> getProductImg(Long id);
    DexteaApiResponse<ProductModel> getProductStatus(Long productId);
    DexteaApiResponse<ProductModel> getProductStatus(Long productId, Long storeId);
    // 更新
    DexteaApiResponse<Void> updateProductBase(Long id, ProductUpdateBaseRequest data);
    DexteaApiResponse<VPKCRequestBuilder> updateProductStatus(Long productId, Integer status);
    DexteaApiResponse<VPKCRequestBuilder> updateProductStatus(Long productId, Long storeId, Integer status);
}

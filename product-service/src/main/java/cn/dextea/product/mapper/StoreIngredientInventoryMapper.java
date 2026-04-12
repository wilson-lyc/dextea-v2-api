package cn.dextea.product.mapper;

import cn.dextea.product.entity.StoreIngredientInventoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface StoreIngredientInventoryMapper extends BaseMapper<StoreIngredientInventoryEntity> {

    @Insert("INSERT INTO store_ingredient_inventory (store_id, ingredient_id, quantity, unit, warn_threshold, last_restock_time) " +
            "VALUES (#{storeId}, #{ingredientId}, #{quantity}, #{unit}, #{warnThreshold}, NOW()) " +
            "ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), unit = VALUES(unit), warn_threshold = VALUES(warn_threshold), last_restock_time = NOW()")
    int upsertInventory(@Param("storeId") Long storeId,
                        @Param("ingredientId") Long ingredientId,
                        @Param("quantity") BigDecimal quantity,
                        @Param("unit") String unit,
                        @Param("warnThreshold") BigDecimal warnThreshold);
}

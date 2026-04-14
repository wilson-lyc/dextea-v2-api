package cn.dextea.product.mapper;

import cn.dextea.product.entity.StoreProductStatusEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StoreProductStatusMapper extends BaseMapper<StoreProductStatusEntity> {
}

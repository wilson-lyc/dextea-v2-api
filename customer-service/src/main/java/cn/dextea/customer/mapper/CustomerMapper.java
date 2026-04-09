package cn.dextea.customer.mapper;

import cn.dextea.customer.entity.CustomerEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<CustomerEntity> {
}

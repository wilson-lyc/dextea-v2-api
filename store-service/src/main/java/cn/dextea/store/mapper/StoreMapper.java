package cn.dextea.store.mapper;

import cn.dextea.store.dto.StoreOptionDTO;
import cn.dextea.store.pojo.Store;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Mapper
public interface StoreMapper extends MPJBaseMapper<Store> {
    @Select("select id as `value`,name as `label` from `s_store`")
    List<StoreOptionDTO> getSelectOptions();
}

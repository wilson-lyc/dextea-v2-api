package cn.dextea.store.mapper;

import cn.dextea.store.dto.StoreSelectOption;
import cn.dextea.store.pojo.Store;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {
    @Select("select id as `value`,name as `label` from `s_store`")
    List<StoreSelectOption> getSelectOptions();
}

package cn.dextea.product.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;

/**
 * LambdaUpdateWrapper.set() resolves column names eagerly at call time, requiring the
 * MyBatis-Plus entity metadata cache. Without a Spring context the cache is not populated,
 * so tests that exercise the update code path must call this before the first test runs.
 */
class MybatisPlusTestSupport {

    static void initTableInfo(Class<?>... entityClasses) {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        for (Class<?> entityClass : entityClasses) {
            TableInfoHelper.initTableInfo(assistant, entityClass);
        }
    }
}

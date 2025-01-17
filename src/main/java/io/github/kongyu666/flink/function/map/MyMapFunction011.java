package io.github.kongyu666.flink.function.map;

import com.alibaba.fastjson2.JSONObject;
import io.github.kongyu666.flink.entity.UserEntity;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * 自定义 MapFunction，用于将 JSON 字符串映射为 UserEntity 对象。
 */
public class MyMapFunction011 implements MapFunction<String, UserEntity> {

    /**
     * 将 JSON 字符串映射为 UserEntity 对象。
     *
     * @param str JSON 字符串
     * @return UserEntity 对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public UserEntity map(String str) throws Exception {
        // 将 JSON 字符串解析为 JSONObject，然后转换为 Java 对象 UserEntity
        return JSONObject.parseObject(str).toJavaObject(UserEntity.class);
    }
}

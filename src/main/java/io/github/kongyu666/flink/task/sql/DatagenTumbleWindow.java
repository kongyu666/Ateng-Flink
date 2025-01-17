package io.github.kongyu666.flink.task.sql;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Component;

/**
 * 生成数据计算窗口
 *
 * @author 孔余
 * @email 2385569970@qq.com
 * @date 2024-07-19 15:33:21
 */
@Component
@Slf4j
public class DatagenTumbleWindow {

    /**
     * 事件时间滚动窗口(2分钟)查询
     */
    public void run() {
        // 获取环境
        StreamExecutionEnvironment env = SpringUtil.getBean("flinkEnv", StreamExecutionEnvironment.class);
        StreamTableEnvironment tableEnv = SpringUtil.getBean("flinkTableEnv", StreamTableEnvironment.class);
        tableEnv.getConfig().set("pipeline.name", "生成数据计算窗口");
        // 创建表并设置水位线
        tableEnv.executeSql("CREATE TABLE my_user (\n" +
                "  id BIGINT NOT NULL,\n" +
                "  name STRING,\n" +
                "  age INT,\n" +
                "  score DOUBLE,\n" +
                "  birthday TIMESTAMP(3),\n" +
                "  province STRING,\n" +
                "  city STRING,\n" +
                "  event_time AS cast(CURRENT_TIMESTAMP as timestamp(3)), --事件时间\n" +
                "  proc_time AS PROCTIME(), --处理时间\n" +
                "  WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "  'connector' = 'datagen',\n" +
                "  'rows-per-second' = '1',\n" +
                "  'fields.id.min' = '1',\n" +
                "  'fields.id.max' = '100000',\n" +
                "  'fields.name.length' = '10',\n" +
                "  'fields.age.min' = '18',\n" +
                "  'fields.age.max' = '60',\n" +
                "  'fields.score.min' = '0',\n" +
                "  'fields.score.max' = '100',\n" +
                "  'fields.province.length' = '5',\n" +
                "  'fields.city.length' = '5'\n" +
                ");");
        // 事件时间滚动窗口(2分钟)查询
        Table table = tableEnv.sqlQuery("SELECT\n" +
                "  age,\n" +
                "  COUNT(name) AS cnt,\n" +
                "  TUMBLE_START(event_time, INTERVAL '1' MINUTE) AS window_start,\n" +
                "  TUMBLE_END(event_time, INTERVAL '1' MINUTE) AS window_end\n" +
                "FROM my_user\n" +
                "GROUP BY\n" +
                "  age,\n" +
                "  TUMBLE(event_time, INTERVAL '1' MINUTE);");
        table.execute().collect().forEachRemaining(row -> log.info(row.toString()));
    }

    /**
     * 处理时间滚动窗口(2分钟)查询
     */
    public void run2() {
        StreamTableEnvironment tableEnv = SpringUtil.getBean("flinkTableEnv", StreamTableEnvironment.class);
        // 创建表并设置水位线
        tableEnv.executeSql("CREATE TABLE my_user (\n" +
                "  id BIGINT NOT NULL,\n" +
                "  name STRING,\n" +
                "  age INT,\n" +
                "  score DOUBLE,\n" +
                "  birthday TIMESTAMP(3),\n" +
                "  province STRING,\n" +
                "  city STRING,\n" +
                "  event_time AS cast(CURRENT_TIMESTAMP as timestamp(3)), --事件时间\n" +
                "  proc_time AS PROCTIME(), --处理时间\n" +
                "  WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "  'connector' = 'datagen',\n" +
                "  'rows-per-second' = '1',\n" +
                "  'fields.id.min' = '1',\n" +
                "  'fields.id.max' = '100000',\n" +
                "  'fields.name.length' = '10',\n" +
                "  'fields.age.min' = '18',\n" +
                "  'fields.age.max' = '60',\n" +
                "  'fields.score.min' = '0',\n" +
                "  'fields.score.max' = '100',\n" +
                "  'fields.province.length' = '5',\n" +
                "  'fields.city.length' = '5'\n" +
                ");");
        // 处理时间滚动窗口(2分钟)查询
        Table table = tableEnv.sqlQuery("SELECT\n" +
                "  age,\n" +
                "  COUNT(name) AS cnt,\n" +
                "  TUMBLE_START(PROCTIME(), INTERVAL '2' MINUTE) AS window_start,\n" +
                "  TUMBLE_END(PROCTIME(), INTERVAL '2' MINUTE) AS window_end\n" +
                "FROM my_user\n" +
                "GROUP BY\n" +
                "  age,\n" +
                "  TUMBLE(PROCTIME(), INTERVAL '2' MINUTE);");
        // 执行表查询并打印结果
        table.execute();
        /*TableResult result = table.execute();
        result.print();*/
    }
}

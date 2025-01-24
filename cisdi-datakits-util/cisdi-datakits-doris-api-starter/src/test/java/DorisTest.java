import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xxx
 * @since 2024/6/19 10:21 PM
 */
public class DorisTest {

    public static void main(String[] args) {
        String sql = "\n" +
                "CREATE TABLE `dy_0_duplicate_all_type_partition_list` (\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NOT NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NOT NULL COMMENT 'float_type',\n" +
                "  `double_type` double NOT NULL DEFAULT \"1.2\" COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NOT NULL DEFAULT \"1.3\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "DUPLICATE KEY(`boolean_type`, `tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nDUPLICATE KEY(`boolean_type`, `tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY LIST(`tinyint_type`, `bigint_type`)\n" +
                "(PARTITION p1_tinyint VALUES IN ((\"1\", \"10\"),(\"10\", \"100\")),\n" +
                "PARTITION p2_tinyint VALUES IN ((\"2\", \"20\"),(\"20\", \"200\")),\n" +
                "PARTITION p3_tinyint VALUES IN ((\"3\", \"30\"),(\"30\", \"300\")))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_duplicate_all_type` (\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NOT NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NOT NULL COMMENT 'float_type',\n" +
                "  `double_type` double NOT NULL DEFAULT \"1.2\" COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NOT NULL DEFAULT \"1.3\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "DUPLICATE KEY(`boolean_type`, `tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nDUPLICATE KEY(`boolean_type`, `tinyint_type`, `bigint_type`)'\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_test_0_duplicate_dynamic_partition` (\n" +
                "  `k1` date NULL,\n" +
                "  `k2` int(11) NULL,\n" +
                "  `k3` smallint(6) NULL,\n" +
                "  `v1` varchar(2048) NULL,\n" +
                "  `v2` datetime NULL DEFAULT \"2014-02-04 15:36:00\"\n" +
                ") ENGINE=OLAP\n" +
                "DUPLICATE KEY(`k1`, `k2`, `k3`)\n" +
                "COMMENT 'OLAP'\n" +
                "PARTITION BY RANGE(`k1`)\n" +
                "(PARTITION p20240517 VALUES [('2024-05-17'), ('2024-05-18')),\n" +
                "PARTITION p20240518 VALUES [('2024-05-18'), ('2024-05-19')),\n" +
                "PARTITION p20240519 VALUES [('2024-05-19'), ('2024-05-20')),\n" +
                "PARTITION p20240520 VALUES [('2024-05-20'), ('2024-05-21')))\n" +
                "DISTRIBUTED BY HASH(`k2`) BUCKETS 32\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 2\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"dynamic_partition.enable\" = \"true\",\n" +
                "\"dynamic_partition.time_unit\" = \"DAY\",\n" +
                "\"dynamic_partition.time_zone\" = \"Asia/Shanghai\",\n" +
                "\"dynamic_partition.start\" = \"-3\",\n" +
                "\"dynamic_partition.end\" = \"3\",\n" +
                "\"dynamic_partition.prefix\" = \"p\",\n" +
                "\"dynamic_partition.replication_allocation\" = \"tag.location.default: 2\",\n" +
                "\"dynamic_partition.buckets\" = \"2\",\n" +
                "\"dynamic_partition.create_history_partition\" = \"false\",\n" +
                "\"dynamic_partition.history_partition_num\" = \"-1\",\n" +
                "\"dynamic_partition.hot_partition_num\" = \"0\",\n" +
                "\"dynamic_partition.reserved_history_periods\" = \"NULL\",\n" +
                "\"dynamic_partition.storage_policy\" = \"\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_duplicate_all_type_no_duplicate` (\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type)\\n test',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `largeint_type` largeint(40) NOT NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NOT NULL COMMENT 'float_type',\n" +
                "  `double_type` double NOT NULL DEFAULT \"1.2\" COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NOT NULL DEFAULT \"1.3\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "COMMENT '这是\\n备注信息\\nDUPLICATE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_aggregate_all_type` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type (\\ntest',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean REPLACE NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) SUM NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) MAX NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) REPLACE_IF_NOT_NULL NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float MIN NULL COMMENT 'float_type',\n" +
                "  `double_type` double REPLACE NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) REPLACE NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date REPLACE NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) REPLACE NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) REPLACE NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) REPLACE NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) REPLACE NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text REPLACE NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `hll_type` hll HLL_UNION NOT NULL COMMENT 'hll(hyperloglog)_type',\n" +
                "  `bitmap_type` bitmap BITMAP_UNION NOT NULL COMMENT 'bitmap_type',\n" +
                "  `quantile_state_type` quantile_state QUANTILE_UNION NOT NULL COMMENT 'quantile_state_type',\n" +
                "  `array_type` array<int(11)> REPLACE NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> REPLACE NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> REPLACE NULL COMMENT 'struct_type'," +
                "\n" +
                "  `json_type` json REPLACE NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "AGGREGATE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nAGGREGATE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_aggregate_all_type_partition_list` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'CREATE TABLE `dy_0_simple` (\\n  `k1` " +
                "tinyint(4) NULL,\\n  `k2` DECIMAL(10, 2) NULL DEFAULT \"10.5\",\\n  `k3` char(10) NULL COMMENT " +
                "\\'string column\\',\\n  `k4` int(11) NOT NULL DEFAULT \"1\" COMMENT \\'int column\\'\\n) " +
                "ENGINE=OLAP\\nDUPLICATE KEY(`k1`, `k2`, `k3`)\\nCOMMENT \\'my first table\\'\\nDISTRIBUTED BY HASH" +
                "(`k1`) BUCKETS 2\\nPROPERTIES (\\n\"replication_allocation\" = \"tag.location.default: 2\"," +
                "\\n\"is_being_synced\" = \"false\",\\n\"storage_format\" = \"V2\",\\n\"light_schema_change\" = " +
                "\"true\",\\n\"disable_auto_compaction\" = \"false\",\\n\"enable_single_replica_compaction\" = " +
                "\"false\"\\n);tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean REPLACE NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) SUM NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) MAX NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) REPLACE_IF_NOT_NULL NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float MIN NULL COMMENT 'float_type',\n" +
                "  `double_type` double REPLACE NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) REPLACE NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date REPLACE NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) REPLACE NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) REPLACE NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) REPLACE NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) REPLACE NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text REPLACE NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `hll_type` hll HLL_UNION NOT NULL COMMENT 'hll(hyperloglog)_type',\n" +
                "  `bitmap_type` bitmap BITMAP_UNION NOT NULL COMMENT 'bitmap_type',\n" +
                "  `quantile_state_type` quantile_state QUANTILE_UNION NOT NULL COMMENT 'quantile_state_type',\n" +
                "  `array_type` array<int(11)> REPLACE NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> REPLACE NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> REPLACE NULL COMMENT 'struct_type'," +
                "\n" +
                "  `json_type` json REPLACE NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "AGGREGATE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nAGGREGATE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY LIST(`tinyint_type`, `bigint_type`)\n" +
                "(PARTITION p1_tinyint VALUES IN ((\"1\", \"10\"),(\"10\", \"100\")),\n" +
                "PARTITION p2_tinyint VALUES IN ((\"2\", \"20\"),(\"20\", \"200\")),\n" +
                "PARTITION p3_tinyint VALUES IN ((\"3\", \"30\"),(\"30\", \"300\")))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_aggregate_all_type_partition_range` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type) ENGINE=OLAP',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean REPLACE NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) SUM NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) MAX NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) REPLACE_IF_NOT_NULL NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float MIN NULL COMMENT 'float_type',\n" +
                "  `double_type` double REPLACE NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) REPLACE NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date REPLACE NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) REPLACE NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) REPLACE NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) REPLACE NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) REPLACE NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text REPLACE NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `hll_type` hll HLL_UNION NOT NULL COMMENT 'hll(hyperloglog)_type',\n" +
                "  `bitmap_type` bitmap BITMAP_UNION NOT NULL COMMENT 'bitmap_type',\n" +
                "  `quantile_state_type` quantile_state QUANTILE_UNION NOT NULL COMMENT 'quantile_state_type',\n" +
                "  `array_type` array<int(11)> REPLACE NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> REPLACE NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> REPLACE NULL COMMENT 'struct_type'," +
                "\n" +
                "  `json_type` json REPLACE NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "AGGREGATE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nAGGREGATE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY RANGE(`tinyint_type`, `bigint_type`)\n" +
                "(PARTITION p11_tinyint VALUES [(\"1\", \"10\"), (\"10\", \"100\")),\n" +
                "PARTITION p12_tinyint VALUES [(\"20\", \"200\"), (\"30\", \"300\")),\n" +
                "PARTITION p13_tinyint VALUES [(\"40\", \"400\"), (\"50\", \"500\")))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_aggregate_all_type_dynamic_partition` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type\\n) ENGINE=OLAP',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `datetime_type` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `boolean_type` boolean REPLACE NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) SUM NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) MAX NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) REPLACE_IF_NOT_NULL NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float MIN NULL COMMENT 'float_type',\n" +
                "  `double_type` double REPLACE NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) REPLACE NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date REPLACE NOT NULL COMMENT 'date_type',\n" +
                "  `char_type` char(1) REPLACE NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) REPLACE NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) REPLACE NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text REPLACE NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `hll_type` hll HLL_UNION NOT NULL COMMENT 'hll(hyperloglog)_type',\n" +
                "  `bitmap_type` bitmap BITMAP_UNION NOT NULL COMMENT 'bitmap_type',\n" +
                "  `quantile_state_type` quantile_state QUANTILE_UNION NOT NULL COMMENT 'quantile_state_type',\n" +
                "  `array_type` array<int(11)> REPLACE NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> REPLACE NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> REPLACE NULL COMMENT 'struct_type'," +
                "\n" +
                "  `json_type` json REPLACE NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "AGGREGATE KEY(`tinyint_type`, `bigint_type`, `datetime_type`)\n" +
                "COMMENT '这是\\n备注信息\\nAGGREGATE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY RANGE(`datetime_type`)\n" +
                "(PARTITION p20240515 VALUES [('2024-05-15 00:00:00'), ('2024-05-16 00:00:00')),\n" +
                "PARTITION p20240516 VALUES [('2024-05-16 00:00:00'), ('2024-05-17 00:00:00')),\n" +
                "PARTITION p20240517 VALUES [('2024-05-17 00:00:00'), ('2024-05-18 00:00:00')),\n" +
                "PARTITION p20240518 VALUES [('2024-05-18 00:00:00'), ('2024-05-19 00:00:00')))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"dynamic_partition.enable\" = \"true\",\n" +
                "\"dynamic_partition.time_unit\" = \"DAY\",\n" +
                "\"dynamic_partition.time_zone\" = \"Asia/Shanghai\",\n" +
                "\"dynamic_partition.start\" = \"-3\",\n" +
                "\"dynamic_partition.end\" = \"3\",\n" +
                "\"dynamic_partition.prefix\" = \"p\",\n" +
                "\"dynamic_partition.replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"dynamic_partition.buckets\" = \"2\",\n" +
                "\"dynamic_partition.create_history_partition\" = \"false\",\n" +
                "\"dynamic_partition.history_partition_num\" = \"-1\",\n" +
                "\"dynamic_partition.hot_partition_num\" = \"0\",\n" +
                "\"dynamic_partition.reserved_history_periods\" = \"NULL\",\n" +
                "\"dynamic_partition.storage_policy\" = \"\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_unique_all_type_partition_range` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NULL COMMENT 'float_type',\n" +
                "  `double_type` double NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `bitmap_type` bitmap NOT NULL COMMENT 'bitmap_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "UNIQUE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nUNIQUE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY RANGE(`tinyint_type`, `bigint_type`)\n" +
                "(PARTITION p11_tinyint VALUES [(\"1\", \"10\"), (\"10\", \"100\")),\n" +
                "PARTITION p12_tinyint VALUES [(\"20\", \"200\"), (\"30\", \"300\")),\n" +
                "PARTITION p13_tinyint VALUES [(\"40\", \"400\"), (\"50\", \"500\")))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_unique_all_type_partition_list` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NULL COMMENT 'float_type',\n" +
                "  `double_type` double NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `bitmap_type` bitmap NOT NULL COMMENT 'bitmap_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "UNIQUE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nUNIQUE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY LIST(`tinyint_type`)\n" +
                "(PARTITION p1_tinyint VALUES IN (\"1\",\"10\"),\n" +
                "PARTITION p2_tinyint VALUES IN (\"11\",\"20\"),\n" +
                "PARTITION p3_tinyint VALUES IN (\"21\",\"30\"))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_unique_all_type_bucket_auto` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NULL COMMENT 'float_type',\n" +
                "  `double_type` double NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `bitmap_type` bitmap NOT NULL COMMENT 'bitmap_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "UNIQUE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nUNIQUE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "PARTITION BY LIST(`tinyint_type`)\n" +
                "(PARTITION p1_tinyint VALUES IN (\"1\",\"10\"),\n" +
                "PARTITION p2_tinyint VALUES IN (\"11\",\"20\"),\n" +
                "PARTITION p3_tinyint VALUES IN (\"21\",\"30\"))\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS AUTO\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `dy_0_unique_all_type` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NULL COMMENT 'float_type',\n" +
                "  `double_type` double NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `bitmap_type` bitmap NOT NULL COMMENT 'bitmap_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "UNIQUE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '这是\\n备注信息\\nUNIQUE KEY(`tinyint_type`, `bigint_type`)'\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n" +
                "\n" +
                "\n" +
                "CREATE TABLE `dy_0_unique_all_type` (\n" +
                "  `tinyint_type` tinyint(4) NOT NULL DEFAULT \"1\" COMMENT 'tinyint_type',\n" +
                "  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" COMMENT 'bigint_type',\n" +
                "  `boolean_type` boolean NOT NULL COMMENT 'boolean_type',\n" +
                "  `smallint_type` smallint(6) NOT NULL COMMENT 'smallint_type',\n" +
                "  `int_type` int(11) NOT NULL DEFAULT \"1\" COMMENT 'int_type',\n" +
                "  `largeint_type` largeint(40) NULL DEFAULT \"1\" COMMENT 'largeint_type',\n" +
                "  `float_type` float NULL COMMENT 'float_type',\n" +
                "  `double_type` double NULL COMMENT 'double_type',\n" +
                "  `decimal_type` DECIMAL(7, 3) NULL DEFAULT \"1.1\" COMMENT 'decimal_type',\n" +
                "  `date_type` date NOT NULL COMMENT 'date_type',\n" +
                "  `datetime_type` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'datetime_type',\n" +
                "  `char_type` char(1) NOT NULL COMMENT 'char_type',\n" +
                "  `varchar_type` varchar(100) NOT NULL COMMENT 'varchar_type',\n" +
                "  `varchar_all_type` varchar(*) NOT NULL COMMENT 'varchar_all_type',\n" +
                "  `string_type` text NOT NULL DEFAULT \"1\" COMMENT 'string_type',\n" +
                "  `bitmap_type` bitmap NOT NULL COMMENT 'bitmap_type',\n" +
                "  `array_type` array<int(11)> NULL COMMENT 'array_type',\n" +
                "  `map_type` MAP<text,int(11)> NULL COMMENT 'map_type',\n" +
                "  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT 'struct_type',\n" +
                "  `json_type` json NULL COMMENT 'json_type'\n" +
                ") ENGINE=OLAP\n" +
                "UNIQUE KEY(`tinyint_type`, `bigint_type`)\n" +
                "COMMENT '\\nCREATE TABLE `dy_0_unique_all_type` (\\n  `tinyint_type` tinyint(4) NOT " +
                "NULL DEFAULT \"1\" COMMENT ''tinyint_type'',\\n  `bigint_type` bigint(20) NOT NULL DEFAULT \"1\" " +
                "COMMENT ''bigint_type'',\\n  `boolean_type` boolean NOT NULL COMMENT ''boolean_type'',\\n  " +
                "`smallint_type` smallint(6) NOT NULL COMMENT ''smallint_type'',\\n  `int_type` int(11) NOT NULL " +
                "DEFAULT \"1\" COMMENT ''int_type'',\\n  `largeint_type` largeint(40) NULL DEFAULT \"1\" COMMENT " +
                "''largeint_type'',\\n  `float_type` float NULL COMMENT ''float_type'',\\n  `double_type` double NULL" +
                " COMMENT ''double_type'',\\n  `decimal_type` DECIMAL(7, 3) NULL DEFAULT \"1.1\" COMMENT " +
                "''decimal_type'',\\n  `date_type` date NOT NULL COMMENT ''date_type'',\\n  `datetime_type` datetime" +
                "(3) NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''datetime_type'',\\n  `char_type` char(1) NOT NULL " +
                "COMMENT ''char_type'',\\n  `varchar_type` varchar(100) NOT NULL COMMENT ''varchar_type'',\\n  " +
                "`varchar_all_type` varchar(*) NOT NULL COMMENT ''varchar_all_type'',\\n  `string_type` text NOT NULL" +
                " DEFAULT \"1\" COMMENT ''string_type'',\\n  `bitmap_type` bitmap NOT NULL COMMENT ''bitmap_type''," +
                "\\n  `array_type` array<int(11)> NULL COMMENT ''array_type'',\\n  `map_type` MAP<text,int(11)> NULL " +
                "COMMENT ''map_type'',\\n  `struct_type` STRUCT<s_id:int(11),s_name:text,s_address:text> NULL COMMENT" +
                " ''struct_type'',\\n  `json_type` json NULL COMMENT ''json_type''\\n) ENGINE=OLAP\\nUNIQUE KEY" +
                "(`tinyint_type`, `bigint_type`)\\nCOMMENT ''这是\\\\n备注信息\\\\nUNIQUE KEY(`tinyint_type`, " +
                "`bigint_type`)''\\nDISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\\nPROPERTIES " +
                "(\\n\"replication_allocation\" = \"tag.location.default: 1\",\\n\"is_being_synced\" = \"false\"," +
                "\\n\"storage_format\" = \"V2\",\\n\"light_schema_change\" = \"true\",\\n\"disable_auto_compaction\" " +
                "= \"false\",\\n\"enable_single_replica_compaction\" = \"false\"\\n);\\n\\n123'\n" +
                "DISTRIBUTED BY HASH(`bigint_type`) BUCKETS 1\n" +
                "PROPERTIES (\n" +
                "\"replication_allocation\" = \"tag.location.default: 1\",\n" +
                "\"is_being_synced\" = \"false\",\n" +
                "\"storage_format\" = \"V2\",\n" +
                "\"light_schema_change\" = \"true\",\n" +
                "\"disable_auto_compaction\" = \"false\",\n" +
                "\"enable_single_replica_compaction\" = \"false\"\n" +
                ");\n\n";

        String regex = "(CREATE TABLE `[^`]+` \\(.*?\\);\n)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        List<String> sqlStatements = new ArrayList<>();

        while (matcher.find()) {
            sqlStatements.add(matcher.group(1));
            sqlStatements.add("-----------------");
        }

        sqlStatements.forEach(System.out::println);
    }
}

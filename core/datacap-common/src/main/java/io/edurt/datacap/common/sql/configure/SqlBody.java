package io.edurt.datacap.common.sql.configure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SqlBody
{
    private String database;
    private String table;
    private List<SqlColumn> columns;
    private List<SqlColumn> orders;
    private Integer limit;
    private Integer offset;
    private String where;
    private SqlType type;
}

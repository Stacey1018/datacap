package io.edurt.datacap.common.sql.configure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SqlColumn
{
    private String column;
    private SqlOrder order;
}

package databaseconnector.impl;

import com.mendix.systemwideinterfaces.core.meta.IMetaPrimitive.PrimitiveType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ResultSetReader converts a given instance of {@link ResultSet} into a list of instances of Map<String, Object>, with key for column name
 * and value for column value.
 */
public class ResultSetReader {
  private final ResultSetIterator rsIter;
  private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  public ResultSetReader(final ResultSet resultSet, final Map<String, PrimitiveType> columnsTypes) {
    this.rsIter = new ResultSetIterator(resultSet, columnsTypes);
  }

  /**
   * Read all records into list of maps.
   *
   * @return list of records, where records are represented as map
   * @throws SQLException
   */
  public List<Map<String, Object>> readAll() throws SQLException {
    // Force the stream to read the whole ResultSet, so that the connection can be closed.
    // As Collectors.toMap does not accept null for a value, we have to explicitly convert the Optional back to its value.
    return rsIter.stream().map(rs -> getRowResult(rs)).map(m -> {
      final Map<String, Object> record = new HashMap<>();
      for (Map.Entry<String, Optional<Object>> entry : m.entrySet()) {
        record.put(entry.getKey(), entry.getValue().orElse(null));
      }
      return record;
    }).collect(Collectors.toList());
  }

  /**
   * The Optional type for value is used because Collectors.toMap does not accept null for a value.
   */
  private Map<String, Optional<Object>> getRowResult(final ResultSet rs) {
    return rsIter.getColumnInfos().collect(Collectors.toMap(ColumnInfo::getName, curryGetColumnResult(rs)));
  }

  private Function<ColumnInfo, Optional<Object>> curryGetColumnResult(final ResultSet rs) {
    return ci -> getColumnResult(rs, ci);
  }

  private Optional<Object> getColumnResult(final ResultSet rs, final ColumnInfo columnInfo) {
    try {
      final int columnIndex = columnInfo.getIndex();
      Object columnValue = null;
      switch (columnInfo.getType()) {
        case Integer:
          columnValue = rs.getInt(columnIndex);
          break;
        case AutoNumber:
        case Long:
          columnValue = rs.getLong(columnIndex);
          break;
        case DateTime:
          Timestamp timeStamp = rs.getTimestamp(columnIndex, calendar);
          columnValue = (timeStamp != null) ? new Date(timeStamp.getTime()) : null;
          break;
        case Boolean:
          columnValue = rs.getBoolean(columnIndex);
          break;
        case Decimal:
          columnValue = rs.getBigDecimal(columnIndex);
          break;
        case Float:
        case Currency:
          columnValue = rs.getDouble(columnIndex);
          break;
        case HashString:
        case Enum:
        case String:
          columnValue = rs.getString(columnIndex);
          break;
        case Binary:
          columnValue = rs.getBytes(columnIndex);
          break;
      }
      return rs.wasNull() ? Optional.empty() : Optional.ofNullable(columnValue);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}

package com.qtrj.simpleframework.ssh.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IResultSetCall<T> {
  T invoke(ResultSet paramResultSet) throws SQLException;
}

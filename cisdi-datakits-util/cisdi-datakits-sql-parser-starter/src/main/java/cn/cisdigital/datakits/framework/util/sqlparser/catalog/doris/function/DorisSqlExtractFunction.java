/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.function;

import org.apache.calcite.avatica.util.TimeUnitRange;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.validate.SqlMonotonicity;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.util.Util;

import static org.apache.calcite.sql.type.OperandTypes.family;
import static org.apache.calcite.sql.validate.SqlNonNullableAccessors.getOperandLiteralValueOrThrow;

/**
 * 支持doris函数
 * INT extract(unit FROM DATETIME)
 */
public class DorisSqlExtractFunction extends SqlFunction {
  //~ Constructors -----------------------------------------------------------

  // SQL2003, Part 2, Section 4.4.3 - extract returns a exact numeric
  // TODO: Return type should be decimal for seconds
  public DorisSqlExtractFunction(String name) {
    super(name, SqlKind.OTHER_FUNCTION, ReturnTypes.BIGINT_NULLABLE, null,
            family(SqlTypeFamily.INTERVAL_YEAR_MONTH, SqlTypeFamily.STRING).or(family(SqlTypeFamily.INTERVAL_DAY_TIME, SqlTypeFamily.STRING)),
        SqlFunctionCategory.USER_DEFINED_FUNCTION);
  }

  //~ Methods ----------------------------------------------------------------

  @Override public String getSignatureTemplate(int operandsCount) {
    Util.discard(operandsCount);
    return "{0}({1} FROM {2})";
  }

  @Override public void unparse(
      SqlWriter writer,
      SqlCall call,
      int leftPrec,
      int rightPrec) {
    final SqlWriter.Frame frame = writer.startFunCall(getName());
    SqlIntervalQualifier.asIdentifier(call.operand(0))
        .unparse(writer, 0, 0);
    writer.sep("FROM");
    call.operand(1).unparse(writer, 0, 0);
    writer.endFunCall(frame);
  }

  @Override public void validateCall(SqlCall call, SqlValidator validator,
      SqlValidatorScope scope, SqlValidatorScope operandScope) {
    super.validateCall(call, validator, scope, operandScope);

    // This is either a time unit or a time frame:
    //
    //  * In "EXTRACT(YEAR FROM x)" operand 0 is a SqlIntervalQualifier with
    //    startUnit = YEAR and timeFrameName = null.
    //
    //  * In "EXTRACT(MINUTE15 FROM x)" operand 0 is a SqlIntervalQualifier with
    //    startUnit = EPOCH and timeFrameName = 'MINUTE15'.
    //
    // If the latter, check that timeFrameName is valid.
    validator.validateTimeFrame(
        (SqlIntervalQualifier) call.getOperandList().get(0));
  }

  @Override public SqlMonotonicity getMonotonicity(SqlOperatorBinding call) {
    TimeUnitRange value = getOperandLiteralValueOrThrow(call, 0, TimeUnitRange.class);
    switch (value) {
    case YEAR:
      return call.getOperandMonotonicity(1).unstrict();
    default:
      return SqlMonotonicity.NOT_MONOTONIC;
    }
  }
}

/* Generated By:JavaCC: Do not edit this line. ParseDorisIndexConstants.java */
package cn.cisdigital.datakits.framework.util.sqlparser.javacc.ext.doris;

public interface ParseDorisIndexConstants {

  int EOF = 0;
  int SINGLE_QUOTED_IDENTIFIER = 6;
  int BACK_QUOTED_IDENTIFIER = 7;
  int QUOTED_IDENTIFIER = 8;
  int INDEX = 9;
  int USING = 10;
  int COMMENT = 11;
  int PROPERTIES = 12;
  int LPAREN = 13;
  int RPAREN = 14;
  int EQ = 15;
  int COMMA = 16;
  int NORMAL_NAME = 17;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<SINGLE_QUOTED_IDENTIFIER>",
    "<BACK_QUOTED_IDENTIFIER>",
    "<QUOTED_IDENTIFIER>",
    "\"INDEX\"",
    "\"USING\"",
    "\"COMMENT\"",
    "\"PROPERTIES\"",
    "\"(\"",
    "\")\"",
    "\"=\"",
    "\",\"",
    "<NORMAL_NAME>",
  };

}

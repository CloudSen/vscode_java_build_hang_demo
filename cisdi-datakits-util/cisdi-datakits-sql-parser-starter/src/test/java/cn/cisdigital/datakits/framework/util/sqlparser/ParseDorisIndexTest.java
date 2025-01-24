package cn.cisdigital.datakits.framework.util.sqlparser;

import cn.cisdigital.datakits.framework.model.dto.database.IndexDto;
import cn.cisdigital.datakits.framework.util.sqlparser.javacc.ext.doris.ParseDorisIndex;
import cn.cisdigital.datakits.framework.util.sqlparser.javacc.ext.doris.ParseException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xxx
 * @since 2024/9/4 11:30
 */
 class ParseDorisIndexTest {

    @Test
     void testParse() throws ParseException {
        try (StringReader input = new StringReader(noComment())) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            final List<IndexDto> indexDtos = parseDorisIndex.parseIndexs();
            assertNotNull(indexDtos);
        }
    }

    @Test
    void testParse1() throws ParseException {
        try (StringReader input = new StringReader(noComment())) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            final List<IndexDto> indexDtos = parseDorisIndex.parseIndexs();
            assertNotNull(indexDtos);
        }
    }

    @Test
    void testParse2() throws ParseException {
        try (StringReader input = new StringReader(mulityIndexs())) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            final List<IndexDto> indexDtos = parseDorisIndex.parseIndexs();
            assertNotNull(indexDtos);
        }
    }

    @Test
    void testParse3() throws ParseException {
        try (StringReader input = new StringReader(getSql1())) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            final List<IndexDto> indexDtos = parseDorisIndex.parseIndexs();
            assertNotNull(indexDtos);
        }
    }

    @Test
    void testParse4() throws ParseException {
        try (StringReader input = new StringReader(mulityColumn())) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            final List<IndexDto> indexDtos = parseDorisIndex.parseIndexs();
            assertNotNull(indexDtos);
        }
    }

    @Test
    void testParse5() throws ParseException {
        try (StringReader input = new StringReader(emptyComment())) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            final List<IndexDto> indexDtos = parseDorisIndex.parseIndexs();
            assertNotNull(indexDtos);
        }
    }

    private static String noComment(){
        return "INDEX idx_id (`id`) USING INVERTED,INDEX idx_etl_time (`etl_time`) USING INVERTED";
    }

    private static String mulityIndexs() {
        return "INDEX USING (`comment`,asdf) USING INVERTED PROPERTIES(\"parser\" = \"chinese\", \"parser_mode\" = \"coarse_grained\", \"support_phrase\" = \"true\") COMMENT 'inverted INDEX for comment',\n" +
                "INDEX INDEX (`df`,`dfadf`,`INDEX`,INDEX) USING INVERTED COMMENT 'inverted index for \n comment'";
    }

    private static String getSql1() {
        return "INDEX USING (`comment`,asdf) USING INVERTED PROPERTIES(\"parser\" = \"chinese\", \"parser_mode\" = \"coarse_grained\", \"support_phrase\" = \"true\") COMMENT 'inverted INDEX for comment'";
    }

    private static String mulityColumn() {
        return "INDEX INDEX (`df`,`dfadf`,`INDEX`,INDEX) USING INVERTED COMMENT 'inverted index for \n comment'";
    }

    private static String emptyComment() {
        return "INDEX INDEX (`comment`) USING INVERTED COMMENT ''";
    }
}

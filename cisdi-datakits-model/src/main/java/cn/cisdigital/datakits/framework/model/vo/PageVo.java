package cn.cisdigital.datakits.framework.model.vo;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * 新项目不要用！
 * @param <T> 模板
 * @author xxx
 * @since 2018/11/20 15:19
 */
@Getter
@ToString
@Deprecated
public class PageVo<T> implements Serializable {

    private static final long serialVersionUID = -5733076241051998142L;

    /**
     * 当前页数
     *
     * @mock 1
     */
    private Integer currPage;

    /**
     * 当前页大小
     *
     * @mock 10
     */
    private Integer pageSize;

    /**
     * 总页数
     *
     * @mock 10
     */
    private Integer totalPage;

    /**
     * 总条数
     *
     * @mock 100
     */
    private Integer total;

    /**
     * 数据列表
     */
    private List<T> list; // NOSONAR

    public PageVo() {
    }

    public PageVo(Integer currPage, Integer pageSize) {
        setCurrPage(currPage);
        setPageSize(pageSize);
    }

    public static <T> PageVo<T> from(PageVo pageVo) { // NOSONAR
        PageVo<T> para = new PageVo<>();
        para.setCurrPage(pageVo.getCurrPage());
        para.setPageSize(pageVo.getPageSize());
        para.setTotal(pageVo.getTotal());
        return para;
    }

    public static <T> PageVo<T> from(PageVo pageVo, List<T> data) { // NOSONAR
        PageVo<T> res = from(pageVo);
        res.setList(data);
        return res;
    }

    private void setCurrPage(Integer currPage) {
        this.currPage = currPage == null || currPage < 1 ? 1 : currPage;
    }

    private void setPageSize(Integer pageSize) {
        this.pageSize = pageSize == null || pageSize < 1 ? 1 : pageSize;
    }

    private void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public void setTotal(Integer total) {
        if (total == null || total < 1) {
            total = 0;
        }
        int totalPage = total / pageSize;
        if (total % pageSize > 0) {
            totalPage++;
        }
        setTotalPage(totalPage);
        this.total = total;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public static <T> PageVo<T> empty(Integer currPage, Integer pageSize) {
        PageVo<T> para = new PageVo<>();
        para.setCurrPage(currPage);
        para.setPageSize(pageSize);
        para.setTotal(0);
        para.setList(Collections.emptyList());
        return para;
    }

}

package com.slwer.cloud.mall.practice.cartorder.util;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

public class PageUtils {
    /**
     * 将 PageInfo 对象泛型中的 PO 对象转化为 VO 对象
     *
     * @param pageInfoPO PageInfo<PO>对象</>
     * @param <P>        PO类型
     * @param <V>        VO类型
     */
    public static <P, V> PageInfo<V> pageInfo2PageInfoVO(PageInfo<P> pageInfoPO) {
        // 创建 Page 对象，实际上是一个 ArrayList 类型的集合
        Page<V> page = new Page<>(pageInfoPO.getPageNum(), pageInfoPO.getPageSize());
        page.setTotal(pageInfoPO.getTotal());
        return new PageInfo<>(page);
    }
}

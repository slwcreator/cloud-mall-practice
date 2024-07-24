package com.slwer.cloud.mall.practice.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.slwer.cloud.mall.practice.categoryproduct.model.dao.CategoryMapper;
import com.slwer.cloud.mall.practice.categoryproduct.model.pojo.Category;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.request.UpdateCategoryReq;
import com.slwer.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.slwer.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallException;
import com.slwer.cloud.mall.practice.common.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if (categoryOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(UpdateCategoryReq updateCategoryReq) {
        if (updateCategoryReq.getName() != null) {
            Category categoryOld = categoryMapper.selectByName(updateCategoryReq.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategoryReq.getId())) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
        }

        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);
        category.setUpdateTime(new Date());
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if (count > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if (categoryOld == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }

        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo<Category> listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        return new PageInfo<>(categoryList);
    }

    /**
     * 递归查询得到分类目录数据（针对前台的）
     */
    @Override
    public List<CategoryVO> listForCustomer(Integer parentId) {
        //定义一个 List，这个 List 就用来存放最终的查询结果；即这个 List 中的直接元素是：所有的 parent_id=0，即type=1的，第1级别的目录；
        List<CategoryVO> categoryVOList = new ArrayList<>();

        /*
        额外创建 recursivelyFindCategories() 方法，去实现递归查询的逻辑；
        第一次递归查询时，是先查一级目录；（而一级目录的 parentId 是0）
        该方法第一个参数是：List<CategoryVO> categoryVOList：用来存放当前级别对应的，所有的下一级目录数据；
        PS：对于【最终返回给前端的 List<CategoryVO> categoryVOList】来说，其所谓的下一级目录就是：所有的 parent_id=0，即 type=1 的，第1级别的目录；
        PS：对于【所有的 parent_id=0，即 type=1 的，第1级别的目录；】来说，其 categoryVOList 就是【List<CategoryVO> childCategory属性】，
        其是用来存放该级别对应的所有的 parent_id=1，即 type=2 的，第2级别的目录；
        PS：对于【所有的 parent_id=1，即 type=2 的，第2级别的目录；】来说，其 categoryVOList 就是【List<CategoryVO> childCategory属性】，
        其是用来存放该级别对应的所有的 parent_id=2，即 type=3 的，第3级别的目录；
        该方法的第二个参数是：当前级别目录的 parent_id，即也就是当前级别的上一级目录的id；
        即，第一个参数是【上一级别的List<CategoryVO> categoryVOList】；第二参数是【下一级别的 parent_id，也就是当前级别的id】；
        */
        recursivelyFindCategories(categoryVOList, parentId);
        return categoryVOList;
    }

    /**
     * 递归查询分类目录数据的具体逻辑；其实就是，递归获取所有目录分类和子目录分类，并组合称为一个“目录树”；
     *
     * @param categoryVOList 存放所有下级别分类目录的数据
     * @param parentId       某级分类目录的 parentId
     */
    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId) {
        //首先，根据 parentId，查询出所有该级别的数据；（比如，第一次查询的是 parent_id=0，即 type=1 的，第1级别的目录）
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        //然后，遍历上面查询的该级别的数据；去尝试查询该级别数据的下一级别数据；
        if (!CollectionUtils.isEmpty(categoryList)) {
            //遍历所有查到的当前级别数据，把其放在对应上级目录的【List<CategoryVO> categoryVOList】中；
            for (Category category : categoryList) {
                //获取到【上面查询的该级别数据中的一条数据】，把其存储到上级目录的 List<CategoryVO> childCategory 属性中；
                //自然，如果该级别是【parent_id=0，即type=1的，第1级别的目录】，就是把其存储在最顶级的、返回给前端的那个 List<CategoryVO> categoryVOS 中；
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);

                //然后，这一步是关键：针对【每一个当前级别的目录数据】去递归调用 recursivelyFindCategories()方法；
                //自然，第一个参数是【当前级别数据的 List<CategoryVO> childCategory 属性】：这是存放所有下级别目录数据的；
                //第二个参数是【当前级别数据的id】：这自然是下级别目录数据的 parent_id；
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}

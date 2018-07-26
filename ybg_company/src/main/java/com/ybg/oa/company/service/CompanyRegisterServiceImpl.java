package com.ybg.oa.company.service;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.ybg.oa.company.dao.CompanyRegisterDao;
import com.ybg.oa.company.domain.CompanyRegisterDO;
import com.ybg.oa.company.domain.CompanyRegisterVO;
import com.ybg.oa.company.qvo.CompanyRegisterQuery;
import java.util.List;
import java.util.Map;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.util.Page;

@Repository
public class CompanyRegisterServiceImpl implements CompanyRegisterService {
	
	@Autowired
	private CompanyRegisterDao companyRegisterDao;
	
	@Override
	/** 返回主键的创建
	 * 
	 * @throws Exception **/
	public CompanyRegisterVO save(CompanyRegisterVO bean) throws Exception {
		return companyRegisterDao.save(bean);
	}
	
	/** 更新数据，条件 和 需要更新的字段都不能为空 不限个数个条件
	 * 
	 * @author Deament
	 * @param updatemap
	 *            需要更新的字段和值
	 * @param wheremap
	 *            更新中的条件字段和值
	 * @param table_name
	 *            表的名称 **/
	@Override
	public void update(BaseMap<String, Object> updatemap, BaseMap<String, Object> wheremap) {
		companyRegisterDao.update(updatemap, wheremap);
	}
	
	/** 分页查询 **/
	@Override
	public Page list(Page page, CompanyRegisterQuery qvo) throws Exception {
		return companyRegisterDao.list(page, qvo);
	}
	
	/** 不分页查询 **/
	@Override
	public List<CompanyRegisterVO> list(CompanyRegisterQuery qvo) throws Exception {
		return companyRegisterDao.list(qvo);
	}
	
	/** 根据条件删除 **/
	public void remove(BaseMap<String, Object> wheremap) {
		companyRegisterDao.remove(wheremap);
	}
	
	public CompanyRegisterVO get(String id) {
		return companyRegisterDao.get(id);
	}
}

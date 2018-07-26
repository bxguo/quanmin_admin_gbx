package com.ybg.gen.service.impl;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ybg.base.util.Page;
import com.ybg.gen.dao.SysGeneratorDao;
import com.ybg.gen.qvo.GeneratorQuery;
import com.ybg.gen.service.SysGeneratorService;
import com.ybg.gen.utils.GenUtils;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Service("sysGeneratorService")
public class SysGeneratorServiceImpl implements SysGeneratorService {
	
	@Autowired
	private SysGeneratorDao sysGeneratorDao;
	
	@Override
	public Page list(Page page, GeneratorQuery qvo) throws Exception {
		
		return sysGeneratorDao.list(page, qvo);
	}
	
	@Override
	public Map<String, String> queryTable(String tableName) {
		return sysGeneratorDao.queryTable(tableName);
	}
	
	@Override
	public List<Map<String, String>> queryColumns(String tableName) {
		return sysGeneratorDao.queryColumns(tableName);
	}
	
	@Override
	public byte[] generatorCode(String[] tableNames) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);
		for (String tableName : tableNames) {
			// 查询表信息
			Map<String, String> table = queryTable(tableName);
			// 查询列信息
			List<Map<String, String>> columns = queryColumns(tableName);
			// 生成代码
			try {
				GenUtils.generatorCode(table, columns, zip);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}

	
}

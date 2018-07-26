package com.ybg.gen.utils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import com.ybg.base.jdbc.util.DateUtil;
import com.ybg.gen.entity.ColumnEntity;
import com.ybg.gen.entity.TableEntity;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** 代码生成器 工具类
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午11:40:24 */
public class GenUtils {
	
	public static List<String> getTemplates() {
		List<String> templates = new ArrayList<String>();
		templates.add("template/DO.java.vm");
		templates.add("template/Dao.java.vm");
		templates.add("template/DaoImpl.java.vm");
		templates.add("template/Service.java.vm");
		templates.add("template/ServiceImpl.java.vm");
		templates.add("template/Controller.java.vm");
		templates.add("template/list.html.vm");
		templates.add("template/list.js.vm");
		templates.add("template/menu.sql.vm");
		templates.add("template/VO.java.vm");
		templates.add("template/Query.java.vm");
		templates.add("template/Mapper.java.vm");
		return templates;
	}
	
	/** 生成代码
	 * 
	 * @throws Exception */
	public static void generatorCode(Map<String, String> table, List<Map<String, String>> columns, ZipOutputStream zip) throws Exception {
		// 配置信息
		Configuration config = getConfig();
		// 表信息
		TableEntity tableEntity = new TableEntity();
		tableEntity.setTableName(table.get("tableName"));
		tableEntity.setComments(table.get("tableComment"));
		// 表名转换成Java类名
		String className = tableToJava(tableEntity.getTableName(), config.getString("tablePrefix"));
		tableEntity.setClassName(className);
		tableEntity.setClassname(StringUtils.uncapitalize(className));
		// 列信息
		List<ColumnEntity> columsList = new ArrayList<>();
		for (Map<String, String> column : columns) {
			ColumnEntity columnEntity = new ColumnEntity();
			columnEntity.setColumnName(column.get("columnName"));
			columnEntity.setDataType(column.get("dataType"));
			columnEntity.setComments(column.get("columnComment"));
			columnEntity.setExtra(column.get("extra"));
			// 列名转换成Java属性名
			String attrName = columnToJava(columnEntity.getColumnName());
			columnEntity.setAttrName(attrName);
			columnEntity.setAttrname(StringUtils.uncapitalize(attrName));
			// 列的数据类型，转换成Java类型
			String attrType = config.getString(columnEntity.getDataType(), "unknowType");
			columnEntity.setAttrType(attrType);
			// 是否主键
			if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
				tableEntity.setPk(columnEntity);
			}
			columsList.add(columnEntity);
		}
		tableEntity.setColumns(columsList);
		// 没主键，则第一个字段为主键
		if (tableEntity.getPk() == null) {
			tableEntity.setPk(tableEntity.getColumns().get(0));
		}
		// 设置velocity资源加载器
		Properties prop = new Properties();
		prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(prop);
		// 封装模板数据
		Map<String, Object> map = new HashMap<>();
		map.put("tableName", tableEntity.getTableName());
		map.put("comments", tableEntity.getComments());
		map.put("pk", tableEntity.getPk());
		map.put("className", tableEntity.getClassName());
		map.put("classname", tableEntity.getClassname());
		map.put("pathName", config.getString("pathName")+  tableEntity.getClassname().toLowerCase()+"_do/");
		map.put("columns", tableEntity.getColumns());
		map.put("package", config.getString("package"));
		map.put("author", config.getString("author"));
		map.put("email", config.getString("email"));
		map.put("datetime", DateUtil.getDate());
		VelocityContext context = new VelocityContext(map);
		// 获取模板列表
		List<String> templates = getTemplates();
		for (String template : templates) {
			// 渲染模板
			StringWriter sw = new StringWriter();
			Template tpl = Velocity.getTemplate(template, "UTF-8");
			tpl.merge(context, sw);
			try {
				// 添加到zip
				zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), config.getString("package"))));
				IOUtils.write(sw.toString(), zip, "UTF-8");
				IOUtils.closeQuietly(sw);
				zip.closeEntry();
			} catch (IOException e) {
				throw new Exception("渲染模板失败，表名：" + tableEntity.getTableName(), e);
			}
		}
	}
	
	/** 列名转换成Java属性名 */
	public static String columnToJava(String columnName) {
		return WordUtils.capitalizeFully(columnName, new char[] { '_' }).replace("_", "");
	}
	
	/** 表名转换成Java类名 */
	public static String tableToJava(String tableName, String tablePrefix) {
		if (StringUtils.isNotBlank(tablePrefix)) {
			tableName = tableName.replace(tablePrefix, "");
		}
		return columnToJava(tableName);
	}
	
	/** 获取配置信息
	 * 
	 * @throws Exception */
	public static Configuration getConfig() throws Exception {
		try {
			return new PropertiesConfiguration("generator.properties");
		} catch (ConfigurationException e) {
			throw new Exception("获取配置文件失败，", e);
		}
	}
	
	/** 获取文件名 */
	public static String getFileName(String template, String className, String packageName) {
		String packagePath = "main" + File.separator + "java" + File.separator;
		if (StringUtils.isNotBlank(packageName)) {
			packagePath += packageName.replace(".", File.separator) + File.separator;
		}
		if (template.contains("DO.java.vm")) {
			return packagePath + "domain" + File.separator + className + "DO.java";
		}
		if (template.contains("VO.java.vm")) {
			return packagePath + "domain" + File.separator + className + "VO.java";
		}
		if (template.contains("Query.java.vm")) {
			return packagePath + "qvo" + File.separator + className + "Query.java";
		}
		if (template.contains("Dao.java.vm")) {
			return packagePath + "dao" + File.separator + className + "Dao.java";
		}
		if (template.contains("DaoImpl.java.vm")) {
			return packagePath + "dao" + File.separator + className + "DaoImpl.java";
		}
		if (template.contains("Mapper.java.vm")) {
			return packagePath + "mapper" + File.separator + className + "Mapper.java";
		}
		
//		if (template.contains("Dao.xml.vm")) {
//			return packagePath + "dao" + File.separator + className + "Dao.xml";
//		}
		if (template.contains("Service.java.vm")) {
			return packagePath + "service" + File.separator + className + "Service.java";
		}
		if (template.contains("ServiceImpl.java.vm")) {
			return packagePath + "service" + File.separator + className + "ServiceImpl.java";
		}
		if (template.contains("Controller.java.vm")) {
			return packagePath + "controller" + File.separator + className + "Controller.java";
		}
		if (template.contains("list.html.vm")) {
			return "main" + File.separator + "webapp" + File.separator + "WEB-INF" + File.separator + "page" + File.separator + "generator" + File.separator + className.toLowerCase() + ".html";
		}
		if (template.contains("list.js.vm")) {
			return "main" + File.separator + "webapp" + File.separator + "js" + File.separator + "generator" + File.separator + className.toLowerCase() + ".js";
		}
		if (template.contains("menu.sql.vm")) {
			return className.toLowerCase() + "_menu.sql";
		}
		return null;
	}
}

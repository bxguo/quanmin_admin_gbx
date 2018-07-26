package com.ybg.gen.controller;
import com.ybg.base.util.Page;
import com.ybg.gen.qvo.GeneratorQuery;
import com.ybg.gen.service.SysGeneratorService;
import com.ybg.gen.utils.xss.XssHttpServletRequestWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 代码生成器
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午9:12:58 */
@Api(tags="代码生成器")
@Controller
@RequestMapping("/sys/generator_do/")
public class SysGeneratorController {
	
	@Autowired
	private SysGeneratorService sysGeneratorService;
	
	@ApiOperation(value = "代码生成器首页", notes = "", produces = MediaType.TEXT_HTML_VALUE)
	@RequestMapping(value = { "index.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String index() {
		return "/gen/index";
	}
	
	@ApiOperation(value = "代码生成器列表", notes = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams({ @ApiImplicitParam(name = "pageNow", value = "当前页数", required = true, dataType = "Integer"), @ApiImplicitParam(name = "qvo", value = "查询页数", required = false, dataType = "GeneratorQuery") })
	@ResponseBody
	@RequestMapping(value = { "list.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public Page list(@ModelAttribute GeneratorQuery qvo, @RequestParam(name = "pageNow", required = false, defaultValue = "0") Integer pageNow, ModelMap map) throws Exception {
		Page page = new Page();
		page.setCurPage(pageNow);
		page = sysGeneratorService.list(page, qvo);
		page.init();
		return page;
	}
	
	/** 生成代码 */
	@ApiOperation(value = "生成代码", notes = "只能页面传输，参数是tables 用英文逗号分隔", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = { "code.do" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void code(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String[] tableNames = new String[] {};
		// 获取表名，不进行xss过滤
		HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);
		String tables = orgRequest.getParameter("tables");
		tableNames = tables.split(",");
		byte[] data = sysGeneratorService.generatorCode(tableNames);
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"gencode.zip\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream; charset=UTF-8");
		IOUtils.write(data, response.getOutputStream());
	}
}

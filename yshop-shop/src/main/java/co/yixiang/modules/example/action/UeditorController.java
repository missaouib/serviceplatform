package co.yixiang.modules.example.action;

import co.yixiang.annotation.AnonymousAccess;
import co.yixiang.modules.baidu.ueditor.ActionEnter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@Slf4j
public class UeditorController {

	@RequestMapping("/")
	public String index(){
		return "ueditor";
	}

	@Autowired
	private ActionEnter actionEnter;

	@ResponseBody
	@RequestMapping("/ueditor/exec")
	@AnonymousAccess
	@CrossOrigin(origins = "*",maxAge = 3600)
	public String exe(HttpServletRequest request, HttpServletResponse response){
		log.info("in /ueditor/exec");
		String  result = actionEnter.exec(request);
		log.info(result);
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Credentials","true");
		return result;
	}



}

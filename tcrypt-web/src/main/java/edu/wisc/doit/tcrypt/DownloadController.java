package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FileInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloadController {
	@RequestMapping("/download")
	public void downloadKey(@RequestParam("filePath") String filePath, HttpServletRequest request, HttpServletResponse response) throws Exception {
		File file = new File(filePath);
	    response.setContentType(new MimetypesFileTypeMap().getContentType(file));
	    response.setContentLength((int)file.length());
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	    FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
	}
}

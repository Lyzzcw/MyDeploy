package com.zc.main;

import java.io.File;

import com.zc.process.LinuxProcess;
import com.zc.process.WindowsProcess;
import com.zc.ssh.SSHUtil;
import com.zc.util.Cnst;
import com.zc.util.ProjectInfoPropertyUtil;

public class Main {
	
	public static String tempFilePath = "";
	
	/**
	 * 1、停服务
	 * 2、对比服务器与本地jar文件差异，如果有差异，就上传
	 * 3、生成本地缓存文件夹，生成META-INF文件夹以及MANIFEST.MF文件
	 * 4、编译class文件到缓存目录，并与步骤2中的文件打包到入口的jar里
	 * 5、上传更新后的入口jar
	 * 6、启动服务
	 * 7、断开
	 * 8、清理本地缓存
	 * @param: @param args  需要传入配置文件的全路径    
	 */
	public static void main(String[] args) {
		try {
			long t1 = System.currentTimeMillis();
			if (args!=null&&args.length==2) {
				ProjectInfoPropertyUtil.filepath = args[1];
				ProjectInfoPropertyUtil.loadProps();
				Cnst.systemType = args[0];
				if("l".equals(Cnst.systemType)){
					SSHUtil.charset = "UTF-8";
					Cnst.log("本次更新linux服务器，请核对，5秒后开始执行：\r\n"
							+ "项目为："+Cnst.project+"\r\n"
							+ "服务器项目路径："+Cnst.rootPath+"\r\n"
							+ "本地项目路径："+Cnst.localProjectPath+"\r\n"
							+ "本地jar包路径："+Cnst.localJarPath);
					Thread.sleep(5000);
					LinuxProcess.start();
				}else if("w".equals(Cnst.systemType)){
					SSHUtil.charset = "GBK";
					Cnst.log("本次更新windows服务器，请核对，5秒后开始执行：\r\n"
							+ "项目为："+Cnst.project+"\r\n"
							+ "服务器项目路径："+Cnst.demoServerDiskPath+"\r\n"
							+ "本地项目路径："+Cnst.localProjectPath+"\r\n"
							+ "本地jar包路径："+Cnst.localJarPath);
					Thread.sleep(5000);
					WindowsProcess.start();
				}else{
					existSystem("系统参数有误，请检查，5秒后退出！");
				}
				int t = (int) ((System.currentTimeMillis()-t1)/1000);
				Cnst.log("用时"+t+"s，所有操作完成，即将退出...");
			}else{
				existSystem("项目启动参数有误，请检查，5秒后退出！");
			}
		} catch (Exception e) {
			existSystem("main error ! five seconds exist!");
		}
	}
	
	public static void existSystem(String msg){
		try {
			Cnst.log(msg);
			Thread.sleep(5000);
		} catch (Exception e) {
			Cnst.log("fucking error!");
		}
		Cnst.deleteTempPath(new File(tempFilePath));
		System.exit(-1);
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

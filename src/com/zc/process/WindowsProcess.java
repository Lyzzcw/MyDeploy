package com.zc.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zc.main.Main;
import com.zc.ssh.SSHUtil;
import com.zc.util.Cnst;
import com.zc.zip.FileUtil;

public class WindowsProcess {
	
	
	public static void start(){
		Cnst.log("windows流程开始执行");
		SSHUtil ssh = null;
		try {
			ssh = new SSHUtil(Cnst.wuname, Cnst.wpsd, Cnst.wip, Cnst.wport);
			ssh.connect();
			//1、
			stopService(ssh);
			//2、
			checkJars();
			//3、
			createTempFile();
			//4、
			Cnst.log("编译+打包...");
			copyAndZip();
			//5、
			uploadFile();
			//8、
			Cnst.log("正在清理本地缓存...");
			Cnst.deleteTempPath(new File(Main.tempFilePath));
			Cnst.log("本地缓存清理完成");
			//7、
			startService(ssh);
		} catch (Exception e) {
			e.printStackTrace();
			Main.existSystem(Cnst.unknowError);
		}finally{
			if (ssh!=null) {
				try {
					ssh.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//1、停服务
	public static void stopService(SSHUtil ssh){
		try {
			List<String> result = ssh.execCmd("netstat -ano");
			String pid = null;
			if (result!=null&&result.size()>0) {
				for(String s:result){
					if (s!=null&&s.length()>10) {
						String temp = Cnst.toConvertStr(s);
						String[] arr = temp.split(" ");
						if (arr!=null&&arr.length>0) {
							for(String sss:arr){
								if (("0.0.0.0:"+Cnst.minaport).equals(sss)) {
									pid = arr[arr.length-1];
								}
							}
						}
					}
				}
			}
			if (pid!=null) {
				Cnst.log("解析到项目pid为"+pid);
				result = ssh.execCmd("taskkill /pid "+pid+" /F");
				Cnst.log("服务停止完成，pid为"+pid);
			}else{
				Cnst.log("警告：未解析到当前项目pid，默认为项目未启动");
			}
		} catch (Exception e) {
			Main.existSystem("停服务出错，5秒后退出！");
		}
	}
	
	//2、验证jar包是否跟本地一致，如果不一致，上传服务器缺少的jar
	public static void checkJars(){
		try {
			//先判断demo服有没有lib文件夹
			File serverPath = new File(Cnst.demoServerRootPath+"/"+Cnst.project+"_lib");
			if (!serverPath.exists()) {
				serverPath.mkdirs();
			}
			File localJarPath = new File(Cnst.localProjectPath+Cnst.localJarPath);
			String[] jars = localJarPath.list();
			List<String> jarNames = Arrays.asList(jars);
			if (jars!=null) {
				List<String> ques = new ArrayList<String>();
				for(String sf:jarNames){
					File temp = new File(serverPath.getPath()+"/"+sf);
					if (!temp.exists()) {
						ques.add(sf);
					}
				}
				if (ques.size()>0) {
					Cnst.log("开始上传差异jar包...");
					for(String que:ques){
						Cnst.log(localJarPath.getPath()+"/"+que+"\t>"+serverPath.getPath()+"/"+que);
						FileUtil.copyFile(new File(localJarPath.getPath()+"/"+que), new File(serverPath.getPath()+"/"+que));
					}
					Cnst.log("差异jar包上传完成");
				}else{
					Cnst.log("服务器jars与本地一致，无需上传");
				}
			}else{
				Cnst.log("警告：本地jar文件夹无jar包！");
			}
		} catch (Exception e) {
			Main.existSystem(Cnst.checkJarError);
		}
	}
	
	//3、生成本地缓存文件夹，生成META-INF文件夹以及MANIFEST.MF文件
	public static void createTempFile(){
		File localProject = new File(Cnst.localProjectPath+Cnst.localJarPath);
		String[] jars = localProject.list();
		List<String> names = Arrays.asList(jars);
		File tempPath = null;
		FileOutputStream out = null;
		try {
			
			Main.tempFilePath = "D:/temp"+System.currentTimeMillis();
			tempPath = new File(Main.tempFilePath+"/classes");
			tempPath.mkdirs();
			
			File METAPath = new File(tempPath.getPath()+"/META-INF");
			METAPath.mkdir();
			
			File MAINFASTFile = new File(METAPath.getPath()+"/MANIFEST.MF");
			MAINFASTFile.createNewFile();
			
			out = new FileOutputStream(MAINFASTFile);
			byte[] b = "Manifest-Version: 1.0".getBytes();
			out.write(b);
			b = "\n".getBytes();
			out.write(b);

			b = "Created-By: zc".getBytes();
			out.write(b);

			b = "\n".getBytes();
			out.write(b);
			
			b = "Class-Path: .".getBytes();
			out.write(b);
			for(String name:names){
				b = (" "+Cnst.project+"_lib/"+name+" \n ").getBytes();
				out.write(b);
			}
			b = "\n".getBytes();
			out.write(b);

			b = ("Main-Class: "+Cnst.mainClass).getBytes();
			out.write(b);

			b = "\n".getBytes();
			out.write(b);
			b = "\n".getBytes();
			out.write(b);
			out.flush();
			
		} catch (Exception e) {
			Main.existSystem("MANIFEST.MF文件创建失败，5秒后退出！");
		}finally{
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					Main.existSystem("fucking error,5 seconds exist!");
				}
			}
		}
	}

	//4、编译class文件到缓存目录，并与步骤2中的文件打包到入口的jar里
	public static void copyAndZip(){
		try {
			FileUtil.copy(Cnst.localProjectPath+"/build/classes", Main.tempFilePath+"/classes");
			Cnst.log("编译class文件完成");
		} catch (Exception e) {
			Main.existSystem("class文件复制出错，5秒后退出！");
		}
		try {
			Process p = Runtime.getRuntime().exec("jar -cfm "+Main.tempFilePath+"/"+Cnst.project+".jar"+"  "+Main.tempFilePath+"/classes/META-INF/MANIFEST.MF  "+" -C "+Main.tempFilePath+"/classes ." );
			p.waitFor();
			Cnst.log("入口jar打包完成");
		} catch (Exception e) {
			Main.existSystem("入口jar打包出错，5秒后退出！");
		}
	}

	//5、上传更新后的入口jar
	public static void uploadFile(){
		try {
			Cnst.log("开始上传入口jar："+Cnst.project+".jar");
			Thread.sleep(1000);
			FileUtil.copyFile(new File(Main.tempFilePath+"/"+Cnst.project+".jar"), new File(Cnst.demoServerRootPath+"/"+Cnst.project+".jar"));
			Cnst.log("入口jar包上传完成");
		} catch (Exception e) {
			Main.existSystem("上传入口jar失败，请检查路径或者网络连接，5秒后退出！");
		}
	}

	//6、清理本地缓存
	
	
	//7、启动服务
	public static void startService(SSHUtil ssh){
		try {
			ssh.execCmd("java -jar "+Cnst.demoServerDiskPath+"/"+Cnst.project+".jar");
		} catch (Exception e) {
			Main.existSystem("项目启动失败，请手动处理，5秒后退出！");
		}
	}
	
}

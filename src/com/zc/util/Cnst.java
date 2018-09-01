package com.zc.util;

import java.io.File;

public class Cnst {
	
	public static String loadPropertiesError = "配置文件加载错误，5秒后退出！";
	public static String propertyNotExistError = "配置有误，5秒后退出！";
	public static String connectError = "服务器连接出错，请检查参数或者网络连接，5秒后退出！";
	public static String uploadError = "服务器上传文件出错，请检查配置或者本地网络，5秒后退出！";
	public static String unknowError = "未知异常，5秒后退出！";
	public static String pathNotExist = "文件夹不存在，5秒后退出！";
	public static String fileNotExist = "文件不存在，5秒后退出！";
	public static String checkJarError = "jar检测有误，请检查路径或者项目名称，5秒后退出！";
	
	
	/**
	 * main方法args传入
	 */
	public static String project = "";//项目标志  例如：东丰为DF，静海为JH，jar包的目录要以此表示后加_lib；例如DF_lib为东风的jar目录，必传
	
	public static String rootPath = "/usr/local/mjproject";
	
	public static String systemType = "windows";//系统类型，还有linux
	
	public static String localProjectPath = "";

	public static String localJarPath = "";
	
	public static String mainClass = "";
	
	public static String exist = "1";
	
	public static String not_exist = "0";
	
	public static int jiexitype_path = 1;
	public static int jiexitype_file = 2;
	public static int jiexitype_pid = 3;

	public static void log(String msg){
		System.out.println(msg);
	}
	
	
	//检测服务器是否有本地文件时用的cmd
	public static String getExistCmd(String fileName){
		return "test -e ".concat(rootPath+"/").concat(fileName).concat(" && echo 1 || echo 0");
	}
	
	//判断文件夹是否存在的cmd
	public static String getExistParthCmd(String path){
		return "test -d ".concat(rootPath+"/"+path).concat(" && echo 1 || echo 0");
	}
	
	//获取启动性目录的命令
	public static String getStartCmd(){
//		return "cd "+rootPath+" && nohup java -jar "+project+".jar > "+project+".log 2>&1 & echo $!";
		return "cd "+rootPath+" && nohup java -jar "+project+".jar > /dev/null 2>&1 & echo $!";
	}
	
	public static String getListJarCmd(){
		return "ls "+rootPath+"/"+project+"_lib";
	}

	public static String getMakeDirCmd(){
		return "mkdir -p "+rootPath+"/"+project+"_lib";
	}
	
	public static String getPsCmd(){
		return "ps -ef|grep "+project+".jar";
	}
	
	public static String getCatLogCmd(){
		return "cd "+rootPath+"/"+project+"_log/"+" && cat "+project+".log";
	}
	
	public static String getCleanLogCmd(){
		return "rm -rf "+rootPath+"/"+Cnst.project+"_log/";
	}
	
	
	public static String demoServerRootPath = "";
	public static String demoServerDiskPath = "";
	//linux connect
	public static String ip = "";
	public static int port = 0;
	public static String uname = "";
	public static String psd = "";
	//windows connect
	public static String wip = "";
	public static int wport = 0;
	public static String wuname = "";
	public static String wpsd = "";
	

	public static String minaport = "";
	
	
	
	static {
		rootPath = ProjectInfoPropertyUtil.getProperty("rootPath");
		project = ProjectInfoPropertyUtil.getProperty("project");
		localProjectPath = ProjectInfoPropertyUtil.getProperty("localProjectPath");
		localJarPath = ProjectInfoPropertyUtil.getProperty("localJarPath");
		mainClass = ProjectInfoPropertyUtil.getProperty("mainClass");

		ip = ProjectInfoPropertyUtil.getProperty("ip");
		port = Integer.parseInt(ProjectInfoPropertyUtil.getProperty("port"));
		uname = ProjectInfoPropertyUtil.getProperty("uname");
		psd = ProjectInfoPropertyUtil.getProperty("psd");
		wip = ProjectInfoPropertyUtil.getProperty("wip");
		wport = Integer.parseInt(ProjectInfoPropertyUtil.getProperty("wport"));
		wuname = ProjectInfoPropertyUtil.getProperty("wuname");
		wpsd = ProjectInfoPropertyUtil.getProperty("wpsd");
		
		demoServerRootPath = ProjectInfoPropertyUtil.getProperty("demoServerRootPath");
		demoServerDiskPath = ProjectInfoPropertyUtil.getProperty("demoServerDiskPath");
		minaport = ProjectInfoPropertyUtil.getProperty("minaport");
	}
	
	
	
	//8、删除本地缓存
		public static void deleteTempPath(File file){
			if (!file.exists()) {
				return;
			}
			try {
				if (file.isFile()) {
					file.delete();
				}else{
					File[] files = file.listFiles();
					if (files!=null&&files.length>0) {
						for(int i=0;i<files.length;i++){
							File f = files[i];
							if (f.isFile()) {
								f.delete();
							}else{
								deleteTempPath(f);
							}
						}
					}
				}
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		public static String toConvertStr(String str){
			String result = "";
			if (str!=null) {
				String temp = str.trim();
				boolean lastNull = false;
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<temp.length();i++){
					char c = temp.charAt(i);
					if (' '==c) {
						if (!lastNull) {
							lastNull = true;
							sb.append(temp.charAt(i));
						}
					}else{
						lastNull = false;
						sb.append(temp.charAt(i));
					}
				}
				result = sb.toString();
			}
			return result;
		}
	
	
	
	
	
	
	
	
	
	
}


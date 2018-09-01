package com.zc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.zc.main.Main;

/**
 * 
 * 项目信息文件获取工具类
 * 
 * @author sbb
 * 
 */
public class ProjectInfoPropertyUtil {
	
	private static Properties props;
	public static String filepath = "";
	
	public synchronized static void loadProps(){
		props = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(filepath);
			props.load(in);
		} catch (IOException e) {
			Main.existSystem(Cnst.loadPropertiesError);
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				Main.existSystem(Cnst.loadPropertiesError);
			}
		}
	}

	public static String getProperty(String key) {
		if (null == props) {
			loadProps();
		}
		if (!props.containsKey(key)) {
			Main.existSystem(key+Cnst.propertyNotExistError);
		}
		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key, defaultValue);
	}

	// test 读取配置方法
	public static void main(String[] args) {
		String version = ProjectInfoPropertyUtil.getProperty("allKeys");
		System.out.println(version);
	}
}
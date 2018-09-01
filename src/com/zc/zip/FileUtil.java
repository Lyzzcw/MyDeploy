package com.zc.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.zc.main.Main;

public class FileUtil {
	
	public static void main(String[] args) {
//		copy("D:/allType", "");
		File f = new File("//DESKTOP-ICMRVJM/javaDploy/javaProject/allType");
		f.mkdir();
	}
	
	/**
	 * @Title: copy   
	 * @Description: 复制时，不包含 sourcePath文件夹，只复制sourcePath内的资源到targetPath内
	 * @param: @param sourcePath
	 * @param: @param targetPath      
	 * @return: void      
	 * @throws
	 */
	public static void copy(String sourcePath,String targetPath) {
		File sou = new File(sourcePath);
		File tar = new File(targetPath);
		copyDir(sou, tar);
	}
	/**
	 * 拷贝文件夹
	 * @param source
	 * @param target
	 */
	private static void copyDir(File source,File target){
		if(source.isFile()){//如果源是一个文件
			//拷贝文件
			copyFile(source,target);
		}else if(source.isDirectory()){//如果源是一个文件夹
			//目标下创建文件夹
			target.mkdirs();
			//递归循环查找该文件夹下的所有文件和文件夹
			for(File sub : source.listFiles()){
				copyDir(sub,new File(target,sub.getName()));
			}
		}
	}
	/**
	 * 拷贝文件
	 * @param sourceFile
	 * @param targetFile
	 */
	public static void copyFile(File sourceFile,File targetFile){
		if(targetFile.exists()){
			targetFile.delete();
		}
		BufferedInputStream fis = null;
		BufferedOutputStream fos = null;
		int len = 100;
		int count = -1;
		byte[] buffer = new byte[len];
		try {
			fis = new BufferedInputStream(new FileInputStream(sourceFile));
			fos = new BufferedOutputStream(new FileOutputStream(targetFile));
//			fis.read(buffer, 0, len) 每次最多读取len这个长度的数据，放入到buffer自己数据中，赋值给变量count,如果数据读取完毕，count=-1,作为推出循环的条件
			while ((count=fis.read(buffer, 0, len))!= -1) {
//				缓冲写出数据
				fos.write(buffer, 0, count);
			}
//			强制刷新数据到磁盘
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Main.existSystem("文件复制出错，请检查路径，5秒后退出！");
		}finally {
			try {
				fis.close();
				fos.close();
			} catch (Exception e) {
				Main.existSystem("fucking error,5 seconds exist!");
			}
		}
	}

}

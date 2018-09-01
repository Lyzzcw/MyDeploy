package com.zc.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipUtil {
	
	/**
	 * 
	 * @Title: zip   
	 * @Description: 压缩包里不包含sourcePath文件夹，只包含sourcePath里的所有内容
	 * @param: @param sourcePath
	 * @param: @param targetFileFullName  必须要目标文件的全路径，目标路径可以没有创建，传个全路径即可
	 * @param: @throws Exception      
	 * @return: void      
	 * @throws
	 */
	public static void zip(String sourcePath,String targetFileFullName) throws Exception {
		File inFile = new File(sourcePath);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFileFullName), Charset.forName("UTF-8"));
        zos.setComment("create by zc");
        File[] files = inFile.listFiles();
        if (files!=null&&files.length>0) {
			for(File f:files){
		        zipFile(f, zos, "");
			}
		}
        
        zos.close();
	}

	public static void zipFile(File inFile, ZipOutputStream zos, String dir) throws IOException {
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            for (File file:files){
            	if ("".equals(dir)) {
                	zipFile(file, zos,inFile.getName());
				}else{
	            	zipFile(file, zos, dir + "/" + inFile.getName());
				}
            	
            }
        } else {
            String entryName = null;
            if (!"".equals(dir))
                entryName =  dir+"/"+inFile.getName();
            else
                entryName = inFile.getName();
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            InputStream is = new FileInputStream(inFile);
            int len = 0;
            while ((len = is.read()) != -1)
                zos.write(len);
            is.close();
        }

    }
	
}

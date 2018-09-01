package com.zc.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zc.main.Main;
import com.zc.util.Cnst;

public class SSHUtil {

	public static String charset = "GBK"; // 设置编码格式
	private String user; // 用户名
	private String passwd; // 登录密码
	private String host; // 主机IP
	private int port; //端口
	private JSch jsch;
	private Session session;

	/**
	 * 
	 * @param user用户名
	 * @param passwd密码
	 * @param host主机IP
	 */
	public SSHUtil(String user, String passwd, String host ,int port) {
		this.user = user;
		this.passwd = passwd;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * 连接到指定的IP
	 * 
	 * @throws JSchException
	 */
	public void connect() throws JSchException {
		try {
			Cnst.log("开始连接服务器"+host+"...");
			jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setPassword(passwd);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			this.session = session;
			Cnst.log("服务器连接成功");
		} catch (Exception e) {
			e.printStackTrace();
			Main.existSystem(Cnst.connectError);
		}
		
	}
	/**
	 * 关闭连接
	 */
	public void disconnect() throws Exception{
		if(session != null && session.isConnected()){
			session.disconnect();
		}
	}
	/**
	 * 执行一条命令
	 * type的值来区分是否解析服务器输出的值
	 */
	public List<String> execCmd(String command) throws Exception{
		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		Channel channel = null;
		channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		((ChannelExec) channel).setErrStream(System.err);
		channel.connect();
		InputStream in = channel.getInputStream();
		reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
		String buf = null;
		
		while ((buf = reader.readLine()) != null) {
			if (Cnst.systemType.equals("w")) {
				if (command.contains("java -jar")) {
					Cnst.log(buf);
				}else{
					result.add(buf);
				}
			}else if (Cnst.systemType.equals("l")) {
				result.add(buf);
			}
		}
		channel.disconnect();
		return result;
	}
	
	
	public List<String> execShellCmd(String command)  throws Exception{
		List<String> result = new ArrayList<String>();
		ChannelShell channel = (ChannelShell) session.openChannel("shell");
		channel.connect();
		// 从远程端到达的所有数据都能从这个流中读取到
		InputStream in = channel.getInputStream();
		// 写入该流的所有数据都将发送到远程端。
		OutputStream outputStream = channel.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(command);
        printWriter.println("exit");//加上个就是为了，结束本次交互
        printWriter.flush();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String msg = null;
        while((msg = reader.readLine())!=null){
        	result.add(msg);
        }
		return result;
	}
	
	
	/**
	 * 执行相关的命令
	 */
	public void execCmd() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String command = "";
		BufferedReader reader = null;
		Channel channel = null;

		try {
			while ((command = br.readLine()) != null) {
				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				channel.setInputStream(null);
				((ChannelExec) channel).setErrStream(System.err);
				
				channel.connect();
				InputStream in = channel.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
				String buf = null;
				while ((buf = reader.readLine()) != null) {
					System.out.println(buf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			channel.disconnect();
		}
	}
	
	
	/**
	 * 上传文件，如果服务器存在，就会覆盖
	 */
	public void uploadFile(String local,String remote) throws Exception {
		ChannelSftp channel = null;
		InputStream inputStream = null;
		try {
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(5000);
			inputStream = new FileInputStream(new File(local));
			channel.setInputStream(inputStream);
			channel.put(inputStream, remote);
		} catch (Exception e) {
			throw e;
		}finally{
			if(channel != null){
				channel.disconnect();
			}
			if(inputStream != null){
				inputStream.close();
			}
		}
	}
	/**
	 * 下载文件
	 */
	public void downloadFile(String remote,String local) throws Exception{
		ChannelSftp channel = null;
		OutputStream outputStream = null;
		try {
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(5000);
			outputStream = new FileOutputStream(new File(local));
			channel.get(remote, outputStream);
			outputStream.flush();
		} catch (Exception e) {
			throw e;
		}finally{
			if( channel != null){
				channel.disconnect();
			}
			if(outputStream != null){
				outputStream.close();
			}
		}
	}

}

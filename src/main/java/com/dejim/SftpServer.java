package com.dejim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.cipher.BuiltinCiphers;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import org.apache.sshd.common.util.security.bouncycastle.BouncyCastleGeneratorHostKeyProvider;

public class SftpServer {

	private int port = 8082;
	private String passwd = "test";
	
	private SshServer sshd;

	public SftpServer() {
		init();
	}
	
	public SftpServer(int _port, String _passwd) {
		this.setPort(_port);
		this.setPasswd(_passwd);
		init();
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public void init() {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(port);
		sshd.setHost("0.0.0.0");
		
		sshd.setKeyPairProvider(new BouncyCastleGeneratorHostKeyProvider(Paths.get("app.pem")));	
		sshd.setCipherFactories(Arrays.asList(BuiltinCiphers.aes256ctr, BuiltinCiphers.aes192ctr, BuiltinCiphers.aes128ctr));
			
		SftpSubsystemFactory factory = new SftpSubsystemFactory();
		sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(factory));

		sshd.setPasswordAuthenticator(
				(username, password, session) -> username.equals("mule") && password.equals(passwd));

		VirtualFileSystemFactory fileSystemFactory = new VirtualFileSystemFactory();
		try {
			fileSystemFactory.setDefaultHomeDir(Files.createTempDirectory("tmp"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sshd.setFileSystemFactory(fileSystemFactory);
	}

	public String start() {
		try {
			sshd.start();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return "SFTP server (Port:" + port + ") starting...";
	}

	public String stop() {
		try {
			sshd.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "SFTP server stopping...";
	}

}

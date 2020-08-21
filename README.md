# Run an embedded SFTP Server on Mule 4.x

> :warning: **Not for CloudHub Production**: This is for demo / example purposes only. Data stored to the file system on a CloudHub worker is not persistent in the event of a restart or deployment. 

 This project is an example that allows you to run a SFTP server on Mule 4.x. It leverages [Apache SSHD](https://mina.apache.org/sshd-project/), which is based on [Apache MINA](https://mina.apache.org/), a light weight network application framework, to expose an SFTP server for testing purposes.

## Versions
* Apache SSHD 2.5.1

## Setup

1. Configure the `sftp.password` properties file with a password

    #### mule-properties.yaml
    ```
    sftp:
    port: "8082"
    password: "password"
    ```
1. Deploy to CloudHub
1. Navigate to `http://<app-name>.<region>.cloudhub.io/sftp?action=start` to start the SFTP server

## Use

In order to connect to the SFTP server, you need to access it from the external IP address of the Mule worker. The address will be in the following format:

```
mule-worker-<app-name>.<region>.cloudhub.io:8082
```

## Code

Below is a snippet from the code that shows how to setup the SFTP server subsystem.

```

sshd = SshServer.setUpDefaultServer();
sshd.setPort(port);
sshd.setHost("0.0.0.0");

sshd.setKeyPairProvider(new BouncyCastleGeneratorHostKeyProvider(Paths.get("app.pem")));	
sshd.setCipherFactories(Arrays.asList(BuiltinCiphers.aes256ctr, BuiltinCiphers.aes192ctr, BuiltinCiphers.aes128ctr));
    
SftpSubsystemFactory factory = new SftpSubsystemFactory();
sshd.setSubsystemFactories(Collections.singletonList(factory));

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

```

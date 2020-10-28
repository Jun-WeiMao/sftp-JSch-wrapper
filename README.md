# sftp-JSch-wrapper
[//]: # "[![Maven Central](badge-pic)](maven-central-url)"

The sftp-JSch-wrapper (SJW) library it's a wrapper based on JSch (JCraft, lnc.) and Commons Pool (The Apache Software Foundation), allows Java applications to easily manipulate files through sftp protocol besides controlling sessions with pool.

It's currently compiled on Java 6.

## :warning: Not available yet!!!

It's an ongoing project, will release first soon.  

## Installation

Binaries aren't deployed on Maven Central yet, so will need to add as an external jar.

## Version

* MAJOR = huge refactoring
* MINOR = new features and minor API changes
* FIX = no API change, just bug fixes

Check CHANGES.md for migration path between versions.

## Basics

Javadoc is coming soon.

[//]: # "Feel free to check the [Javadoc](url) or the code for more information."

## SFTP
### Prepare target host details

```java
    // Prepare server details
    ServerDetails details = new ServerDetails();
    details.setRemoteHost("remoteHost");
    details.setPort("port");
    details.setUsername("username");
    details.setPassword("password");
    details.setTimeout("timeout");

    // Extra config
    Map<String, String> configMap = new HashMap<String, String>();
    configMap.put("StrictHostKeyChecking", "No");
    details.setConfig(configMap);
```

### Init session pool

```java          
    // Setting session pool max count and init pool, without setting it the default value is 8
    StackSessionPool.getInstance().setMax(5);

    // Remeber to call .close() to close the pool before recreate it
    KeyedObjectPool<ServerDetails, Session> pool = StackSessionPool.getInstance().getPool();

    // Getting session and channel of particular host
    Session session = pool.borrowObject(details);
```

### Connect to host

```java
    // Connect on SFTPClient been created with the giving session
    SFTPClient sftpClient = new SFTPClient(session);
```

### Return session to pool

```java
    // Disconnect with host
    sftpClient.disconnect();

    // Return to pool
    pool.returnObject(details, session);
```

### Get a file from host

```java
    SFTPResult result = sftpClient.getFile(remoteFile, localFile);

    // In this case will get "downloadFile"
    String action = result.getActionType().name();

    // Full path file name from host
    String theRemoteFileFullPath = result.getSourcePath();

    // Full path file name that store at local
    String theDownloadFileFullPath = result.getDestPath();

    // Result of this action
    boolean isSuccess = result.isSuccess();
    
    // Failure reason
    String errMsg = result.getErrMsg();
```

### Put a file to host

```java
    SFTPResult result = sftpClient.putFile(localFile, remoteFile);

    // In this case will get "uploadFile"
    String action = result.getActionType().name();

    // Full path local file name
    String theLocalFileFullPath = result.getSourcePath();

    // Full path file name that store to host
    String theUploadFileFullPath = result.getDestPath();

    // Result of this action
    boolean isSuccess = result.isSuccess();
    
    // Failure reason
    String errMsg = result.getErrMsg();
```

## More

You can find more information on org.apache.commons.pool2 Java docs about manipulating with KeyedObjectPool.

* https://commons.apache.org/proper/commons-pool/apidocs/index.html

## Contributing

Pull Requests are welcome.

Here are the few rules we'd like you to follow if you do so:

* Only edit the code related to the suggested change, so DON'T automatically format the classes you've edited.
* Use IntelliJ default formatting rules.
* Regarding licensing:
  * You must be the original author of the code you suggest.
  * You must give the copyright to SJW project.
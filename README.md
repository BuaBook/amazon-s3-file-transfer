# Amazon S3 File Transfer Library

This library provides file upload and download wrapper classes for the AWS S3 SDK.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.buabook/amazon-s3-file-transfer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.buabook/amazon-s3-file-transfer)
[![Build Status](https://travis-ci.org/BuaBook/amazon-s3-file-transfer.svg?branch=master)](https://travis-ci.org/BuaBook/amazon-s3-file-transfer)
[![Coverage Status](https://coveralls.io/repos/github/BuaBook/amazon-s3-file-transfer/badge.svg?branch=master)](https://coveralls.io/github/BuaBook/amazon-s3-file-transfer?branch=master)

## Uploading

We wrap a `TransferManager` with the following features:

* Application environment path prefix
   * This allows multiple environments to use the same S3 bucket but data separation at the root level of the bucket between them. The environment can be specified as any String.
* Automatic content length calculation
* Files downloaded straight to String

Uploading can be performed in 3 ways, each allow the caller to do more configuration:

* Using an `UploadContent` object - requires supplying the file content as a String along with a file name for the upload.
* Using a file name and `InputStream`
* Using your own `PutObjectRequest`

### Modifying Uploaded File Permissions

By default the permissions of a newly uploaded file will be inherited from the parent folder or bucket. If you want to upload a file with different permissions, you can use the `uploadSync` method directly, supplying your own `PutObjectRequest`:

```java
PutObjectRequest uploadRequest = s3FileUploader.getNewPutObjectRequest(s3Path, fileDataInputStream, fileDataLength);
// Allow everyone to read uploaded file
uploadRequest.setCannedAcl(CannedAccessControlList.PublicRead);

try {
    s3FileUploader.uploadSync(uploadRequest);
} catch (AmazonClientException | IllegalArgumentException e) {
    log.error("Failed to upload public file to Amazon. Error - " + e.getMessage(), e);
}
```

## Required Access Permissions

In order to either upload or download files from Amazon S3, an account must be created with the following permissions:

* Download: `GetObject`
* Upload: `PutObject`

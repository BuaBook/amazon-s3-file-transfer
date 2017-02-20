package com.buabook.amazon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.buabook.amazon.exceptions.FileUploadDownloadFailedException;
import com.buabook.amazon.interfaces.IAmazonS3Uploader;
import com.buabook.amazon.pojos.UploadContent;
import com.google.common.base.Strings;

/**
 * <h3>Amazon S3 File Uploader</h3>
 * (c) 2015 Sport Trades Ltd
 * 
 * @author Jas Rajasansir
 * @version 1.1.0
 * @since 4 Sep 2015
 */
public class AmazonS3FileUploader implements IAmazonS3Uploader {
	private static final Logger log = LoggerFactory.getLogger(AmazonS3FileUploader.class);
	

	private final String bucketName;
	
	private final AmazonS3Connection s3Connection;
	
	private final TransferManager transferManager;
	
	
	public AmazonS3FileUploader(AmazonS3Connection s3Connection, String bucketName) throws IllegalArgumentException {
		if(s3Connection == null)
			throw new IllegalArgumentException("No connection to S3 specified");
		
		if(Strings.isNullOrEmpty(bucketName))
			throw new IllegalArgumentException("No S3 region specified");
		
		this.bucketName = bucketName;
		this.s3Connection = s3Connection;
		
		this.transferManager = TransferManagerBuilder.standard()
															.withS3Client(s3Connection.getConnection())
															.build();
		
		log.info("Creating new Amazon S3 File Uploader [ Bucket Name: " + bucketName + " ]");
	}
	
	
	@Override
	public void upload(UploadContent dataToLog) throws FileUploadDownloadFailedException {
		byte[] stringAsBytes = dataToLog.getFileContent().getBytes();
		ByteArrayInputStream stream = new ByteArrayInputStream(stringAsBytes);
		
		uploadSync(getNewPutObjectRequest(dataToLog.getFileName(), stream, stringAsBytes.length));
	}
	
	
	public void upload(String filePath, InputStream fileData) throws FileUploadDownloadFailedException {
		try {
			uploadSync(getNewPutObjectRequest(filePath, fileData));
		} catch(AmazonClientException e) {
			throw new FileUploadDownloadFailedException(e);
		}
	}
	
	public void uploadSuppressed(String filePath, InputStream fileData) {
		try {
			upload(filePath, fileData);
		} catch(FileUploadDownloadFailedException | RuntimeException e) {
			log.info("[ Exception Suppressed ] " + e.getMessage(), e);
		}
	}
	
	
	/** @throws IllegalArgumentException If the file path to upload to S3 does not contain the current application environment (see {@link AmazonS3Connection#getAppEnvironment()}) */
	public void uploadSync(PutObjectRequest fileToUpload) throws AmazonClientException, IllegalArgumentException {
		if(fileToUpload == null)
			throw new IllegalArgumentException("No upload object specified");
		
		if(! fileToUpload.getKey().contains(s3Connection.getAppEnvironment()))
			throw new IllegalArgumentException("No environment found within the file path. The file path must contain '" + s3Connection.getAppEnvironment() + "'");
		
		long uploadFileLength = fileToUpload.getMetadata().getContentLength();
		
		log.info("Attempting to upload object to Amazon S3 [ Expected File Name: " + fileToUpload.getKey() + " ] " + 
					(uploadFileLength == 0 ? "[ No Content-Length Specified ]" : "[ Length: " + fileToUpload.getMetadata().getContentLength() + " bytes ]"));
		
		try {
			Upload upload = transferManager.upload(fileToUpload);
			upload.waitForCompletion();
		} catch (AmazonServiceException e) {
			log.error("Amazon rejected our uploader request! [ Error Code: " + e.getErrorCode() + " ] Error - " + e.getMessage());
			throw e;
		} catch(AmazonClientException e) {
			log.error("Internal error while attempting to upload to Amazon! Error - " + e.getMessage());
			throw e;
		} catch (InterruptedException e) {
			log.warn("Thread was interrupted waiting for upload confirmation. Error - " + e.getMessage());
		}
		
		log.info("Object successfully uploaded to Amazon S3 [ File Name: " + fileToUpload.getKey() + " ]");
	}

	/**
	 * This method will provide the bucket name (as configured in this class) and prefix the
	 * file path with the application environment.
	 * @return A valid request to upload based on the file path and data provided.
	 */
	public PutObjectRequest getNewPutObjectRequest(String filePath, InputStream fileData) {
		return getNewPutObjectRequest(filePath, fileData, null);
	}
	
	public PutObjectRequest getNewPutObjectRequest(String filePath, InputStream fileData, Integer fileDataLength) {
		ObjectMetadata metadata = new ObjectMetadata();
		
		if(fileDataLength != null)
			metadata.setContentLength(fileDataLength);
		
		return new PutObjectRequest(bucketName, s3Connection.getAppEnvironment() + "/" + filePath, fileData, metadata);
	}
	
	public String getBucketName() {
		return bucketName;
	}

}

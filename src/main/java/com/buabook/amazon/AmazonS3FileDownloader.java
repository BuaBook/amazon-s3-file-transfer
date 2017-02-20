package com.buabook.amazon;

import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.buabook.amazon.exceptions.FileUploadDownloadFailedException;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;

/**
 * <h3>Amazon S3 File Downloader</h3>
 * <br/><br/>(c) 2015 Sport Trades Ltd
 * 
 * @author Jaskirat M.S. Rajasansir
 * @version 1.0.0
 * @since 2 Sep 2015
 */
public class AmazonS3FileDownloader {
	private static final Logger log = LoggerFactory.getLogger(AmazonS3FileDownloader.class);
	

	private final AmazonS3Connection s3Connection;
	
	private final String bucketName;
	

	public AmazonS3FileDownloader(AWSCredentials credentials, String s3BucketName, Regions region, String environment) {
		if(Strings.isNullOrEmpty(s3BucketName) || region == null)
			throw new IllegalArgumentException("No S3 bucket / region specified");
		
		this.s3Connection = new AmazonS3Connection(credentials, region, environment);
		this.bucketName = s3BucketName;
	}
	
	
	/**
	 * <b>NOTE</b>: It is up to the calling function to close the file stream once done with it. Call 
	 * {@link S3Object#close()} when done.
	 * @throws FileUploadDownloadFailedException 
	 */
	public S3Object getFile(String filePath) throws IllegalArgumentException, FileUploadDownloadFailedException {
		if(Strings.isNullOrEmpty(filePath))
			throw new IllegalArgumentException("File path to download is empty");
		
		S3Object file = null;
		
		try {
			file = s3Connection.getConnection().getObject(new GetObjectRequest(bucketName, s3Connection.getAppEnvironment() + "/" + filePath));
		} catch (AmazonServiceException e) {
			log.error("Amazon rejected our download request! [ Error Code: " + e.getErrorCode() + " ] Error - " + e.getMessage());
			throw new FileUploadDownloadFailedException(e);
		} catch(AmazonClientException e) {
			log.error("Internal error while attempting to download from Amazon! Error - " + e.getMessage());
			throw new FileUploadDownloadFailedException(e);
		} 
		
		return file;
	}
	
	public String getFileAsString(String filePath) throws IllegalArgumentException, FileUploadDownloadFailedException {
		String fileString = null;
		
		try(	S3Object file = getFile(filePath);
				InputStreamReader isr = new InputStreamReader(file.getObjectContent())) {
			
			fileString = CharStreams.toString(isr);
			
		} catch (IOException e) {
			log.error("Failed to convert file data to string. Error - " + e.getMessage());
		}
		
		return fileString;
	}
}
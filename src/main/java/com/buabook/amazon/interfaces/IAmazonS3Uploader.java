package com.buabook.amazon.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buabook.amazon.exceptions.FileUploadDownloadFailedException;
import com.buabook.amazon.pojos.UploadContent;

/**
 * <h3>Amazon S3 Upload Interface</h3>
 * (c) 2017 Sport Trades Ltd
 * 
 * @author Jaskirat M.S. Rajasansir
 * @version 1.0.0
 * @since 20 Feb 2017
 */
public interface IAmazonS3Uploader {
	static final Logger log = LoggerFactory.getLogger(IAmazonS3Uploader.class);

	/**
	 * Uploads the specified content to Amazon. Any upload error will be thrown back to the caller.
	 * @throws FileUploadDownloadFailedException
	 */
	public void upload(UploadContent dataToUpload) throws FileUploadDownloadFailedException;
	
	/**
	 * Uploads the specified content to Amazon. Any upload error is caught and logged and <i>no</i>
	 * error is returned to the caller.
	 * @see #upload(UploadContent)
	 */
	default void uploadSuppressed(UploadContent dataToUpload) {
		try {
			upload(dataToUpload);
		} catch(FileUploadDownloadFailedException | RuntimeException e) {
			log.info("[ Exception Suppressed ] " + e.getMessage(), e);
		}
	}
	

}

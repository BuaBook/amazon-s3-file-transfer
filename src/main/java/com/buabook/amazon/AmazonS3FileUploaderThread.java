package com.buabook.amazon;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buabook.amazon.exceptions.FileUploadDownloadFailedException;
import com.buabook.amazon.interfaces.IAmazonS3Uploader;
import com.buabook.amazon.pojos.UploadContent;

/**
 * <h3>Threaded Wrapper for {@link AmazonS3Uploader}</h3>
 * <p>Processing of each file to upload is handled on a separate daemon thread</p> 
 * (c) 2015 Sport Trades Ltd
 * 
 * @author Jas Rajasansir
 * @version 1.1.0
 * @since 8 Jun 2015
 */
public class AmazonS3FileUploaderThread extends Thread implements IAmazonS3Uploader {
	private static final Logger log = LoggerFactory.getLogger(AmazonS3FileUploaderThread.class);
	
	private static final Long THREAD_SLEEP_MS = 1000l;

	
	private final AmazonS3FileUploader uploader;

	private final ConcurrentLinkedQueue<UploadContent> logsToUpload;
	
	
	private Boolean timeToTerminate = Boolean.FALSE;
	
	
	public AmazonS3FileUploaderThread(AmazonS3Connection s3Connection, String bucketName) {
		this.uploader = new AmazonS3FileUploader(s3Connection, bucketName);
		
		this.logsToUpload = new ConcurrentLinkedQueue<>();
		
		this.setName("S3Uploader-" + bucketName + "-" + s3Connection.getAppEnvironment());
		this.setPriority(Thread.MIN_PRIORITY);
		this.setDaemon(true);
	}
	

	@Override
	public synchronized void start() {
		log.info("Starting Amazon S3 Uploader thread processing");
		super.start();
	}
	
	
	@Override
	public void run() {

		while(! timeToTerminate) {
			// Only sleep if nothing to do
			if(logsToUpload.isEmpty()) {
				try {
					Thread.sleep(THREAD_SLEEP_MS);
				} catch (InterruptedException e) {}
				
				continue;
			}
			
			UploadContent logToUpload = logsToUpload.poll();
			
			if(log.isDebugEnabled())
				log.debug("Uploading log to Amazon S3 [ Upload Queue Size: " + logsToUpload.size() + " ]");
			
			try {
				uploader.upload(logToUpload);
			} catch (FileUploadDownloadFailedException | RuntimeException e) {
				log.error("Data logging failed. MESSAGE LOST! Error - " + e.getMessage(), e);
			}
		}
		
	}

	/**
	 * <b>NOTE</b>: This function will never throw {@link FileUploadDownloadFailedException}
	 * as the logging is performed by the thread at a later point.
	 */
	@Override
	public void upload(UploadContent dataToLog) throws FileUploadDownloadFailedException {
		if(dataToLog == null)
			return;
		
		logsToUpload.add(dataToLog);
	}

	public synchronized void terminateThread() {
		timeToTerminate = true;
	}
}

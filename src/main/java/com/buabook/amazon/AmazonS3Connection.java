package com.buabook.amazon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.common.base.Strings;

/**
 * <h3>Amazon S3 Connection Object</h3>
 * (c) 2015 Sport Trades Ltd
 * 
 * @author Jas Rajasansir
 * @version 1.0.0
 * @since 13 Jul 2015
 */
public class AmazonS3Connection {
	private static final Logger log = LoggerFactory.getLogger(AmazonS3Connection.class);
	

	/** 
	 * Credentials required to connect to Amazon S3. The credentials should be given the following rights:
	 * <ul>
	 * 	<li>{@link AmazonS3FileDownloader}: <code>GetObject</code></li>
	 * 	<li>{@link AmazonS3FileUploader}: <code>PutObject</code></li>
	 * </ul>
	 */
	private final AWSCredentials credentials;
	
	/** 
	 * The current environment the application is running in. This will determine which root folder within
	 * the bucket the files will be stored within.
	 */
	private final String appEnvironment;
	
	private final AmazonS3 connection;
	
	
	public AmazonS3Connection(AWSCredentials credentials, Regions region, String environment) throws IllegalArgumentException {
		if(credentials == null || Strings.isNullOrEmpty(environment))
			throw new IllegalArgumentException("No credentials or environment set");
		
		log.info("Creating new Amazon S3 connection [ Region: {} ] [ Access Key ID: {} ]", region, credentials.getAWSAccessKeyId());
	
		this.credentials = credentials;
		this.appEnvironment = environment;
		this.connection = AmazonS3ClientBuilder.standard()
														.withCredentials(new AWSStaticCredentialsProvider(credentials))
														.withRegion(region)
														.build();
	}


	public AWSCredentials getCredentials() {
		return credentials;
	}

	public String getAppEnvironment() {
		return appEnvironment;
	}

	public AmazonS3 getConnection() {
		return connection;
	}
}

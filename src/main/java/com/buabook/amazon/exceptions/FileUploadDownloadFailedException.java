package com.buabook.amazon.exceptions;

public class FileUploadDownloadFailedException extends Exception {
	private static final long serialVersionUID = 1219611070158031636L;
	
	private static final String message = "The file failed to upload / download with Amazon S3";

	public FileUploadDownloadFailedException() {
		super(message);
	}

	public FileUploadDownloadFailedException(String message) {
		super(FileUploadDownloadFailedException.message + " " + message);
	}

	public FileUploadDownloadFailedException(Throwable cause) {
		super(FileUploadDownloadFailedException.message, cause);
	}

	public FileUploadDownloadFailedException(String message, Throwable cause) {
		super(FileUploadDownloadFailedException.message + " " + message, cause);
	}

	public FileUploadDownloadFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(FileUploadDownloadFailedException.message + " " + message, cause, enableSuppression, writableStackTrace);
	}

}

package com.sorted.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@Component
public class GoogleDriveService {
	private static final String APPLICATION_NAME = "AudioBook";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/drive");
	private static final String CREDENTIALS_FILE_PATH = "E:/credentials/service_account.json";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets from file
		java.io.File credentialsFile = new java.io.File(CREDENTIALS_FILE_PATH);
		if (!credentialsFile.exists()) {
			throw new FileNotFoundException("File not found: " + CREDENTIALS_FILE_PATH);
		}

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(new FileInputStream(credentialsFile)));

		// Build flow and trigger user authorization request
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();

		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public Drive getInstance() throws GeneralSecurityException, IOException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		return service;
	}

	// Method to upload a photo
	public String uploadPhoto(MultipartFile multipartFile) throws IOException, GeneralSecurityException {
		Drive service = getInstance();
		java.io.File tempFile = java.io.File.createTempFile("temp", ".tmp");
		multipartFile.transferTo(tempFile);
		File fileMetadata = new File();
		fileMetadata.setName("Image");
		fileMetadata.setParents(Collections.singletonList("1gzz7LRmeLT0bsJhF7OeuWfMkyBTgautX"));

		FileContent mediaContent = new FileContent("image/jpeg", tempFile);

		File file = service.files().create(fileMetadata, mediaContent).setFields("id").execute();
		return file.getId();
	}

	// Method to fetch a photo
	public void fetchPhoto(String fileId, String destinationPath) throws IOException, GeneralSecurityException {
		Drive service = getInstance();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

		try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
			outputStream.writeTo(fos);
		}
	}
}

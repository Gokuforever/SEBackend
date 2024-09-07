package com.sorted.commons.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GoogleDriveService {

	private static final String SERVICE_ACCOUNT_FILE = "D:\\google_auth\\service_account.json";
	private static final String PARENT_FOLDER_ID = "1gzz7LRmeLT0bsJhF7OeuWfMkyBTgautX";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	private Drive getDriveService() throws GeneralSecurityException, IOException {
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(SERVICE_ACCOUNT_FILE)) // Changed
																												// to
																												// FileInputStream
				.createScoped(Collections.singleton(DriveScopes.DRIVE));
		return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				new HttpCredentialsAdapter(credentials)).setApplicationName("My Google Drive App").build();
	}

	public String uploadPhoto(String filePath) throws IOException, GeneralSecurityException {
		Drive service = getDriveService();
		File fileMetadata = new File();
		fileMetadata.setName("Image");
		fileMetadata.setParents(Collections.singletonList(PARENT_FOLDER_ID));

		java.io.File filePathObj = new java.io.File(filePath);
		FileContent mediaContent = new FileContent("image/jpeg", filePathObj);

		File file = service.files().create(fileMetadata, mediaContent).setFields("id").execute();
		System.out.printf("File '%s' uploaded successfully with ID: %s%n", filePath, file.getId());
		return file.getId();
	}

	public void fetchPhoto(String fileId, String destinationPath) {
		try {
			Drive service = getDriveService();
			Drive.Files.Get request = service.files().get(fileId);
			try (InputStream inputStream = request.executeMediaAsInputStream();
					FileOutputStream outputStream = new FileOutputStream(destinationPath)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				System.out.printf("File downloaded successfully to '%s'%n", destinationPath);
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}
}

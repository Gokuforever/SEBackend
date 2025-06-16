package com.sorted.commons.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.sorted.commons.beans.UsersBean;
import com.sorted.commons.entity.mongo.File_Upload_Details;
import com.sorted.commons.entity.service.File_Upload_Details_Service;
import com.sorted.commons.enums.DocumentType;
import com.sorted.commons.enums.ResponseCode;
import com.sorted.commons.enums.UserType;
import com.sorted.commons.exceptions.CustomIllegalArgumentsException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoogleDriveService {

	@Autowired
	private File_Upload_Details_Service file_upload_details_service;

	@Value("${se.google.service_account_file_path}")
	private String service_account_file_path;

	@Value("${se.folder_id.product.image}")
	private String product_image_folder_id;

	@Value("${se.folder_id.profile.image}")
	private String profile_image_folder_id;

	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	private Drive getDriveService() throws GeneralSecurityException, IOException {
		log.debug("Initializing Google Drive service");
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(service_account_file_path))
				.createScoped(Collections.singleton(DriveScopes.DRIVE));
		return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				new HttpCredentialsAdapter(credentials)).setApplicationName("My Google Drive App").build();
	}

	public File_Upload_Details uploadPhoto(@NonNull MultipartFile multipart_file, @NonNull UsersBean users_bean,
			@NonNull DocumentType document_type) throws IOException, GeneralSecurityException {

		log.info("Uploading photo for user: {}", users_bean.getId());

		boolean image = CommonUtils.isImage(multipart_file);
		if (!image) {
			throw new CustomIllegalArgumentsException(ResponseCode.INVALID_FILE_TYPE);
		}
		File_Upload_Details upload_details = new File_Upload_Details();
		UserType user_type = users_bean.getRole().getUser_type();
		List<UserType> allowed_users = document_type.getAllowed_to();

		if (!allowed_users.contains(user_type)) {
			log.error("User type {} is not allowed to upload document type {}", user_type, document_type);
			throw new CustomIllegalArgumentsException(ResponseCode.ACCESS_DENIED);
		}

		String folder_id = this.getFolderIdByDocumentType(document_type);
		this.populateUploadDetails(upload_details, users_bean, user_type, document_type);

		java.io.File file_to_upload = convertMultipartFileToFile(multipart_file);
		Drive service = getDriveService();

		log.debug("Uploading file to Google Drive");
		File file_metadata = new File();
		file_metadata.setName(multipart_file.getOriginalFilename());
		file_metadata.setParents(Collections.singletonList(folder_id));

		FileContent media_content = new FileContent(multipart_file.getContentType(), file_to_upload);
		File file = service.files().create(file_metadata, media_content).setFields("id, size, fileExtension").execute();

		log.info("File uploaded successfully: ID = {}, Name = {}", file.getId(), file_to_upload.getName());

		return this.storeFileDetails(upload_details, file, file_to_upload, users_bean);
	}

	private File_Upload_Details storeFileDetails(File_Upload_Details upload_details, File file, java.io.File file_to_upload,
			UsersBean users_bean) {
		upload_details.setFile_extension(file.getFileExtension());
		long size_in_bytes = file.getSize();
		double size_in_kb = size_in_bytes / 1024.0;
		upload_details.setSize(size_in_kb + "kb");

		File_Upload_Details file_Upload_Details = file_upload_details_service.create(upload_details,
				users_bean.getId());
		boolean delete = file_to_upload.delete();
		if (delete) {
			log.info("temp file deleted.");
		}
		log.info("File details stored in the database for user: {} and File_Upload_Details id: {}", users_bean.getId(),
				file_Upload_Details.getId());
		return file_Upload_Details;
	}

	private String getFolderIdByDocumentType(DocumentType document_type) {
		String folder_id;
		switch (document_type) {
		case PRODUCT_IMAGE:
			folder_id = product_image_folder_id;
			break;
		case PROFILE_PICTURE:
			folder_id = profile_image_folder_id;
			break;
		default:
			log.error("Unsupported document type: {}", document_type);
			throw new CustomIllegalArgumentsException(ResponseCode.ERR_0001);
		}
		return folder_id;
	}

	private void populateUploadDetails(File_Upload_Details upload_details, UsersBean users_bean, UserType user_type,
			DocumentType document_type) {
		if (user_type == UserType.SELLER) {
			upload_details.setEntity_id(users_bean.getRole().getSeller_id());
		} else {
			upload_details.setEntity_id(users_bean.getId());
		}
		upload_details.setUser_type(user_type);
		upload_details.setDocument_type_id(document_type.getId());
	}

	private java.io.File convertMultipartFileToFile(MultipartFile multipart_file) throws IOException {
		log.debug("Converting MultipartFile to java.io.File");
		java.io.File conv_file = new java.io.File(multipart_file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(conv_file)) {
			fos.write(multipart_file.getBytes());
		}
		return conv_file;
	}

	public String fetchPhoto(String fileId) throws Exception {
		try {
			log.info("Fetching photo with ID: {}", fileId);
			Drive service = getDriveService();
			try (InputStream input_stream = service.files().get(fileId).executeMediaAsInputStream()) {

				// Read the image bytes
				byte[] imageBytes = input_stream.readAllBytes();

				// Encode image bytes to Base64
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);

				// Return a success response with the Base64-encoded image
				return base64Image;
			}
		} catch (Exception e) {
			log.error("Error fetching file with ID: {}, Message: {}", fileId, e.getMessage(), e);
			throw e;
		}
	}

	@SuppressWarnings("unused")
	private String createFolder(String folder_name) throws IOException, GeneralSecurityException {
		log.info("Creating folder: {}", folder_name);
		Drive service = getDriveService();
		File file_metadata = new File();
		file_metadata.setName(folder_name);
		file_metadata.setMimeType("application/vnd.google-apps.folder");

		File folder = service.files().create(file_metadata).setFields("id").execute();

		log.info("Folder '{}' created successfully with ID: {}", folder_name, folder.getId());
		return folder.getId();
	}
}

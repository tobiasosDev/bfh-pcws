package ch.bfh.pcws.log.file;

import ch.bfh.pcws.log.DataLogEntry;
import ch.bfh.pcws.log.DataLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.UUID;

import static ch.bfh.pcws.util.Json.createObjectMapper;

public class FileDataLogServiceImpl implements DataLogService {

    private static final Logger logger = LoggerFactory.getLogger(FileDataLogServiceImpl.class);

    private final String applicationName;
    private final String datalogDirectory;
    private final ObjectMapper objectMapper = createObjectMapper();

    public FileDataLogServiceImpl(
            String applicationName,
            String datalogDirectory) {
        this.applicationName = applicationName;
        this.datalogDirectory = datalogDirectory;
    }

    @Override
    public void log(ZonedDateTime start, ZonedDateTime end, String accountName, String generatedBarcode) {
        try {
            DataLogEntry dataLogEntry = new DataLogEntry(applicationName, start, end, accountName, generatedBarcode);
            writeDatalogEntryFile(dataLogEntry);
        } catch (IOException e) {
            logger.error("Could not write data log for accountName={},barcode={}", accountName, generatedBarcode, e);
        }
    }

    private void writeDatalogEntryFile(DataLogEntry dataLogEntry) throws JsonProcessingException, FileNotFoundException {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        String datalogEntryJson = objectMapper.writeValueAsString(dataLogEntry);
        String fileName = UUID.randomUUID().toString() + ".json";
        String bucketName = "pcws-log-bucket"; // Your bucket name

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

        // Write the file
        Blob blob = storage.create(blobInfo, datalogEntryJson.getBytes());

        System.out.println("File written successfully!");
    }
}

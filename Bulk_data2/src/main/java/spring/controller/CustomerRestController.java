package spring.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import spring.entity.Customer;
import spring.repo.CustomerRepository;
import java.io.*;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/batch")
public class CustomerRestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private CustomerRepository customerRepo;

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            // Ensure the upload directory exists
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Create a unique file name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            // Pass filePath dynamically using JobParameters
            JobParameters jobParams = new JobParametersBuilder()
                    .addString("filePath", filePath.toAbsolutePath().toString()) // Dynamic file path
                    .addLong("time", System.currentTimeMillis()) // Ensure uniqueness
                    .toJobParameters();

            jobLauncher.run(job, jobParams);

            return ResponseEntity.ok("CSV uploaded and batch processing started for file: " + fileName);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/download-csv")
    public ResponseEntity<Resource> downloadCsv() {
        try {
            List<Customer> customers = customerRepo.findAll();

            // Create CSV file
            String fileName = "customers_data.csv";
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            BufferedWriter writer = Files.newBufferedWriter(filePath);

            writer.write("ID,First Name,Last Name,Email,Gender,Contact,Country,DOB\n");
            for (Customer c : customers) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                        c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(),
                        c.getGender(), c.getContactNo(), c.getCountry(), c.getDob()));
            }
            writer.close();

            // Return file as response
            Resource fileResource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}

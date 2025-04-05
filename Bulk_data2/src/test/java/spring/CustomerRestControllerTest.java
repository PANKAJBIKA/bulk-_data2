package spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import spring.entity.Customer;
import spring.repo.CustomerRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CustomerRestControllerTest {

    private CustomerRepository customerRepo;
    private CustomerRestController controller;

    @BeforeEach
    void setup() {
        customerRepo = mock(CustomerRepository.class);
        controller = new CustomerRestController();
        controller.customerRepo = customerRepo; // Injecting mock repo
    }

    @Test
    void testDownloadCsvReturnsValidFile() throws Exception {
        // Prepare mock data
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setFirstName("John");
        mockCustomer.setLastName("Doe");
        mockCustomer.setEmail("john@example.com");
        mockCustomer.setGender("Male");
        mockCustomer.setContactNo("1234567890");
        mockCustomer.setCountry("USA");
        mockCustomer.setDob(LocalDate.of(1990, 1, 1));

        when(customerRepo.findAll()).thenReturn(Collections.singletonList(mockCustomer));

        // Act
        ResponseEntity<Resource> response = controller.downloadCsv();

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("customers_data.csv");
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("text/csv");
        assertThat(response.getBody()).isNotNull();

        // Clean up generated file
        Path filePath = Path.of("uploads/customers_data.csv");
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}

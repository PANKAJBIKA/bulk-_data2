package spring.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import spring.entity.Customer;
@EnableJpaRepositories
public interface CustomerRepository extends JpaRepository<Customer, Serializable> {

	
}

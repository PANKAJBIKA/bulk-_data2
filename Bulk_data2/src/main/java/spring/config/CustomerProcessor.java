package spring.config;

import org.springframework.batch.item.ItemProcessor;

import spring.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {
		return item;
	}

}

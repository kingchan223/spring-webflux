package com.example.demo.web;

import com.example.demo.CustomerRepository;
import com.example.demo.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@SpringBootTest
public class CustomerControllerTest {

    @Autowired
    CustomerRepository customerRepository;


    @Test
    public void 한건찾기_테스트(){
        Flux<Customer> all = customerRepository.findAll();
        System.out.println(all);
    }
}

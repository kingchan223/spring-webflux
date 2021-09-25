package com.example.demo.web;

import com.example.demo.CustomerRepository;
import com.example.demo.domain.Customer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sink;

    // < sink>
    // A요청 -> 리ㅕㅌ

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping("/flux")
    public Flux<Integer> flux(){//모았다가 한방에 응답
        return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value="/fluxStream", produces= MediaType.APPLICATION_STREAM_JSON_VALUE)//하나씩 버퍼에서 플러쉬
    public Flux<Integer> fluxStream(){
        return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/customer", produces= MediaType.APPLICATION_STREAM_JSON_VALUE)//데이터 여러건이면 flux 반환
    public Flux<Customer> findAll(){

        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/customer/{id}")//데이터 한건이면 mono 반환
    public Mono<Customer> findById(@PathVariable Long id){
        return customerRepository.findById(id).log();
    }

    @GetMapping(value="/customer/sse")//SSE는 연결 계속 유지 , produces=MediaType.TEXT_EVENT_STREAM_VALUE 생략가능
    public Flux<ServerSentEvent<Customer>> findAllSSE(){
        return sink.asFlux().map(c -> ServerSentEvent.builder(c).build()).doOnCancel(()->{
            sink.asFlux().blockLast();
        });
    }

    @PostMapping("/customer")
    public Mono<Customer> save(){
        return customerRepository.save(new Customer("lea", "seadox")).doOnNext(sink::tryEmitNext);
    }
}

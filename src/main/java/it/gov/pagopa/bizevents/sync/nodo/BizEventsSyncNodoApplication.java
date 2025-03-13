package it.gov.pagopa.bizevents.sync.nodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class BizEventsSyncNodoApplication {

  public static void main(String[] args) {
    SpringApplication.run(BizEventsSyncNodoApplication.class, args);
  }
}

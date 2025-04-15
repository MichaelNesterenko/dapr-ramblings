package mn.dapr.demo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootApplication @Slf4j
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RestController
    static class Endpoints {

        final String daprHost = "http://localhost:3500";

        final WebClient webClient;
        final DaprClient daprClient;
        final String targetService;

        public Endpoints(@Value("${target-service}") String targetService) {
            this.targetService = targetService;
            this.webClient = WebClient.builder()
                .baseUrl(daprHost)
                .defaultHeader("dapr-app-id", targetService)
                .build();
            this.daprClient = new DaprClientBuilder().build();
        }

        @PostMapping("/entrypoint") Mono<String> entrypoint() {
            log.info("request to target-service {}", targetService);
            return webClient.post().uri("/ping")
                .retrieve()
                .bodyToMono(String.class);
        }

        @PostMapping("/ping") Mono<String> ping() {
            log.info("ping request");
            return
                daprClient
                    .invokeBinding("log-db", "exec", null, Map.of("sql", "insert into ping_log (tst) values (now())"))
                    .then(Mono.just("pong"));
        }
    }
}

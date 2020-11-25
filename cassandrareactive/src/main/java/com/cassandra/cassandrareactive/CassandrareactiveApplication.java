package com.cassandra.cassandrareactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableReactiveCassandraRepositories(basePackages = "com.cassandra.cassandrareactive")
public class CassandrareactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CassandrareactiveApplication.class, args);
    }

}


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "user")
class CassandraUser {

    @PrimaryKey
    @Id
    private Long id;
    private String name;

}


@Repository
interface CassandraUserRepo extends ReactiveCassandraRepository<CassandraUser, Long> {

}

@RestController
@RequiredArgsConstructor
class CassandraUserController {

    private final CassandraUserRepo repo;

    @GetMapping("/users")
    Flux<CassandraUser> users() {
        return repo.findAll();
    }

}


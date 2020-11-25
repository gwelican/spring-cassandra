package com.cassandra.cassandranonreactive;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.config.EnableCassandraAuditing;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableCassandraAuditing
@EnableCassandraRepositories(basePackages = "com.cassandra.cassandranonreactive")
public class CassandraNonreactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CassandraNonreactiveApplication.class, args);
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
interface CassandraUserRepo extends CassandraRepository<CassandraUser, Long> {

}

@RestController
@RequiredArgsConstructor
class CassandraUserController {

    private final CassandraUserRepo repo;

    @GetMapping("/users")
    List<CassandraUser> users() {
        return repo.findAll();
    }

}

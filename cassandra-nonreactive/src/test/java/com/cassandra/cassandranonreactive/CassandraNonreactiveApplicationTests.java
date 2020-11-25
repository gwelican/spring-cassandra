package com.cassandra.cassandranonreactive;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.datastax.driver.core.Cluster;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
class CassandraNonreactiveApplicationTests {


	@Autowired
	CassandraUserRepo repo;

	@Autowired
	MockMvc mockMvc;

	public static final String CASSANDRA_DOCKER_IMAGE_NAME = "cassandra:3";
	public static final String CREATE_KEYSPACE_QUERY = "CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class':'SimpleStrategy','replication_factor':'1'};";

	static CassandraContainer<?> cassandra = new CassandraContainer<>(DockerImageName.parse(CASSANDRA_DOCKER_IMAGE_NAME));

	@DynamicPropertySource
	static void cassandraProperties(final DynamicPropertyRegistry registry) {
		cassandra.start();
		final var host = cassandra.getHost();
		final var mappedPort = cassandra.getMappedPort(CassandraContainer.CQL_PORT);
		try (final var cluster = Cluster.builder()
				.addContactPoint(host)
				.withPort(mappedPort)
				.withoutJMXReporting()
				.build()) {
			final var session = cluster.newSession();
			session.execute(CREATE_KEYSPACE_QUERY);
			session.execute("CREATE TABLE test.user(id bigint PRIMARY KEY, name text)");
		}
		registry.add(
				"spring.data.cassandra.contact-points",
				() -> String.format("%s:%d", host, mappedPort));
	}


	@Test
	void testJoshIsThere() throws Exception {
		repo.save(new CassandraUser(1L, "Josh"));

		this.mockMvc.perform(get("/users"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath(".name", contains("Josh")));

	}

}

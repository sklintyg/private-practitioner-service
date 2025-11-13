package se.inera.intyg.privatepractitionerservice.integrationtest.environment;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class Containers {

  public static MockServerContainer mockServerContainer;
  public static GenericContainer<?> redisContainer;

  public static void ensureRunning() {
    mockServerContainer();
    redisContainer();
  }

  private static void mockServerContainer() {
    if (mockServerContainer == null) {
      mockServerContainer = new MockServerContainer(
          DockerImageName.parse("mockserver/mockserver:5.15.0")
      );
    }

    if (!mockServerContainer.isRunning()) {
      mockServerContainer.start();
    }

    final var mockServerContainerHost = mockServerContainer.getHost();
    final var mockServerContainerPort = String.valueOf(mockServerContainer.getServerPort());

    System.setProperty("integration.intygproxyservice.baseurl",
        "http://%s:%s".formatted(mockServerContainerHost, mockServerContainerPort)
    );
  }

  private static void redisContainer() {
    if (redisContainer == null) {
      redisContainer = new GenericContainer<>(
          DockerImageName.parse("redis:6.0.9-alpine")
      ).withExposedPorts(6379);
    }

    if (!redisContainer.isRunning()) {
      redisContainer.start();
    }

    System.setProperty("spring.data.redis.host", redisContainer.getHost());
    System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString());
  }
}

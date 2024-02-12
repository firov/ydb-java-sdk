package tech.ydb.test.integration.docker;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.UUID;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import org.testcontainers.utility.ResourceReaper;

import tech.ydb.test.integration.YdbEnvironment;
import tech.ydb.test.integration.utils.PortsGenerator;


/**
 *
 * @author Alexandr Gorshenin
 */
public class YdbDockerContainer extends GenericContainer<YdbDockerContainer> {
    private final YdbEnvironment env;
    private final int grpcsPort; // Secure connection
    private final int grpcPort;  // Non secure connection

    public YdbDockerContainer(YdbEnvironment env, PortsGenerator portsGenerator) {
        super(env.dockerImage());

        this.env = env;

        grpcsPort = portsGenerator.findAvailablePort();
        grpcPort = portsGenerator.findAvailablePort();

        addExposedPort(grpcPort);
        addExposedPort(grpcsPort);

        // Host ports and container ports MUST BE equal - ydb implementation limitation
        addFixedExposedPort(grpcsPort, grpcsPort);
        addFixedExposedPort(grpcPort, grpcPort);

        withEnv("GRPC_PORT", String.valueOf(grpcPort));
        withEnv("GRPC_TLS_PORT", String.valueOf(grpcsPort));
        withEnv("YDB_USE_IN_MEMORY_PDISKS", "true");

        withReuse(env.dockerReuse());

        String id = "ydb-" + UUID.randomUUID();

        withLabel("com.docker.ydb.id", id);

        withCreateContainerCmdModifier(modifier -> modifier
                .withName(id)
                .withHostName(getHost()));

        waitingFor(Wait.forHealthcheck());

        // Register container cleaner
        ResourceReaper.instance().registerLabelsFilterForCleanup(Collections.singletonMap(
            "com.docker.ydb.id", id
        ));
    }

    public String nonSecureEndpoint() {
        return String.format("%s:%s", getHost(), grpcPort);
    }

    public String secureEndpoint() {
        return String.format("%s:%s", getHost(), grpcsPort);
    }

    public byte[] pemCert() {
        return copyFileFromContainer(env.dockerPemPath(), is -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(is, baos);
            return baos.toByteArray();
        });
    }

    public String database() {
        return env.dockerDatabase();
    }
}

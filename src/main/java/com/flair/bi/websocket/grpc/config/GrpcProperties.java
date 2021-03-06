package com.flair.bi.websocket.grpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "grpc", ignoreUnknownFields = false)
@Data
public class GrpcProperties {

	private Server server = new Server();
	private Tls tls = new Tls();
	private Integer port;

	@Data
	public static class Server {
		@Deprecated
		private String serviceName;
		private String engineServiceName;
		private String notificationsServiceName;
	}

	@Data
	public static class Tls {
		private boolean enabled = false;
		private String rootCertificate;
		private String clientCertChainFile;
		private String clientPrivateKeyFile;
		private String trustCertCollectionFile;
		private String notificationsClientCertChainFile;
		private String notificationsClientPrivateKeyFile;
		private String notificationsTrustCertCollectionFile;
	}

}

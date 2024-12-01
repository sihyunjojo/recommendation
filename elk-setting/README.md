# logstash jdbc 설치 
```bash
wget https://mariadb.com/downloads/connectors/connectors-data-access/java8-connector/mariadb-java-client-3.4.1.jar
```

# 포팅 방법

먼저 현재 시스템에 설치된 Java 버전을 확인합니다:
Copyjava -version

JAVA_HOME 환경 변수가 올바르게 설정되어 있는지 확인합니다:
Copyecho $JAVA_HOME

Java 17이면 
JAVA_HOME을 Java 17의 경로로 설정합니다:
Copyexport JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

이 설정을 영구적으로 만들기 위해 ~/.bashrc 파일에 추가합니다:
Copyecho 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc

새로운 JAVA_HOME 설정을 확인합니다:
Copyecho $JAVA_HOME

이제 올바른 경로로 CA 인증서를 Java 트러스트스토어에 추가합니다:
Copysudo keytool -importcert -file /home/sihyun/certs/ca/ca.crt -alias elasticsearch -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

인증서 추가에 성공했다면, Java 애플리케이션의 실행 스크립트나 서비스 파일에 다음 JVM 옵션을 추가하세요:
Copy-Djavax.net.ssl.trustStore=$JAVA_HOME/lib/security/cacerts
-Djavax.net.ssl.trustStorePassword=changeit

```azure
export JAVA_OPTS="-Djavax.net.ssl.trustStore=$JAVA_HOME/lib/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit"
java $JAVA_OPTS -jar your-application.jar
```

ElasticsearchService 클래스에서 createHttpClient 메소드를 다음과 같이 수정하세요:
javaCopyprivate CloseableHttpClient createHttpClient() throws Exception {
SSLContextBuilder sslBuilder = new SSLContextBuilder();
sslBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

    return HttpClients.custom()
            .setSSLContext(sslBuilder.build())
            .setSSLHostnameVerifier(new DefaultHostnameVerifier())
            .setDefaultCredentialsProvider(getCredentialsProvider())
            .build();
}

private CredentialsProvider getCredentialsProvider() {
CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(new AuthScope(elasticsearchHost, Integer.parseInt(elasticsearchPort)),
new UsernamePasswordCredentials(elasticsearchUserName, elasticsearchPassword));
return credentialsProvider;
}

애플리케이션의 빌드 설정(예: pom.xml이나 build.gradle)에서 Java 버전이 17로 설정되어 있는지 확인하세요.
변경사항을 적용한 후 애플리케이션을 다시 빌드하고 실행하세요.

이렇게 하면 Java 환경이 올바르게 설정되고, CA 인증서가 Java의 신뢰 저장소에 추가되어 Elasticsearch와의 SSL 연결 문제가 해결될 것입니다. 만약 여전히 문제가 발생한다면, Elasticsearch 서버의 로그와 애플리케이션의 로그를 확인하여 더 자세한 오류 정보를 얻을 수 있습니다.

```azure
sihyun@slave1:~$ nohup java $JAVA_OPTS -jar recommendation-0.0.1-SNAPSHOT.jar &
```
keytool -genkeypair \
  -storepass server_password \
  -keyalg RSA \
  -keysize 2048 \
  -dname "CN=server" \
  -alias server \
  -ext SAN=DNS:localhost,IP:127.0.0.1 \
  -keystore server.keystore \
  && \
cp server.keystore \
  ../../../../../../gateway-service/src/main/resources/META-INF/resources/server.truststore
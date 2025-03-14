@echo off
java -Xms4G -Xmx4G -classpath ./target/classes org.webproxy.client.ClientProxy --port 3000 --remote-server localhost:8080 --max-threads-pool 60
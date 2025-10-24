@echo off
cd %~dp0
java -Xms4G -Xmx4G -classpath ./target/classes org.webproxy.server.ServerProxy --port 8080 --max-threads-pool 60

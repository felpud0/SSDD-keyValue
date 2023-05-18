backend-restexterno:
	(cd es.um.sisdist.backend.restexterno \
		&& mvn -DskipTests clean package)

containers:
	(cd es.um.sisdist.backend.restexterno \
		&& docker build -t dsevilla/ssdd-backend-restexterno:1.0 .)

clean:
	docker rmi -f dsevilla/ssdd-backend-restexterno:1.0

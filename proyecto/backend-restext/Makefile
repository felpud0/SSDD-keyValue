backend-restext:
	(cd restexterno \
		&& mvn -DskipTests clean package)

containers:
	(cd restexterno \
		&& docker build -t dsevilla/ssdd-backend-restext:1.0 .)

clean:
	docker rmi -f dsevilla/ssdd-backend-restext:1.0

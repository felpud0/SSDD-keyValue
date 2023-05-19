# SSDD

Código para la asignatura Sistemas Distribuidos de la UMU, curso 22-23.

# Requisitos
Se tiene que realizar un make sobre el directorio en el que se encuentre.
A continuación, seguir con el apartado de Ejecución.

# Ejecución
Para ejecutar el proyecto, ejecuta en la carpeta proyecto: 
````
make;
docker-compose -f docker-compose-devel-mongo.yml up;
````

Para ejecutar cuando se han realizado cambios en el código, ejecuta en la carpeta proyecto: 
````
make;
docker-compose -f docker-compose-devel-mongo.yml up --build;
````

# Prueba
Probar poniendo en la terminal
`````
curl -v -XPOST -HContent-type:application/json -HAccept:application/json localhost:8080/Service/checkLogin --data '{"email": "dsevilla@um.es", "password": "admin"}'
`````

# Docker Swarm y Stack
Para ejecutarlo en Swarm y Stack, tendremos que ejecutar lo siguiente:
`````
docker swarm init
`````
Cargamos el fichero para ejecutar los Dockers.
`````
docker-compose -f docker-compose-devel-mongo.yml up --build;
`````
Inicio se hace con docker swarm init.
`````
docker service create -- name registry -- publish published =5000 , target =5000 registry :2
`````
Vemos aquí los Image ID
`````
docker image ls
`````
Asignamos a cada uno su tag
`````
docker tag 629535dcc91a 127.0.0.1:5000/ssdd-frontend

docker tag 45f89b6b3cf3 127.0.0.1:5000/db-mongo

docker tag 59cbe3732882 127.0.0.1:5000/backend-grpc

docker tag 80cd502ccb2c 127.0.0.1:5000/backend-restexterno

docker tag 071de23e71c4 127.0.0.1:5000/backend-rest
`````
Subimos cada contenedor.
`````
docker push 127.0.0.1:5000/ssdd-frontend

docker push 127.0.0.1:5000/db-mongo

docker push 127.0.0.1:5000/backend-grpc

docker push 127.0.0.1:5000/backend-rest

docker push 127.0.0.1:5000/backend-restexterno
`````

Tendremos que cambiar ahora el fichero del docker-compose.yml.

Y a continuación, se realizará el despliegue de la siguiente forma:
`````
docker stack deploy -c docker-compose-devel-mongo-swarm.yml ssdd
`````
# SSDD

Código para la asignatura Sistemas Distribuidos de la UMU, curso 22-23.

# Requisitos
Se tiene que realizar un make sobre el directorio en el que se encuentre.
A continuación, seguir con el apartado de Ejecución.

# Ejecución
Para ejecutar el proyecto, ejecuta en la carpeta proyecto: 
````
make;
docker-compse -f docker-compose-devel.yml up;
````

Para ejecutar cuando se han realizado cambios en el código, ejecuta en la carpeta proyecto: 
````
make;
docker-compse -f docker-compose-devel.yml up --build;
````

# Prueba
Probar poniendo en la terminal
`````
curl -v -XPOST -HContent-type:application/json -HAccept:application/json localhost:8080/Service/checkLogin --data '{"email": "dsevilla@um.es", "password": "admin"}'
`````


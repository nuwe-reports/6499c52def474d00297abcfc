# Containerization of the app and the persistence

### Create teh mySQL image and run the mySQL container

- Docker command to build FIRST the mySql image
````shell
docker build -t accenture_db -f Dockerfile.mysql .
````

- Now we have to RUN the container using this image
````shell
docker run --name accenture_db -p 3306:3306 -d accenture_db
````

*** Exposing in the port 3306 in the local machine, we can test our database in mySQLWorkbench (or others).

- With this command we can see the IPAddress used by the mySQL instance into the Docker (it must be 172.17.0.2)
````shell
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' accenture_db 


### Create teh App image and run the App container

- Docker command to build the App image
````shell
docker build -t accenture_app -f Dockerfile.maven .
````

- Docker command to run the app container
````shell
docker run -d --name accenture_app -e MYSQL_USER=root -e MYSQL_ROOT_PASSWORD=root -p 8080:8080 accenture_app
````

*** Exposing in the port 8080 in the local machine, we can test our application in postman.

!!! The next step is use Kubernetes to orchestrate these containers - I'll implement it soon
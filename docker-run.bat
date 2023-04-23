docker build -t sever/portfolio-api:1.0 .

docker run -p 9081:8081 -v %cd%/docker-data:/var/data --name portfolio-api sever/portfolio-api:1.0
#!/bin/bash

# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Create app directory
mkdir -p ~/flames-app
cd ~/flames-app

# Create docker-compose.yml
cat > docker-compose.yml << 'EOL'
version: '3.8'

services:
  app:
    image: ${DOCKERHUB_USERNAME}/flames-game:latest
    container_name: flames_app
    depends_on:
      - postgres
    environment:
      - DB_URL=jdbc:postgresql://postgres:5432/mydatabase
      - DB_USER=postgres
      - DB_PASSWORD=postgres
    ports:
      - "8080:8080"
    restart: always

  postgres:
    image: postgres:latest
    container_name: my_postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: mydatabase
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: always

volumes:
  postgres_data:
EOL

# Start the application
docker-compose pull
docker-compose up -d 
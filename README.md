# FLAMES Game with PostgreSQL

A Java application that implements the FLAMES game and stores results in PostgreSQL.

## Deployment Instructions

### 1. Render.com Deployment (Free Tier)

1. Sign up for a free account at https://render.com
2. Connect your GitHub repository to Render
3. Click "New +" and select "Blueprint"
4. Select your repository
5. Render will automatically detect the `render.yaml` file and set up:
   - A web service for the Java application
   - A PostgreSQL database
   - All necessary environment variables

### 2. Access the Application

Once deployed, your application will be available at:
```
https://flames-game.onrender.com
```

### 3. Monitoring

You can monitor your application through the Render dashboard:
- View application logs
- Check database status
- Monitor resource usage

## Development

### Prerequisites
- Java 11
- Maven
- Docker
- Docker Compose

### Local Development
1. Build the application:
```bash
mvn clean package
```

2. Run with Docker Compose:
```bash
docker-compose up -d
```

### Running Tests
```bash
mvn test
```

## Security Notes
- Database credentials are automatically managed by Render
- Environment variables are securely stored
- Automatic SSL/TLS is provided by Render
- Logs are available through the Render dashboard 
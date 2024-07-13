# Define variables
DOCKER_IMAGE_NAME=jumbo-locator
DOCKER_IMAGE_TAG=latest
COMPOSE_FILE=docker-compose.yaml

# Build Docker image
.PHONY: build
build:
	@echo "Building Docker image..."
	docker build -t $(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG) .

# Push Docker image to Docker Hub (Optional)
.PHONY: push
push:
	@echo "Pushing Docker image to Docker Hub..."
	docker tag $(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG) your-dockerhub-username/$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)
	docker push your-dockerhub-username/$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)

# Start the application using Docker Compose
.PHONY: up
up: build
	@echo "Starting application using Docker Compose..."
	docker-compose -f $(COMPOSE_FILE) up --build

# Stop the application and remove Docker Compose resources
.PHONY: down
down:
	@echo "Stopping application and removing Docker Compose resources..."
	docker-compose -f $(COMPOSE_FILE) down -v

# View logs
.PHONY: logs
logs:
	@echo "Viewing logs..."
	docker-compose -f $(COMPOSE_FILE) logs -f

# Full redeployment cycle
.PHONY: redeploy
redeploy: down up

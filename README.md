# Building the spingboot jar
./mvnw -DskipTests package

#Building Docker image
docker build -t cloudez-inventory-api .

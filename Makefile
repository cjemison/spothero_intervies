.PHONY: clean test install

clean:
	./gradlew clean

javaBuild:
	./gradlew clean build

docker:
	./gradlew clean build
	docker-compose up --build -d

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENV JAVA_OPTS="-Xmx2048m -Xms512m"

CMD ["sh", "-c", "java -Dspring.data.mongodb.uri=${MONGO_URI} -Djwt.secret=${JWT_SECRET} -Dspring.mail.username=${SPRING_MAIL_USERNAME} -Dspring.mail.password=${SPRING_MAIL_PASSWORD} -jar target/NFL-NBAAnalytics-0.0.1-SNAPSHOT.jar"]
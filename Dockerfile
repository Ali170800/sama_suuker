# Étape 1 : Build du projet avec Maven et Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution sur un serveur Tomcat officiel compatible Java 21
FROM tomcat:10.1-jdk21-temurin

# Supprimer les applications par défaut de Tomcat pour libérer la racine
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier explicitement le fichier s'appelant samasuker-1.0-SNAPSHOT.war vers ROOT.war
COPY --from=build /app/target/samasuker-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Exposer le port par défaut de Tomcat
EXPOSE 8080

# Démarrer Tomcat
CMD ["catalina.sh", "run"]
# Étape 1 : Build du projet avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution sur un serveur Tomcat officiel
FROM tomcat:10.1-jdk17-temurin

# Supprimer les applications par défaut de Tomcat pour libérer la racine
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le fichier WAR généré par Maven dans le dossier webapps de Tomcat en le renommant ROOT.war
# (Cela permet à l'application de se lancer directement à la racine de l'URL, ex: https://votre-site.onrender.com/)
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Exposer le port par défaut de Tomcat
EXPOSE 8080

# Démarrer Tomcat
CMD ["catalina.sh", "run"]
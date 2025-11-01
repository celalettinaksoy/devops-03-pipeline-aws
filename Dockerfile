# Uygalanin calismasi için JDK lazim
FROM ubuntu/jre:17_edge

# projenin jar dosyasi nerede?
ARG JAR_FILE=target/*.jar

# projenin jar halini docker icine kopyala
COPY ${JAR_FILE} devops-application.jar

# uygulamalarin ic portunu sabitle
EXPOSE 8080

# uygulamayi Java komutla calistir
ENTRYPOINT ["java", "-jar", "devops-application.jar"]

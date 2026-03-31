FROM openjdk:21
WORKDIR /home/kymokim/desktop-dictionary
COPY /build/libs/desktop-dictionary-0.0.1-SNAPSHOT.jar /home/kymokim/desktop-dictionary/desktop-dictionary.jar
ENTRYPOINT ["java","-jar","desktop-dictionary.jar"]
FROM maven:latest AS builder

RUN mkdir /work
COPY src/ /work/src
COPY templates/ /work/templates
COPY pom.xml *.semtrail /work/
RUN mkdir /work/site
RUN mkdir /work/site/nodes

WORKDIR /work
RUN ls -lR /work

RUN mvn compile assembly:single

RUN java -jar target/semtrail-jar-with-dependencies.jar Architektur.semtrail templates site
RUN ls -lR /work/site

FROM nginx:latest
COPY --from=builder /work/site/ /usr/share/nginx/html/

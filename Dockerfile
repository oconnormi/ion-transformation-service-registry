# Copyright (c) Connexta
#
# <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
# Lesser General Public License as published by the Free Software Foundation, either version 3 of
# the License, or any later version.
#
# <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
# License is distributed along with this program and can be found at
# <http://www.gnu.org/licenses/lgpl.html>.

FROM openjdk:8-jre-alpine
WORKDIR /transformation-service-registry-app
# use version from command line argument
ARG VERSION
COPY target/ion-transformation-service-registry-${VERSION}.jar ion-transformation-service-registry.jar
COPY target/classes/application.properties application.properties
ENTRYPOINT ["java",\
    "-Djava.security.egd=file:/dev/./urandom",\
    "-jar",\
    "ion-transformation-service-registry.jar"]

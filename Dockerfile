# precondition: jar should be assembled with './gradlew clean assemble unpack'
# build with 'docker build -f Dockerfile -t ghaege/pdf-converter build/exploded'
#       or   'docker build --build-arg="BASE_IMAGE=registry.c4.dev/debian:stable-20240513" -t ghaege/pdf-converter build/exploded'
# performance ca. 20 millis/500 pdf's

#  ---------------------------------- debian ------
ARG BASE_IMAGE=debian:stable-20240513
FROM ${BASE_IMAGE} AS base

#  ---------------------------------- libreoffice ------
RUN apt-get update && apt-get -y install \
        openjdk-17-jre \
        apt-transport-https locales-all libpng16-16 libxinerama1 libgl1-mesa-glx libfontconfig1 libfreetype6 libxrender1 \
        libxcb-shm0 libxcb-render0 adduser cpio findutils \
        # procps needed for us finding the libreoffice process, see https://github.com/sbraconnier/jodconverter/issues/127#issuecomment-463668183
        procps \
    # sid variant
    && apt-get -y install libreoffice --no-install-recommends \
    # fonts
    && apt-get -y install fonts-liberation \
    && rm -rf /var/lib/apt/lists/*

#  ---------------------------------- spring boot app ------
# create app dir and cd to it
WORKDIR /app

# copy the exploded app
COPY . .

EXPOSE 8100

# via JarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

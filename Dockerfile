#  ---------------------------------- debian ------
FROM debian:stable as pdf-converter
# FROM debian:sid as pdf-converter

# debian:stable with LibreOffice 7.0.4.2 00(Build:2) 17-19 millis/500 pdf's
# debian:sid    with LibreOffice 7.4.5.1 40(Build:1) 18-20 millis/500 pdf's
# performance is nearly the same (by 2023-03), stable image is a bit smaller > stable

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
    # unzip for exlode war
    && apt-get -y install unzip
    # && rm -rf /var/lib/apt/lists/*

#  ---------------------------------- spring boot app ------
#  prepare spring boot app config
RUN mkdir -p /etc/app \
  && touch /etc/app/application.properties

# create app dir and cd to it
WORKDIR /opt/app

# copy war to app and explode
ENV WAR_FILE=pdf-converter-1.0.4.war
COPY build/libs/$WAR_FILE .
RUN unzip $WAR_FILE && rm $WAR_FILE

EXPOSE 8100

# via WarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.WarLauncher", "--spring.config.additional-location=/etc/app/"]



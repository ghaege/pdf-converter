#  ---------------------------------- debian ------
# FROM debian:stable as pdf-converter
# debian:sid with libre office 7.0.x is faster than debian:stable with libre office 6.1.x (by 2020-12)
FROM debian:sid as pdf-converter

#  ---------------------------------- libreoffice ------
RUN apt-get update && apt-get -y install \
        openjdk-11-jre \
        apt-transport-https locales-all libpng16-16 libxinerama1 libgl1-mesa-glx libfontconfig1 libfreetype6 libxrender1 \
        libxcb-shm0 libxcb-render0 adduser cpio findutils \
        # procps needed for us finding the libreoffice process, see https://github.com/sbraconnier/jodconverter/issues/127#issuecomment-463668183
        procps \
    # sid variant
    && apt-get -y install libreoffice --no-install-recommends \
    # fonts
    && apt-get -y install fonts-liberation
    # && rm -rf /var/lib/apt/lists/*


#  ---------------------------------- spring boot app ------
#  prepare spring boot app config
RUN mkdir -p /etc/app \
  && touch /etc/app/application.properties

# create app dir and cd to it
WORKDIR /opt/app

# copy the exploded war app
COPY build/exploded/ .

# via WarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.WarLauncher", "--spring.config.additional-location=/etc/app/"]

EXPOSE 8100


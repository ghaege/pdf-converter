#  ---------------------------------- debian ------
# FROM debian:stable as pdf-converter
# debian:sid with libre office 7.0.x is faster (ca. 80ms for simple .docx) than debian:stable with libre office 6.1.x (ca. 100ms for simple .docx)
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
    && rm -rf /var/lib/apt/lists/*


#  ---------------------------------- spring boot app ------
ENV JAR_FILE_NAME=app.jar
ENV JAR_FILE_BASEDIR=/opt/app
ENV LOG_BASE_DIR=/var/log

#  prepare spring boot app config
RUN mkdir -p ${JAR_FILE_BASEDIR} /etc/app \
  && touch /etc/app/application.properties

# copy the pdf-converter-*.jar from build/libs
COPY build/libs/pdf-converter-*.jar ${JAR_FILE_BASEDIR}/${JAR_FILE_NAME}
 
CMD ["java", "-jar", "/opt/app/app.jar", "--spring.config.additional-location=/etc/app/"]

EXPOSE 8100

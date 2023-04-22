// via pipeline script (to test pipeline works)
pipeline {
  agent any
  
  environment {
    // on macosx extend PATH for local docker used in int/e2eTest's
    PATH = "/usr/local/bin:$PATH"
  }
  
  stages {
    /*
    stage('checkout') {
      steps {
        // branch must be local
        git branch: 'main', url: 'ssh://git@github.com/ghaege/pdf-converter.git'
      }
    }
    */

    stage('build, test & assemble') {
      steps { 
        sh './gradlew --no-build-cache clean build'
        sh './gradlew assemble unpack'
      }
    }

    stage('intTest') {
      steps {
        sh './gradlew integrationTest'
      }
    }

    stage('e2eTest') {
      steps {
        sh './gradlew e2eTest'
      }
    }
  }
}

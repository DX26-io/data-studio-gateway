pipeline {
    agent any
    stages {
        stage('Checkout SCM') {
          steps {
            cleanWs()
            checkout scm
          }
        }

        stage('Fetch Config') {
          steps {
            sh 'aws s3 cp s3://dx26-ci-cd/config/global/settings.xml .'
            sh 'ls .'
          }
        }

      stage('Test and Package') {
        agent {
          docker {
            image 'openjdk:8-jdk-slim'
            reuseNode true
          }
        }
        steps {
          script {
            echo '[INFO] Test and Package'
            sh 'java -version'
            if (env.BRANCH_NAME == 'master') {
              sh './mvnw -s settings.xml clean package'
              // withCredentials([usernamePassword(
              //   credentialsId: 'github',
              //   usernameVariable: 'GIT_CREDS_USR',
              //   passwordVariable: 'GIT_CREDS_PSW'
              // )]) { sh 'mvn -s settings.xml -B release:clean release:prepare release:perform -Dusername=$GIT_CREDS_USR -Dpassword=$GIT_CREDS_PSW' }
            } else {
              sh './mvnw -s settings.xml clean package'
            }
          }
        }
      }
    }
}

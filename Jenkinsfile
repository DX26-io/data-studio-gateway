// node {

//     stage('Checkout SCM') {
//         cleanWs()
//         checkout scm
//     }

//     stage('Fetch Config') {
//       sh 'git branch --show-current'
//       sh 'aws s3 cp s3://dx26-ci-cd/config/global/settings.xml .'
//     }

//     if (env.BRANCH_NAME == 'master') {
//         stage('Deploy Artifact') {
//             echo '[INFO] Checking out master'
//             sh 'git checkout -f master'
//             echo '[INFO] Deploy Artifacts'
//             sh 'mvn -s settings.xml clean package'
//             // withCredentials([usernamePassword(
//             //     credentialsId: 'github',
//             //     usernameVariable: 'GIT_CREDS_USR',
//             //     passwordVariable: 'GIT_CREDS_PSW'
//             // )]) { sh 'mvn -B release:clean release:prepare release:perform -Dusername=$GIT_CREDS_USR -Dpassword=$GIT_CREDS_PSW' }
//         }
//     } else {
//         stage('Test and Package') {
//             echo '[INFO] Test and Package'
//             sh 'mvn -s settings.xml clean package'
//         }
//         stage('Publish results') {
//             echo '[INFO] [TODO] Publish tests'
//         }
//     }
// }

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
            image 'maven:3.8-adoptopenjdk-8'
            reuseNode true
          }
        }
        steps {
          script {
            echo '[INFO] Test and Package'
            sh 'java -version'
            if (env.BRANCH_NAME == 'master') {
              sh 'mvn -s settings.xml clean package'
              // withCredentials([usernamePassword(
              //   credentialsId: 'github',
              //   usernameVariable: 'GIT_CREDS_USR',
              //   passwordVariable: 'GIT_CREDS_PSW'
              // )]) { sh 'mvn -B release:clean release:prepare release:perform -Dusername=$GIT_CREDS_USR -Dpassword=$GIT_CREDS_PSW' }
            } else {
              sh 'mvn -s settings.xml clean package'
            }
          }
        }
      }
    }
}

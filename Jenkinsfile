node {

    stage('Checkout SCM') {
        cleanWs()
        echo 'Clone SCM'
        checkout scm
    }

    if (env.BRANCH_NAME == 'master') {
        stage('Deploy Artifact') {
            echo '[INFO] Checking out master'
            sh 'git checkout -f master'
            sh 'git branch --show-current'
            echo '[INFO] Deploy Artifacts'
            // withCredentials([usernamePassword(
            //     credentialsId: 'github',
            //     usernameVariable: 'GIT_CREDS_USR',
            //     passwordVariable: 'GIT_CREDS_PSW'
            // )]) { sh 'mvn -B release:clean release:prepare release:perform -Dusername=$GIT_CREDS_USR -Dpassword=$GIT_CREDS_PSW' }
        }
    } else {
        stage('Test and Package') {
            echo '[INFO] Test and Package'
            sh 'git branch --show-current'
            sh 'mvn clean package'
        }
        stage('Publish results') {
            echo '[INFO] [TODO] Publish tests'
        }
    }
}

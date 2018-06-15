pipeline {
    agent {
        docker {
               image 'jmesserli/openjdk-with-docker'
               args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    configFileProvider([
                        configFile(fileId: '03252855-e233-4f1b-b32b-b27c3df40248', targetLocation: 'src/main/kotlin/resources/application.yml')
                    ]) {}
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean assemble'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Docker Push') {
            environment { DOCKER = credentials('docker-deploy') }
            steps {
                sh 'docker login -u "$DOCKER_USR" -p "$DOCKER_PSW" docker.mkweb.me:443'

                sh 'docker build -t docker.mkweb.me:443/m183_security_app:$BUILD_NUMBER -t docker.mkweb.me:443/m183_security_app:latest .'

                sh 'docker push docker.mkweb.me:443/m183_security_app:$BUILD_NUMBER && docker push docker.mkweb.me:443/m183_security_app:latest'
            }
        }
    }
    post {
        always {
            deleteDir()
        }
    }
}
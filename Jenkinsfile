pipeline {
    agent {
        docker {
               image 'jmesserli/openjdk-with-docker'
        }
    }

    stages {
        /*stage('Prepare') {
            steps {
                script {
                    configFileProvider([
                        configFile(fileId: '59d1c401-7bc1-4c14-b8f4-7f0f3aadecd0', targetLocation: 'src/main/kotlin/resources/application.yml')
                    ]) {}
                }
            }
        }*/

        stage('Build') {
            steps {
                sh './gradlew clean assemble'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew clean test'
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
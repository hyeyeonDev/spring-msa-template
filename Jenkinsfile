pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.build('user-service-image')
                }
            }
        }

        stage('Deploy (Docker Compose Up)') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}

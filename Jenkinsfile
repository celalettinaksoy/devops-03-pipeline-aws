// DÜZELTME: 'docker_image' değişkenini tüm aşamalarda kullanabilmek için
// pipeline seviyesinde tanımlıyoruz.
def docker_image

pipeline {
    agent {
        label 'Jenkins-Agent'
    }

    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    environment {
         APP_NAME = "devops-03-pipeline-aws"
         RELEASE = "1.0"
         DOCKER_USER = "floryos"
         DOCKER_LOGIN = "dockerhub-token"
         DOCKER_IMAGE_NAME = "${DOCKER_USER}/${APP_NAME}"
         DOCKER_IMAGE_TAG = "${RELEASE}.${BUILD_NUMBER}"
    }

    stages {

       stage('SCM GitHub') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/celalettinaksoy/devops-03-pipeline-aws']])
            }
        }

        stage('Build Maven') {
            steps {
                sh "mvn clean install"
            }
        }

        stage('Test Maven') {
            steps {
                sh "mvn test"
            }
        }

        stage("SonarQube Analysis") {
            steps {
                script {
                    withSonarQubeEnv(credentialsId: 'jenkins-sonar-token') {
                        if (isUnix()) {
                            sh "mvn sonar:sonar"
                        } else {
                            bat 'mvn sonar:sonar'
                        }
                    }
                }
            }
        }

        stage("Quality Gate"){
           steps {
               script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonar-token'
                }
            }
        }

        // YENİ AŞAMA 1: Sadece Docker imajını build eder.
        stage('Docker Image Build') {
            steps {
                script {
                    // Oluşturulan imajı pipeline seviyesindeki değişkene atıyoruz.
                    docker_image = docker.build("${DOCKER_IMAGE_NAME}")
                }
            }
        }

        // YENİ AŞAMA 2: Build edilen Docker imajını push eder.
        stage('Push Docker Image to DockerHub') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_LOGIN) {
                        docker_image.push("${DOCKER_IMAGE_TAG}")
                        docker_image.push("latest")
                    }
                }
            }
        }

        /*
        stage('Deploy Kubernetes') {
            steps {
                script {
                    kubernetesDeploy (configs: 'deployment-service.yaml', kubeconfigId: 'kubernetes')
                }
            }
        }

        stage('Docker Image to Clean') {
            steps {
                     sh "docker image prune -f"
            }
        }
        */
    }
}
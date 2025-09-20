pipeline {
    // Agent'ı etiketine göre seçiyoruz.
    agent {
        label 'Jenkins-Agent'
    }

    // 'tools' bloğu 'pipeline' seviyesinde olmalı.
    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    environment {
         APP_NAME = "devops-03-pipeline-aws"
         RELEASE = "1.0"
         DOCKER_USER = "floryos"
         DOCKER_LOGIN = "dockerhub-token"
         // DÜZELTME: Environment bloğunda değişkenler bu şekilde birleştirilmelidir.
         // Çift tırnak ve ${} kullanımına gerek yoktur.
         DOCKER_IMAGE_NAME = DOCKER_USER + '/' + APP_NAME
         DOCKER_IMAGE_TAG = "${RELEASE}.${BUILD_NUMBER}"
    }


    // 'stages' bloğu da 'pipeline' seviyesinde olmalı.
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

        // DÜZELTME: 'stage' bloğu mutlaka bir 'steps' bloğu içermelidir.
        // 'sh "mvn test"' komutu 'steps' bloğunun içine alındı.
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
                            // Linux or MacOS
                            sh "mvn sonar:sonar"
                        } else {
                            bat 'mvn sonar:sonar'  // Windows
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

        stage('Build & Push Docker Image to DockerHub') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_LOGIN) {
                        def docker_image = docker.build("${DOCKER_IMAGE_NAME}")
                        docker_image.push("${DOCKER_IMAGE_TAG}")
                        docker_image.push("latest")
                    }
                }
            }
        }

        // --- Diğer aşamalarınız yorum satırı olarak bırakıldı ---
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
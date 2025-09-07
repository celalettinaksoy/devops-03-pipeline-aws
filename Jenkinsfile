pipeline {
    //agent any
    agent {
        Jenkins-Agent {
//     environment {
//         DOCKER_REGISTRY_USER = 'floryos'
//         DOCKER_IMAGE_NAME = 'devops-application'
//     }

    tools {
        maven 'Maven3'
        jdk 'Java21'
    }

    stages {

       stage('SCM GitHub') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/celalettinaksoy/devops-03-pipeline-aws']])
            }
        }

        stage('Test Maven') {
            steps {
                sh "mvn test"
            }
        }

        stage('Build Maven') {
            steps {
                sh "mvn clean install"
            }
        }

//         stage('Docker Image Build') {
//             steps {
//                 // --- DÜZELTME: Çift tırnak ve Windows formatında (%) değişken kullanımı ---
//                 sh "docker build -t %DOCKER_REGISTRY_USER%/%DOCKER_IMAGE_NAME%:latest ."
//             }
//         }
//
//         stage('Docker Image To DockerHub') {
//             steps {
//                 script {
//                     withCredentials([string(credentialsId: 'dockerhub-token', variable: 'DOCKER_TOKEN')]) {
//                         if (isUnix()) {
//                              // --- DÜZELTME: Çift tırnak kullanımı ---
//                              sh "docker login -u ${DOCKER_REGISTRY_USER} -p ${DOCKER_TOKEN}"
//                              sh "docker push ${DOCKER_REGISTRY_USER}/${DOCKER_IMAGE_NAME}:latest"
//                           } else {
//                              // --- DÜZELTME: Çift tırnak ve Windows formatında (%) değişken kullanımı ---
//                              bat "docker login -u %DOCKER_REGISTRY_USER% -p %DOCKER_TOKEN%"
//                              bat "docker push %DOCKER_REGISTRY_USER%/%DOCKER_IMAGE_NAME%:latest"
//                          }
//                     }
//                 }
//             }
//         }
//
//         stage('Deploy Kubernetes') {
//             steps {
//             script {
//                     kubernetesDeploy (configs: 'deployment-service.yaml', kubeconfigId: 'kubernetes')
//                 }
//             }
//         }
//
//         stage('Docker Image to Clean') {
//             steps {
//                      sh "docker image prune -f"
//             }
//         }
    }
}
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
         DOCKER_IMAGE_NAME = "${DOCKER_USER}" + "/" + "${APP_NAME}"
         DOCKER_IMAGE_TAG = "${RELEASE}"."${BUILD_NUMBER}"

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

        stage('Test Maven')
                // 'withMaven' kullanmaya gerek yok çünkü 'tools' direktifi
                // Maven'ı zaten PATH'e ekliyor.
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

        stage('Build & Push Docker Image to DockerHub') {
            steps {
                script {

                    docker.withRegistry('', DOCKER_LOGIN) {

                        docker_image = docker.build "${DOCKER_IMAGE_NAME}"
                        docker_image.push("${DOCKER_IMAGE_TAG}")
                        docker_image.push("latest")
                    }
                }
            }
        }

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
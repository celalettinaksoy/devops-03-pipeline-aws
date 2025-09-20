// Pipeline genelinde kullanılacak 'docker_image' değişkenini tanımlıyoruz.
def docker_image

pipeline {
    // Pipeline'ın çalışacağı Jenkins agent'ını etiketine göre seçiyoruz.
    agent {
        label 'Jenkins-Agent'
    }
    // Pipeline'da kullanılacak araçları (Maven, JDK) tanımlıyoruz.
    tools {
        maven 'Maven3'
        jdk 'Java21'
    }
    // Pipeline boyunca geçerli olacak ortam değişkenlerini ayarlıyoruz.
    environment {
        APP_NAME = "devops-03-pipeline-aws"
        RELEASE = "1.0"
        DOCKER_USER = "floryos"
        DOCKER_LOGIN = "dockerhub-token"
        DOCKER_IMAGE_NAME = "${DOCKER_USER}/${APP_NAME}"
        DOCKER_IMAGE_TAG = "${RELEASE}.${BUILD_NUMBER}"
    }
    stages {
        // 1. KODU GITHUB'DAN ÇEKME: Projenin en güncel kodunu 'main' branch'inden çeker.
        stage('SCM GitHub') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/celalettinaksoy/devops-03-pipeline-aws']])
            }
        }
        // 2. MAVEN BUILD: Projeyi Maven kullanarak derler ve paketler (jar/war dosyası oluşturur).
        stage('Build Maven') {
            steps {
                sh "mvn clean install"
            }
        }
        // 3. MAVEN TEST: Koddaki birim testlerini (unit tests) çalıştırır.
        stage('Test Maven') {
            steps {
                sh "mvn test"
            }
        }
        // 4. SONARQUBE ANALİZİ: Kod kalitesini ve olası hataları analiz etmek için SonarQube'a gönderir.
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
        // 5. KALİTE KONTROLÜ (QUALITY GATE): SonarQube analiz sonuçlarının belirlenen standartlara uyup uymadığını kontrol eder.
        stage("Quality Gate") {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonar-token'
                }
            }
        }
        // 6. DOCKER IMAGE OLUŞTURMA: Uygulamanın Docker imajını build eder.
        stage('Docker Image Build') {
            steps {
                script {
                    // Oluşturulan imajı pipeline seviyesindeki değişkene atıyoruz.
                    docker_image = docker.build("${DOCKER_IMAGE_NAME}")
                }
            }
        }
        // 7. DOCKERHUB'A PUSH'LAMA: Oluşturulan imajı DockerHub'a (veya başka bir registry'ye) gönderir.
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
        // 8. GÜVENLİK TARAMASI (TRIVY): Docker imajındaki bilinen güvenlik zafiyetlerini tarar.
        stage("Trivy Image Scan") {
            steps {
                script {
                    if (isUnix()) {
                        sh ('docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image floryos/devops-03-pipeline-aws:latest --no-progress --scanners vuln  --exit-code 0 --severity HIGH,CRITICAL --format table')
                    } else {
                        bat ('docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image floryos/devops-03-pipeline-aws:latest --no-progress --scanners vuln  --exit-code 0 --severity HIGH,CRITICAL --format table')
                    }
                }
            }
        }
        // 9. AGENT TEMİZLİĞİ: Jenkins Agent üzerinde biriken eski ve gereksiz Docker imajlarını silerek yer açar.
        stage('Cleanup Docker Images') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            # Bu repo için tüm image’leri al, tarihe göre sırala, son 3 hariç sil
                            docker images "${env.DOCKER_IMAGE_NAME}" --format "{{.Repository}}:{{.Tag}} {{.CreatedAt}}" \\
                            | sort -r -k2 \\
                            | tail -n +4 \\
                            | awk '{print \$1}' \\
                            | xargs -r docker rmi -f

                            # Tüm <none> (dangling) imajları temizle
                            docker image prune -f
                        """
                    } else {
                        bat """
                            rem Bu repo için eski imajları sil
                            for /f "skip=3 tokens=1" %%i in ('docker images ${env.DOCKER_IMAGE_NAME} --format "{{.Repository}}:{{.Tag}}" ^| sort') do docker rmi -f %%i

                            rem Tüm <none> (dangling) imajları temizle
                            docker image prune -f
                        """
                    }
                }
            }
        }

        // --- OPSİYONEL: DEPLOY AŞAMASI (ŞU ANDA PASİF) ---
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
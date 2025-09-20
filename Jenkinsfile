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
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: SCM GitHub ✅'
                        echo '=================================================='
                    """
                    checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/celalettinaksoy/devops-03-pipeline-aws']])
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: SCM GitHub 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 2. MAVEN BUILD: Projeyi Maven kullanarak derler ve paketler (jar/war dosyası oluşturur).
        stage('Build Maven') {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Build Maven ✅'
                        echo '=================================================='
                    """
                    sh "mvn clean install"
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Build Maven 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 3. MAVEN TEST: Koddaki birim testlerini (unit tests) çalıştırır.
        stage('Test Maven') {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Test Maven ✅'
                        echo '=================================================='
                    """
                    sh "mvn test"
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Test Maven 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 4. SONARQUBE ANALİZİ: Kod kalitesini ve olası hataları analiz etmek için SonarQube'a gönderir.
        stage("SonarQube Analysis") {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: SonarQube Analysis ✅'
                        echo '=================================================='
                    """
                    withSonarQubeEnv(credentialsId: 'jenkins-sonar-token') {
                        if (isUnix()) {
                            sh "mvn sonar:sonar"
                        } else {
                            bat 'mvn sonar:sonar'
                        }
                    }
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: SonarQube Analysis 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 5. KALİTE KONTROLÜ (QUALITY GATE): SonarQube analiz sonuçlarının belirlenen standartlara uyup uymadığını kontrol eder.
        stage("Quality Gate") {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Quality Gate ✅'
                        echo '=================================================='
                    """
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonar-token'
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Quality Gate 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 6. DOCKER IMAGE OLUŞTURMA: Uygulamanın Docker imajını build eder.
        stage('Docker Image Build') {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Docker Image Build ✅'
                        echo '=================================================='
                    """
                    docker_image = docker.build("${DOCKER_IMAGE_NAME}")
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Docker Image Build 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 7. DOCKERHUB'A PUSH'LAMA: Oluşturulan imajı DockerHub'a (veya başka bir registry'ye) gönderir.
        stage('Push Docker Image to DockerHub') {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Push Docker Image to DockerHub ✅'
                        echo '=================================================='
                    """
                    docker.withRegistry('', DOCKER_LOGIN) {
                        docker_image.push("${DOCKER_IMAGE_TAG}")
                        docker_image.push("latest")
                    }
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Push Docker Image to DockerHub 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 8. GÜVENLİK TARAMASI (TRIVY): Docker imajındaki bilinen güvenlik zafiyetlerini tarar.
        stage("Trivy Image Scan") {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Trivy Image Scan ✅'
                        echo '=================================================='
                    """
                    if (isUnix()) {
                        sh ('docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image floryos/devops-03-pipeline-aws:latest --no-progress --scanners vuln  --exit-code 0 --severity HIGH,CRITICAL --format table')
                    } else {
                        bat ('docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image floryos/devops-03-pipeline-aws:latest --no-progress --scanners vuln  --exit-code 0 --severity HIGH,CRITICAL --format table')
                    }
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Trivy Image Scan 🎉'
                        echo '=================================================='
                    """
                }
            }
        }
        // 9. AGENT TEMİZLİĞİ: Jenkins Agent üzerinde biriken eski ve gereksiz Docker imajlarını silerek yer açar.
        stage('Cleanup Docker Images') {
            steps {
                script {
                    sh """
                        echo '=================================================='
                        echo '     STAGE BAŞLIYOR: Cleanup Docker Images ✅'
                        echo '=================================================='
                    """
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
                    sh """
                        echo '=================================================='
                        echo '     STAGE TAMAMLANDI: Cleanup Docker Images 🎉'
                        echo '=================================================='
                    """
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
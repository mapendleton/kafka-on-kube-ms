//Jenkinsfile (Declarative Pipeline)
pipeline {
    agent { label 'pipes-docker-agent' }

    stages {
        stage('gradle:7.3.3-jdk11 agent stages'){
            agent {
                docker {
                    image 'gradle:7.3.3-jdk11'
                    registryUrl 'https://gapinc-docker-repo.jfrog.io'
                    registryCredentialsId 'pt-services-integration-artifactory-token'
                    reuseNode true
                }
            }
            stages {
                stage('Build/Test') {

                    steps {
                        echo 'Building and Testing with gradle...'
                        sh('''
                            ./gradlew -v
                            ./gradlew clean build
                        ''')
                    }
                }
                stage('Scan using Gradle') {
                    steps {
                        echo 'Running sonar scan...'
                        withSonarQubeEnv(installationName: 'gap-sonar'){
                            sh('''
                                ./gradlew sonarqube \
                                -Dsonar.projectName='kafka-on-kube-ms' \
                                -Dsonar.qualitygate.wait=true \
                            ''')
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy not implemented yet. SERI-61'
            }
        }
    }
}

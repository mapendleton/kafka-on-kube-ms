//Jenkinsfile (Declarative Pipeline)
pipeline {
    agent { label 'pipes-docker-agent' }


    stages {
        stage('Build') {
            agent {
                docker {
                    registryUrl 'https://gapinc-docker-repo.jfrog.io'
                    registryCredentialsId 'pt-services-integration-artifactory-token'
                    image 'gradle:7.3.3-jdk11'
                    reuseNode true
                }
            }
            steps {
                echo 'Building with gradle..'
                sh('''
                    ./gradlew -v
                    ./gradlew clean build
                ''')
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Not implemented yet. SERI-61'
            }
        }
    }
}

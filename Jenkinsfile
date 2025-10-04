pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 21'
    }
    
    environment {
        DOCKER_IMAGE = 'smashorder-backend'
        DOCKER_TAG = "${env.BRANCH_NAME}-${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the application...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        
        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    branch 'quality'
                }
            }
            steps {
                echo "Building Docker image for branch: ${env.BRANCH_NAME}..."
                script {
                    def branchTag = env.BRANCH_NAME
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -f Dockerfile.app .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:${branchTag}-latest
                    """
                    
                    // Tag as 'latest' only for main branch
                    if (env.BRANCH_NAME == 'main') {
                        sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
        
        stage('Deploy') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    branch 'quality'
                }
            }
            steps {
                echo "Deploying application from branch: ${env.BRANCH_NAME}..."
                script {
                    // Different deployment logic based on branch
                    if (env.BRANCH_NAME == 'main') {
                        echo 'Deploying to PRODUCTION environment...'
                        // Add production deployment steps here
                    } else if (env.BRANCH_NAME == 'quality') {
                        echo 'Deploying to QA/QUALITY environment...'
                        // Add QA deployment steps here
                    } else if (env.BRANCH_NAME == 'develop') {
                        echo 'Deploying to DEVELOPMENT environment...'
                        // Add development deployment steps here
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}

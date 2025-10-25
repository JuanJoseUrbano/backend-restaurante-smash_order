pipeline {
    agent any

    tools {
        jdk 'JDK 21'
    }

    environment {
        JAVA_HOME = tool 'JDK 21'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Validate Branch') {
            steps {
                script {
                    def currentBranch = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '')
                    
                    echo "Rama actual: ${currentBranch}"
                    
                    // Definir patrones de ramas permitidas
                    def allowedPatterns = [
                        'develop',
                        'main',
                        'quality',
                        'release',
                        ~/^dev\/HU-\d+$/,           // dev/HU-109, dev/HU-208, etc.
                        ~/^qa\/HU-\d+$/,            // qa/HU-109, qa/HU-110, etc.
                        ~/^release\.\d+\.\d+\.\d+$/, // release.0.0.1
                        ~/^CHU-\d+$/                // CHU-201
                    ]
                    
                    def isAllowed = allowedPatterns.any { pattern ->
                        if (pattern instanceof String) {
                            return currentBranch == pattern
                        } else {
                            return currentBranch ==~ pattern
                        }
                    }
                    
                    if (!isAllowed) {
                        error("Pipeline solo se ejecuta en ramas permitidas. Rama actual: ${currentBranch}")
                    }
                    
                    echo "✓ Rama ${currentBranch} es válida para el pipeline"
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    echo 'Obteniendo código del repositorio...'
                    checkout scm
                }
            }
        }

        stage('Grant Execute Permission') {
            steps {
                script {
                    echo 'Asegurando permisos de ejecución para mvnw...'
                    if (isUnix()) {
                        sh 'chmod +x ./mvnw || true'
                    } else {
                        bat 'echo Windows agent: permisos no requeridos'
                    }
                }
            }
        }

        stage('Compile and Package') {
            steps {
                script {
                    def currentBranch = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '')
                    echo "Compilando y empaquetando la aplicación para la rama: ${currentBranch}..."
                    
                    if (isUnix()) {
                        sh "./mvnw clean package -DskipTests"
                    } else {
                        bat '.\\mvnw clean package -DskipTests'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo 'Ejecutando pruebas unitarias...'
                    if (isUnix()) {
                        sh "./mvnw test"
                    } else {
                        bat '.\\mvnw test'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def currentBranch = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '')
                    def imageName = "smash-order-app"
                    // Sanitizar el nombre de la rama para usar en tags de Docker (reemplazar / por -)
                    def branchTag = currentBranch.replaceAll('/', '-')
                    def imageTag = "${branchTag}-${env.BUILD_NUMBER}"
                    
                    echo "Construyendo imagen Docker: ${imageName}:${imageTag}..."
                    
                    try {
                        // Verificar que Docker esté disponible
                        if (isUnix()) {
                            sh 'docker --version'
                            
                            // Construir la imagen con docker build
                            sh "docker build -t ${imageName}:${imageTag} -f Dockerfile.app ."
                            
                            // También etiquetar con el nombre de la rama
                            sh "docker tag ${imageName}:${imageTag} ${imageName}:${branchTag}"
                        } else {
                            bat 'docker --version'
                            bat "docker build -t ${imageName}:${imageTag} -f Dockerfile.app ."
                            bat "docker tag ${imageName}:${imageTag} ${imageName}:${branchTag}"
                        }
                        
                        echo "✓ Imagen Docker construida exitosamente: ${imageName}:${imageTag}"
                    } catch (err) {
                        echo "✗ Error al construir imagen Docker: ${err}"
                        currentBuild.result = 'FAILURE'
                        error("Fallo en la construcción de imagen Docker")
                    }
                }
            }
        }

        stage('Deploy Info') {
            steps {
                script {
                    def currentBranch = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '')
                    def branchTag = currentBranch.replaceAll('/', '-')
                    
                    echo """
                    ========================================
                    BUILD COMPLETADO EXITOSAMENTE
                    ========================================
                    Rama: ${currentBranch}
                    Build: #${env.BUILD_NUMBER}
                    Imagen Docker: smash-order-app:${branchTag}-${env.BUILD_NUMBER}
                    ========================================
                    """
                    
                    // Información específica por rama
                    if (currentBranch == 'develop') {
                        echo 'Entorno: DESARROLLO'
                        echo 'Listo para desplegar en ambiente de desarrollo'
                    } else if (currentBranch == 'quality') {
                        echo 'Entorno: CALIDAD/QA'
                        echo 'Listo para desplegar en ambiente de pruebas'
                    } else if (currentBranch == 'main') {
                        echo 'Entorno: PRODUCCIÓN'
                        echo 'Listo para desplegar en ambiente de producción'
                    } else if (currentBranch.startsWith('dev/')) {
                        echo 'Entorno: DESARROLLO - Feature Branch'
                        echo 'Imagen lista para pruebas de desarrollo'
                    } else if (currentBranch.startsWith('qa/')) {
                        echo 'Entorno: QA - Testing Branch'
                        echo 'Imagen lista para pruebas de QA'
                    } else if (currentBranch.startsWith('release')) {
                        echo 'Entorno: RELEASE'
                        echo 'Imagen lista para release'
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✓ Pipeline ejecutado exitosamente'
        }
        failure {
            echo '✗ Pipeline falló'
        }
        always {
            echo 'Limpiando workspace...'
            cleanWs()
        }
    }
}
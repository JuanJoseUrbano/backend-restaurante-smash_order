pipeline {
    agent any

    tools {
        jdk 'JDK 21'
    }

    environment {
        JAVA_HOME = tool 'JDK 21'
    }

    // Configurar para ejecutar solo en ramas específicas
    options {
        // Mantener solo los últimos 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Timeout de 30 minutos para el pipeline
        timeout(time: 30, unit: 'MINUTES')
    }

    // Ejecutar solo en las ramas especificadas
    // Esta sección valida la rama antes de ejecutar cualquier stage
    stages {
        stage('Validate Branch') {
            steps {
                script {
                    def currentBranch = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '')
                    
                    echo "Rama actual: ${currentBranch}"
                    
                    // Ramas base permitidas
                    def allowedBranches = ['develop', 'main', 'quality']
                    
                    // Patrones de ramas permitidas
                    def allowedPatterns = [
                        ~/dev\/.*/,      // dev/HU-XXX, dev/feature, etc.
                        ~/qa\/.*/,       // qa/HU-XXX, qa/test, etc.
                        ~/release.*/     // release, release.0.0.1, etc.
                    ]
                    
                    // Validar si la rama está permitida
                    def isAllowed = allowedBranches.contains(currentBranch) || 
                                   allowedPatterns.any { pattern -> currentBranch ==~ pattern }
                    
                    if (!isAllowed) {
                        error("Pipeline solo se ejecuta en las ramas: ${allowedBranches.join(', ')} o patrones dev/*, qa/*, release*. Rama actual: ${currentBranch}")
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
                    def imageTag = "${currentBranch.replace('/', '-')}-${env.BUILD_NUMBER}"
                    
                    echo "Construyendo imagen Docker: ${imageName}:${imageTag}..."
                    
                    try {
                        // Usar Dockerfile.app que tiene multi-stage build
                        docker.build("${imageName}:${imageTag}", "-f Dockerfile.app .")
                        
                        // También etiquetar con el nombre de la rama (reemplazar / con -)
                        docker.build("${imageName}:${currentBranch.replace('/', '-')}", "-f Dockerfile.app .")
                        
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
                    
                    echo """
                    ========================================
                    BUILD COMPLETADO EXITOSAMENTE
                    ========================================
                    Rama: ${currentBranch}
                    Build: #${env.BUILD_NUMBER}
                    Imagen Docker: smash-order-app:${currentBranch.replace('/', '-')}-${env.BUILD_NUMBER}
                    ========================================
                    """
                    
                    // Información específica por rama
                    if (currentBranch.startsWith('dev/')) {
                        echo 'Entorno: DESARROLLO (Feature Branch)'
                        echo 'Rama de desarrollo de feature'
                    } else if (currentBranch.startsWith('qa/')) {
                        echo 'Entorno: CALIDAD (QA Branch)'
                        echo 'Rama de pruebas de feature'
                    } else if (currentBranch.startsWith('release')) {
                        echo 'Entorno: PRE-PRODUCCIÓN (Release Branch)'
                        echo 'Rama de release candidato'
                    } else {
                        switch(currentBranch) {
                            case 'develop':
                                echo 'Entorno: DESARROLLO'
                                echo 'Listo para desplegar en ambiente de desarrollo'
                                break
                            case 'quality':
                                echo 'Entorno: CALIDAD/QA'
                                echo 'Listo para desplegar en ambiente de pruebas'
                                break
                            case 'main':
                                echo 'Entorno: PRODUCCIÓN'
                                echo 'Listo para desplegar en ambiente de producción'
                                break
                        }
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
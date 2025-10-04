pipeline {
    agent any // Ejecutar en cualquier agente disponible

    stages {
        stage('Grant Execute Permission') {
            steps {
                script {
                    echo 'Asegurando permisos de ejecución para mvnw (si aplica)...'
                    // En agentes Linux/Unix concedemos permiso; en Windows usamos no-op
                    if (isUnix()) {
                        sh 'chmod +x ./mvnw || true'
                    } else {
                        bat 'echo Windows agent: skip chmod'
                    }
                }
            }
        }
        stage('Compile and Package') {
            steps {
                script {
                    echo 'Compilando y empaquetando la aplicación...'
                    // Elegir entre sh y bat según el sistema
                    if (isUnix()) {
                        sh './mvnw -B package'
                    } else {
                        bat '.\\mvnw -B package'
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Construyendo la imagen de Docker...'
                    // 'smash-order-app' es el nombre de la imagen
                    // 'env.BUILD_NUMBER' es una variable de Jenkins que nos da un número de compilación único
                    // Build solo si Docker está disponible en el agente
                    try {
                        docker.build("smash-order-app:${env.BUILD_NUMBER}", ".")
                    } catch (err) {
                        echo "Docker build skipped or failed: ${err}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
    }
    post {
        always {
            echo 'Pipeline finalizado.'
        }
    }
}

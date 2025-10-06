stage('Compile and Package') {
    steps {
        script {
            def currentBranch = env.BRANCH_NAME ?: env.GIT_BRANCH?.replaceAll('origin/', '')
            echo "Compilando y empaquetando la aplicación para la rama: ${currentBranch}..."
            
            if (isUnix()) {
                sh '''
                    echo "📦 Configurando JAVA_HOME..."
                    export JAVA_HOME=/opt/java/openjdk
                    export PATH=$JAVA_HOME/bin:$PATH
                    echo "JAVA_HOME: $JAVA_HOME"
                    java -version

                    echo "🚀 Ejecutando build con Maven..."
                    ./mvnw clean package -DskipTests
                '''
            } else {
                bat '''
                    set JAVA_HOME=C:\\Program Files\\Java\\jdk-21
                    set PATH=%JAVA_HOME%\\bin;%PATH%
                    .\\mvnw clean package -DskipTests
                '''
            }
        }
    }
}

stage('Run Tests') {
    steps {
        script {
            echo '🧪 Ejecutando pruebas unitarias...'
            if (isUnix()) {
                sh '''
                    export JAVA_HOME=/opt/java/openjdk
                    export PATH=$JAVA_HOME/bin:$PATH
                    ./mvnw test
                '''
            } else {
                bat '''
                    set JAVA_HOME=C:\\Program Files\\Java\\jdk-21
                    set PATH=%JAVA_HOME%\\bin;%PATH%
                    .\\mvnw test
                '''
            }
        }
    }
}

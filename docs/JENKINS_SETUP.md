# Configuración de Jenkins para SmashOrder

## Requisitos Previos

1. Jenkins instalado y corriendo (puerto 8080)
2. Docker instalado en el sistema donde corre Jenkins
3. Plugin de Docker instalado en Jenkins
4. Plugin de Git instalado en Jenkins
5. Plugin de Pipeline instalado en Jenkins

## Paso 1: Configurar Credenciales de GitHub

### 1.1 Crear Personal Access Token en GitHub

**IMPORTANTE**: Primero necesitas crear un Personal Access Token en GitHub con los permisos correctos.

📖 **Ver guía detallada**: [GITHUB_TOKEN_SETUP.md](GITHUB_TOKEN_SETUP.md)

**Resumen rápido de permisos necesarios:**

- ✅ **repo** (completo) - OBLIGATORIO
- ✅ **admin:repo_hook** (write y read) - Opcional pero recomendado para webhooks

### 1.2 Guardar el Token en Jenkins

1. En Jenkins, ir a: **Manage Jenkins** → **Manage Credentials**
2. Seleccionar el dominio global
3. Hacer clic en **Add Credentials**
4. Configurar:
   - **Kind**: Username with password
   - **Username**: Tu usuario de GitHub (ej: JuanJoseUrbano)
   - **Password**: Tu Personal Access Token de GitHub (el token que copiaste)
   - **ID**: `github-credentials` (o el nombre que prefieras)
   - **Description**: GitHub Token for SmashOrder
5. Hacer clic en **Create**

## Paso 2: Crear un Multibranch Pipeline Job

1. En el Dashboard de Jenkins, hacer clic en **New Item**
2. Ingresar el nombre del proyecto: `smash-order-backend`
3. Seleccionar **Multibranch Pipeline**
4. Hacer clic en **OK**

## Paso 3: Configurar el Branch Sources

En la configuración del job creado:

### 3.1 Branch Sources

1. Hacer clic en **Add source** → **Git**
2. Configurar:
   - **Project Repository**: `https://github.com/JuanJoseUrbano/backend-restaurante-smash_order.git`
   - **Credentials**: Seleccionar las credenciales creadas en el Paso 1

### 3.2 Behaviors

Agregar los siguientes behaviors:

1. **Discover branches**

   - Strategy: All branches

2. **Filter by name (with regular expression)**
   - Regular expression: `^(develop|main|quality)$`
   - Esto asegura que solo se ejecuten las ramas: develop, main y quality

### 3.3 Build Configuration

- **Mode**: by Jenkinsfile
- **Script Path**: `Jenkinsfile`

### 3.4 Scan Multibranch Pipeline Triggers

- Marcar: **Periodically if not otherwise run**
- **Interval**: 1 minute (o el que prefieras)

### 3.5 Orphaned Item Strategy

- **Days to keep old items**: 7
- **Max # of old items to keep**: 10

## Paso 4: Guardar y Escanear

1. Hacer clic en **Save**
2. Jenkins automáticamente escaneará el repositorio y detectará las ramas
3. Ejecutará el pipeline para cada rama detectada (develop, main, quality)

## Paso 5: Verificar Ejecución

1. Verás que Jenkins crea un sub-job para cada rama detectada
2. Cada rama tendrá su propio historial de builds
3. El pipeline validará la rama antes de ejecutarse

## Estructura del Pipeline

El Jenkinsfile actualizado incluye los siguientes stages:

1. **Validate Branch**: Valida que solo se ejecute en develop, main o quality
2. **Checkout**: Obtiene el código del repositorio
3. **Grant Execute Permission**: Da permisos a mvnw (Linux/Unix)
4. **Compile and Package**: Compila el proyecto con Maven
5. **Run Tests**: Ejecuta las pruebas unitarias
6. **Build Docker Image**: Construye la imagen Docker con tags específicos por rama
7. **Deploy Info**: Muestra información del build y entorno

## Tags de Imágenes Docker Generadas

Para cada build exitoso, se generan las siguientes imágenes:

- `smash-order-app:{rama}-{buildNumber}` (ej: `smash-order-app:develop-15`)
- `smash-order-app:{rama}` (ej: `smash-order-app:develop`) - siempre apunta al último build

## Ambientes por Rama

- **develop**: Ambiente de DESARROLLO
- **quality**: Ambiente de CALIDAD/QA
- **main**: Ambiente de PRODUCCIÓN

## Solución de Problemas Comunes

### Error: "Docker not found"

**Solución**: Asegúrate que Docker esté instalado en el servidor Jenkins y que el usuario Jenkins tenga permisos para usarlo.

```bash
# Dar permisos al usuario Jenkins
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Error: "Permission denied" con mvnw

**Solución**: El pipeline automáticamente intenta dar permisos. Si persiste:

```bash
# En el repositorio local
chmod +x mvnw
git add mvnw
git commit -m "Fix mvnw permissions"
git push
```

### Error: "Rama no autorizada"

**Solución**: El pipeline solo ejecuta en develop, main y quality. Si necesitas agregar otra rama, modifica el array `allowedBranches` en el Jenkinsfile.

### Error al conectar con GitHub

**Solución**:

1. Verifica que las credenciales sean correctas
2. Si usas 2FA, debes usar un Personal Access Token en lugar de la contraseña
3. El token debe tener permisos de `repo` completos

## Comandos Útiles

### Ver logs de Jenkins (si está en Docker)

```bash
docker logs -f jenkins
```

### Reiniciar Jenkins

```bash
docker restart jenkins
```

### Ver imágenes Docker generadas

```bash
docker images | grep smash-order-app
```

### Ejecutar la imagen Docker localmente

```bash
docker run -d -p 8080:8080 --name smash-order smash-order-app:develop
```

## Webhooks (Opcional)

Para que Jenkins ejecute automáticamente cuando haces push:

1. En GitHub, ir a: **Settings** → **Webhooks** → **Add webhook**
2. **Payload URL**: `http://tu-jenkins-url:8080/github-webhook/`
3. **Content type**: application/json
4. **Events**: Just the push event
5. Guardar

## Notas Importantes

- El pipeline limpia el workspace después de cada ejecución
- Los builds se mantienen por 10 ejecuciones
- Timeout del pipeline: 30 minutos
- Se usa `Dockerfile.app` para builds multi-stage optimizados
- Las pruebas deben pasar para que el build sea exitoso

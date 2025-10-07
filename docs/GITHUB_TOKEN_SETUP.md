# Cómo Crear un Personal Access Token de GitHub para Jenkins

## Paso 1: Acceder a la Configuración de Tokens

1. Inicia sesión en GitHub
2. Haz clic en tu foto de perfil (esquina superior derecha)
3. Selecciona **Settings**
4. En el menú lateral izquierdo, baja hasta **Developer settings**
5. Haz clic en **Personal access tokens**
6. Selecciona **Tokens (classic)**
7. Haz clic en **Generate new token** → **Generate new token (classic)**

## Paso 2: Configurar el Token

### Información Básica

- **Note**: `Jenkins - SmashOrder Backend` (o un nombre descriptivo)
- **Expiration**:
  - `90 days` (recomendado para producción)
  - `No expiration` (solo si es un ambiente de desarrollo/testing)

### Permisos Requeridos (Scopes)

Marca los siguientes scopes/permisos:

#### ✅ repo (OBLIGATORIO)

Marca el checkbox principal **repo** - esto automáticamente seleccionará todos los sub-permisos:

- ✓ `repo:status` - Acceso al estado del commit
- ✓ `repo_deployment` - Acceso a deployments
- ✓ `public_repo` - Acceso a repositorios públicos
- ✓ `repo:invite` - Acceso para aceptar invitaciones
- ✓ `security_events` - Acceso a eventos de seguridad

**¿Por qué se necesita?**

- Jenkins necesita clonar el repositorio
- Leer el código fuente
- Acceder a las ramas (develop, main, quality)
- Leer el Jenkinsfile

#### ✅ admin:repo_hook (OPCIONAL pero RECOMENDADO)

Si quieres que Jenkins se ejecute automáticamente cuando haces push:

- ✓ `write:repo_hook` - Crear webhooks
- ✓ `read:repo_hook` - Leer webhooks

**¿Por qué se necesita?**

- Para configurar webhooks automáticamente
- Jenkins puede detectar cambios en tiempo real
- Sin esto, deberás configurar el webhook manualmente

#### ❌ NO NECESITAS estos permisos:

- `workflow` - Solo si usas GitHub Actions
- `write:packages` - Solo si publicas paquetes
- `delete:packages` - Solo si eliminas paquetes
- `admin:org` - Solo si administras organizaciones
- `admin:public_key` - No necesario para Jenkins
- `admin:org_hook` - No necesario para Jenkins
- `gist` - No necesario para Jenkins
- `notifications` - No necesario para Jenkins
- `user` - No necesario para Jenkins
- `delete_repo` - Nunca dar este permiso a Jenkins

## Paso 3: Generar y Copiar el Token

1. Desplázate hacia abajo y haz clic en **Generate token**
2. **MUY IMPORTANTE**: Copia el token INMEDIATAMENTE
3. El token se muestra solo una vez - si lo pierdes, deberás crear uno nuevo
4. Formato del token: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

## Paso 4: Guardar el Token en Jenkins

1. Ve a Jenkins: **Manage Jenkins** → **Manage Credentials**
2. Selecciona el dominio **(global)**
3. Haz clic en **Add Credentials**
4. Configura:
   ```
   Kind: Username with password
   Scope: Global
   Username: tu-usuario-github (ej: JuanJoseUrbano)
   Password: [pega aquí el token copiado]
   ID: github-credentials
   Description: GitHub Token for SmashOrder
   ```
5. Haz clic en **Create**

## Resumen Visual de Permisos

```
┌─────────────────────────────────────┐
│ Personal Access Token Configuration │
├─────────────────────────────────────┤
│                                     │
│ ✅ repo                             │
│    ├── ✓ repo:status                │
│    ├── ✓ repo_deployment            │
│    ├── ✓ public_repo                │
│    ├── ✓ repo:invite                │
│    └── ✓ security_events            │
│                                     │
│ ✅ admin:repo_hook (opcional)       │
│    ├── ✓ write:repo_hook            │
│    └── ✓ read:repo_hook             │
│                                     │
└─────────────────────────────────────┘
```

## Verificar que Funciona

Después de configurar el token en Jenkins:

1. Crea el Multibranch Pipeline Job
2. Configura el repositorio: `https://github.com/JuanJoseUrbano/backend-restaurante-smash_order.git`
3. Selecciona las credenciales creadas
4. Haz clic en **Validate** (debe aparecer un check verde ✓)
5. Si aparece error, revisa:
   - Token copiado correctamente (sin espacios)
   - Permisos correctos seleccionados
   - Usuario correcto en las credenciales

## Solución de Problemas

### Error: "Authentication failed"

- ✗ Token incorrecto o expirado
- ✗ Username no coincide con el dueño del repositorio
- ✓ Genera un nuevo token y actualiza las credenciales

### Error: "Repository not found"

- ✗ URL del repositorio incorrecta
- ✗ Token sin permisos de `repo`
- ✓ Verifica la URL y los permisos del token

### Error: "Access denied"

- ✗ El usuario no tiene acceso al repositorio
- ✗ Token sin el scope `repo` completo
- ✓ Verifica que el usuario sea colaborador del repo

## Seguridad

### ✓ Buenas Prácticas:

- Usa tokens con fecha de expiración
- Un token por servicio (uno para Jenkins, otro para CI/CD, etc.)
- Guarda el token en un gestor de contraseñas
- Revoca tokens que no uses

### ✗ Nunca:

- Compartas el token públicamente
- Lo subas a GitHub en el código
- Le des más permisos de los necesarios
- Uses el mismo token para todo

## Renovación del Token

Cuando el token expire:

1. Genera un nuevo token con los mismos permisos
2. En Jenkins: **Manage Credentials**
3. Encuentra las credenciales de GitHub
4. Haz clic en **Update**
5. Reemplaza el password (token) con el nuevo
6. Guarda los cambios

## Referencia Rápida

**Permisos mínimos para Jenkins:**

```
✅ repo (completo)
```

**Permisos recomendados para Jenkins:**

```
✅ repo (completo)
✅ admin:repo_hook (para webhooks)
```

---

## Comandos para Verificar

### Verificar acceso desde línea de comandos:

```bash
# Reemplaza YOUR_TOKEN con tu token
curl -H "Authorization: token YOUR_TOKEN" \
  https://api.github.com/repos/JuanJoseUrbano/backend-restaurante-smash_order
```

Si obtienes información del repositorio, el token funciona correctamente.

# Dockerfile para la imagen personalizada de Kong
FROM kong:3.6

# Copia el archivo de configuración declarativa.
# Kong lo cargará automáticamente al iniciar.
COPY kong.yml /usr/local/kong/declarative/

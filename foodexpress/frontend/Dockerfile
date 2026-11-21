# Etapa 1: compilar la aplicación React con Node
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Etapa 2: servir los archivos estáticos con Nginx, que además hace de proxy hacia los servicios
FROM nginx:1.27-alpine
ENV DNS_RESOLVER=127.0.0.11 \
    RESTAURANTES_UPSTREAM=restaurantes-service:8081 \
    PEDIDOS_UPSTREAM=pedidos-service:8082 \
    NOTIFICACIONES_UPSTREAM=notificaciones-service:8083
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY nginx/proxy_comun.conf /etc/nginx/proxy_comun.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80

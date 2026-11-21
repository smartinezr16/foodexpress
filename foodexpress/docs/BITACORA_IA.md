# Bitácora de uso de inteligencia artificial

**Proyecto:** FoodExpress  
**Asignatura:** Diseño y Arquitectura de Software  
**Integrantes:** Sebastián Quintero, Santiago Martínez y Juan David Borda  
**Periodo:** Noviembre de 2026

## 1. Introducción

Durante el desarrollo de FoodExpress utilizamos herramientas de inteligencia artificial como apoyo en distintas etapas del proyecto, desde la planificación inicial hasta la implementación y documentación.

Decidimos trabajar con Claude para facilitar la construcción de componentes, explorar soluciones técnicas y detectar posibles errores. Como equipo nos encargamos de seleccionar el proyecto, definir las tecnologías, establecer el alcance y realizar las verificaciones que estaban a nuestro alcance.

Esta bitácora presenta cómo utilizamos la herramienta, qué resultados obtuvimos, cuáles fueron sus limitaciones y qué actividades requirieron intervención del equipo.

## 2. Herramienta utilizada

Utilizamos Claude, desarrollado por Anthropic, en un entorno de trabajo con acceso a archivos y terminal.

Las sesiones registradas corresponden al 15 y al 19 de noviembre de 2026. La herramienta se utilizó principalmente para apoyar el desarrollo de los microservicios, el frontend, las pruebas, la configuración de Docker y la documentación de arquitectura.

## 3. Registro de actividades

### Sesión 1. Selección del proyecto — 15 de noviembre

Comenzamos revisando las alternativas disponibles en el catálogo de la asignatura: Clínica Veterinaria, GymTrack, FoodExpress y EventHub.

Utilizamos Claude para comparar la complejidad técnica, los patrones de diseño y las funcionalidades de cada alternativa. Después de revisar la información, el equipo decidió desarrollar FoodExpress.

### Sesión 2. Definición de tecnologías — 15 de noviembre

Una vez seleccionado el proyecto, definimos las tecnologías y el alcance inicial.

Escogimos Java con Spring Boot para el backend y posteriormente React con TypeScript para el frontend. También decidimos priorizar los requerimientos principales y dejar algunas funcionalidades adicionales para una etapa posterior.

Claude ayudó a organizar la propuesta técnica a partir de estas decisiones.

### Sesión 3. Desarrollo del backend — 15 de noviembre

Utilizamos Claude para generar una primera implementación de los tres microservicios: Restaurantes, Pedidos y Notificaciones.

La solución incorporó Clean Architecture, los patrones Builder, State y Adapter, comunicación mediante RabbitMQ y persistencia con PostgreSQL.

También se generaron 66 pruebas automatizadas de dominio y casos de uso.

Durante esta etapa, la herramienta reportó limitaciones para ejecutar determinadas dependencias y servicios de infraestructura. Por esta razón, fue necesario ejecutar el proyecto en un entorno local.

El equipo realizó una ejecución mediante Docker Compose y comprobó el funcionamiento de la versión inicial.

### Sesión 4. Desarrollo del frontend — 19 de noviembre

Para la interfaz de usuario utilizamos React, TypeScript y Vite.

Se desarrollaron las pantallas de restaurantes, menú, carrito de compras, seguimiento de pedidos, historial y panel de cocina.

Claude realizó pruebas utilizando un backend simulado e identificó problemas relacionados con notificaciones duplicadas y visualización en dispositivos móviles.

Estas observaciones permitieron realizar correcciones antes de continuar con la integración.

### Sesión 5. Integración con Docker — 19 de noviembre

Se incorporó el frontend al entorno de Docker mediante una configuración multietapa con Node.js y Nginx.

También se configuró Nginx como proxy inverso para comunicar la interfaz con los tres microservicios.

Durante las verificaciones se detectó una directiva duplicada en la configuración de Nginx. El problema fue corregido y se volvió a comprobar la configuración.

La ejecución completa de la versión final de Docker Compose debe confirmarse en el entorno local del equipo.

### Sesión 6. Diagramas de arquitectura — 19 de noviembre

Utilizamos la herramienta para elaborar los diagramas C4 correspondientes al contexto, los contenedores y los componentes del sistema.

Los diagramas se generaron a partir de la arquitectura propuesta y se realizaron ajustes visuales para mejorar la claridad de las relaciones entre componentes.

También se incluyeron los archivos fuente para facilitar futuras modificaciones.

### Sesión 7. Preparación de la documentación — 19 de noviembre

Claude apoyó la organización de los requerimientos funcionales, la presentación del proyecto y el registro de las actividades realizadas con inteligencia artificial.

Esta etapa permitió reunir la información técnica en documentos que facilitaran explicar la solución durante la entrega final.

### Sesión 8. Revisión de los requerimientos de entrega — 19 de noviembre

En la última etapa utilizamos la guía de entrega de la asignatura para revisar la documentación y los componentes desarrollados.

Se realizaron ajustes en Docker Compose, los diagramas C4, los diagramas UML, los registros de decisiones arquitectónicas (ADR) y las reglas de negocio.

También se documentó una propuesta de trabajo con GitFlow para organizar el control de versiones.

Las pruebas automatizadas reportadas fueron 66 del backend y 6 del frontend. Sin embargo, la ejecución integral del Docker Compose actualizado quedó pendiente de confirmación local.

## 4. Participación del equipo

El desarrollo de FoodExpress combinó decisiones tomadas por los integrantes con el apoyo de herramientas de inteligencia artificial.

Entre las principales actividades realizadas por el equipo estuvieron la selección del proyecto, la elección de tecnologías, la definición del alcance y la ejecución inicial del sistema mediante Docker Compose.

Claude tuvo una participación importante en la generación del código, las pruebas y la documentación. Por ello, una parte fundamental del trabajo académico consiste en revisar los resultados, comprender la arquitectura propuesta y comprobar que las funcionalidades correspondan a los requerimientos establecidos.

**Registro de aportes individuales por completar:**

- **Sebastián Quintero:** especificar las actividades de implementación, revisión, pruebas o documentación realizadas.
- **Santiago Martínez:** especificar las actividades de implementación, revisión, pruebas o documentación realizadas.
- **Juan David Borda:** especificar las actividades de implementación, revisión, pruebas o documentación realizadas.

## 5. Dificultades y limitaciones

Durante el proceso encontramos varias situaciones que requirieron atención.

Una de las principales dificultades fue que el entorno de Claude no permitía ejecutar completamente algunos componentes del backend y de Docker. Esto hizo necesario diferenciar las pruebas automatizadas de las verificaciones realizadas sobre la aplicación completa.

También se identificaron errores en la configuración de Nginx, notificaciones duplicadas y problemas de visualización en pantallas pequeñas.

Otro aspecto importante fue que el proyecto se desarrolló inicialmente sin un repositorio de Git. Por esa razón, el historial de commits no representa todas las etapas de construcción y la estrategia GitFlow quedó planteada para el trabajo posterior.

Estas situaciones evidenciaron la importancia de realizar pruebas en el entorno real y mantener un registro de los cambios desde el comienzo.

## 6. Reflexión final

El desarrollo de FoodExpress nos permitió conocer mejor cómo se organiza una aplicación basada en microservicios y cómo se relacionan sus diferentes componentes.

El uso de inteligencia artificial facilitó la generación de código y documentación, pero también nos mostró que una solución generada automáticamente necesita verificaciones. No basta con que el código esté escrito: es necesario comprobar que funcione, que las tecnologías se comuniquen correctamente y que se cumplan las reglas de negocio.

Una de las principales lecciones del proyecto fue la importancia de organizar el trabajo desde el inicio, utilizar control de versiones y realizar pruebas de integración.

Como mejora para futuros proyectos, consideramos importante incorporar Git desde la primera etapa, distribuir las responsabilidades de forma más clara y documentar las decisiones técnicas a medida que se toman.

## 7. Conclusión

La inteligencia artificial fue una herramienta importante durante el desarrollo de FoodExpress, especialmente en la implementación técnica y la preparación de los entregables.

Su utilización permitió construir una solución con tres microservicios, una interfaz web y mecanismos de comunicación entre servicios.

El proyecto también puso de manifiesto la necesidad de complementar el trabajo automatizado con decisiones humanas, revisión técnica y pruebas de funcionamiento.

La experiencia nos permitió identificar tanto las ventajas como las limitaciones de incorporar inteligencia artificial en el desarrollo de software.

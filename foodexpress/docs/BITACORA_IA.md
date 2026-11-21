# Bitácora de uso de inteligencia artificial

**Proyecto:** FoodExpress  
**Asignatura:** Diseño y Arquitectura de Software  
**Integrantes:** Sebastián Quintero, Santiago Martínez y Juan David Borda

## 1. Introducción

Durante el desarrollo de FoodExpress utilizamos inteligencia artificial como una herramienta complementaria de consulta, aprendizaje y orientación técnica.

El proyecto se desarrolló a partir de los requerimientos de la asignatura y de las decisiones tomadas por nuestro equipo. A lo largo del proceso investigamos diferentes tecnologías, analizamos alternativas de arquitectura y trabajamos en la construcción e integración de los componentes necesarios para el funcionamiento del sistema.

La inteligencia artificial fue uno de los recursos utilizados para resolver inquietudes, comprender conceptos técnicos y consultar posibles soluciones ante las dificultades encontradas.

Su utilización no reemplazó la necesidad de comprender el funcionamiento del software, evaluar las alternativas disponibles y comprobar los resultados mediante pruebas.

## 2. Herramienta utilizada

La herramienta de inteligencia artificial utilizada fue **Claude, desarrollada por Anthropic**.

Recurrimos a ella principalmente como recurso de apoyo para consultar conceptos relacionados con arquitectura de software, patrones de diseño, microservicios y tecnologías utilizadas en el proyecto.

También la empleamos para revisar ejemplos de implementación, analizar mensajes de error, consultar buenas prácticas y recibir sugerencias técnicas.

Las respuestas obtenidas se utilizaron como referencias durante el desarrollo, teniendo en cuenta que debían ser comprendidas, evaluadas y contrastadas con los requerimientos y el comportamiento real de FoodExpress.

## 3. Aplicación de la inteligencia artificial durante el proyecto

### Selección y planificación

Durante la planificación analizamos las alternativas disponibles para desarrollar el proyecto y los requerimientos establecidos en la asignatura.

El equipo seleccionó FoodExpress y definió las funcionalidades principales que debía ofrecer la aplicación.

En esta etapa utilizamos Claude para aclarar dudas sobre las tecnologías que estábamos considerando, comprender sus características y consultar recomendaciones relacionadas con la organización del sistema.

Esta información sirvió como complemento para evaluar las opciones y orientar nuestras decisiones.

### Arquitectura del software

Uno de los aspectos más importantes del proyecto fue comprender cómo organizar una aplicación basada en microservicios y cómo aplicar los principios de Clean Architecture.

Para ello estudiamos la separación de responsabilidades, las dependencias entre componentes y la comunicación entre servicios.

La solución se organizó alrededor de tres servicios principales:

- Restaurantes y Menú.
- Pedidos.
- Notificaciones.

También trabajamos con los patrones de diseño Builder, State y Adapter, relacionando sus características con las necesidades del sistema.

Durante este proceso utilizamos Claude para consultar explicaciones, revisar ejemplos de código y resolver inquietudes sobre la aplicación de estos conceptos.

El objetivo de estas consultas fue fortalecer nuestra comprensión de las decisiones arquitectónicas y contar con referencias técnicas que pudiéramos evaluar durante el desarrollo.

### Desarrollo del backend

Para el backend trabajamos con Java y Spring Boot, organizando los componentes de acuerdo con las responsabilidades definidas para cada servicio.

Durante esta etapa abordamos aspectos relacionados con controladores, servicios, validaciones, persistencia de información y comunicación entre componentes.

Claude fue utilizado como apoyo para consultar documentación, comprender determinadas configuraciones, revisar ejemplos de implementación y analizar posibles causas de errores.

También consultamos recomendaciones sobre pruebas automatizadas, RabbitMQ y PostgreSQL.

Una parte importante del proceso consistió en ejecutar los servicios mediante Docker Compose en nuestro entorno local, revisar su comportamiento y comprobar la integración de los diferentes componentes.

Estas verificaciones fueron necesarias para identificar problemas que no podían resolverse únicamente mediante consultas a la herramienta.

### Desarrollo del frontend

La interfaz web de FoodExpress se trabajó utilizando React, TypeScript y Vite.

Durante esta etapa abordamos la organización de componentes visuales, la navegación entre pantallas y la presentación de información al usuario.

Entre las funcionalidades contempladas estuvieron la consulta de restaurantes, visualización de menús, carrito de compras, seguimiento de pedidos y panel de cocina.

Utilizamos Claude para aclarar dudas relacionadas con React y TypeScript, consultar alternativas de organización de componentes y analizar errores que aparecieron durante las pruebas.

También realizamos verificaciones utilizando un backend simulado, lo que permitió revisar el comportamiento de la interfaz e identificar aspectos que necesitaban ajustes.

Este proceso nos permitió comprender mejor la relación entre la interfaz de usuario y los servicios que proporcionan la información.

### Integración de tecnologías

La integración de las diferentes tecnologías representó uno de los principales desafíos técnicos del proyecto.

Fue necesario comprender la relación entre los servicios, la configuración de contenedores y los mecanismos utilizados para permitir la comunicación entre los componentes.

Durante esta etapa recurrimos a Claude para consultar conceptos relacionados con Docker, redes, variables de entorno y configuración de Nginx.

Las recomendaciones obtenidas nos sirvieron como referencia para investigar los problemas encontrados y evaluar posibles soluciones.

Las pruebas en el entorno local fueron fundamentales para comprobar qué configuraciones funcionaban correctamente y cuáles requerían modificaciones.

### Documentación y diagramas

La documentación fue utilizada para organizar y representar los aspectos técnicos del proyecto.

Trabajamos con conceptos relacionados con diagramas UML y C4, requerimientos funcionales, reglas de negocio y decisiones arquitectónicas.

Claude sirvió como recurso de consulta para aclarar la finalidad de los diferentes diagramas, revisar convenciones y recibir sugerencias sobre la organización de la información.

La documentación permitió relacionar los componentes de FoodExpress con los conceptos estudiados durante la asignatura y facilitar la comprensión general de la solución.

## 4. Participación del equipo

El equipo tuvo a su cargo la definición del proyecto, la selección de tecnologías, la organización de las funcionalidades y el seguimiento del proceso de desarrollo.

Durante la construcción de FoodExpress trabajamos en comprender la arquitectura propuesta, revisar la relación entre los componentes y comprobar el funcionamiento de las diferentes tecnologías.

Las consultas realizadas a Claude formaron parte de nuestro proceso de investigación y resolución de dificultades técnicas.

Las sugerencias de la herramienta no se consideraron automáticamente como soluciones definitivas. Fue necesario revisar su pertinencia, contrastarlas con los requerimientos y comprobar su funcionamiento.

La ejecución local del sistema, las pruebas de integración y la revisión de errores permitieron identificar situaciones que requerían análisis y ajustes adicionales.

De esta manera, el uso de inteligencia artificial se integró como un recurso complementario dentro de las actividades de desarrollo del equipo.

## 5. Dificultades y soluciones

Durante el desarrollo encontramos dificultades relacionadas principalmente con la configuración del entorno, la integración de tecnologías y la comunicación entre componentes.

Uno de los retos fue conseguir que los diferentes servicios funcionaran conjuntamente mediante Docker Compose.

Para abordar estas dificultades consultamos documentación técnica, revisamos mensajes de error y utilizamos Claude como una fuente adicional de orientación.

También identificamos situaciones relacionadas con la configuración de Nginx, notificaciones duplicadas y problemas de visualización en dispositivos móviles.

Las recomendaciones consultadas permitieron considerar diferentes alternativas, pero fue necesario realizar verificaciones y ajustes en el entorno de desarrollo para evaluar los resultados.

Otra dificultad fue la incorporación tardía del control de versiones. Esta experiencia nos permitió reconocer la importancia de organizar los cambios del proyecto desde sus primeras etapas y considerar una estrategia GitFlow para futuras modificaciones.

Estas situaciones demostraron que el desarrollo de software requiere análisis, pruebas y seguimiento continuo, independientemente de las herramientas de apoyo utilizadas.

## 6. Aprendizajes

El desarrollo de FoodExpress nos permitió profundizar en diferentes conceptos estudiados durante la asignatura.

Entre los principales aprendizajes estuvieron la organización de aplicaciones mediante microservicios, la separación de responsabilidades, la aplicación de patrones de diseño y la comunicación entre componentes.

También adquirimos mayor familiaridad con tecnologías como Java, Spring Boot, React, TypeScript, RabbitMQ, PostgreSQL y Docker.

El uso de Claude nos permitió consultar explicaciones adicionales y explorar diferentes maneras de abordar problemas técnicos.

Sin embargo, uno de los aprendizajes más importantes fue comprender que una respuesta generada por inteligencia artificial no garantiza que una solución sea correcta o apropiada para un proyecto.

Por esta razón, reconocimos la importancia de revisar las recomendaciones, comprender el código utilizado y realizar pruebas que permitieran comprobar el comportamiento del sistema.

## 7. Reflexión final

Consideramos que la inteligencia artificial fue una herramienta útil para complementar nuestro proceso de aprendizaje durante el desarrollo de FoodExpress.

Su principal valor estuvo en facilitar el acceso a explicaciones técnicas, ejemplos de referencia y alternativas que nos ayudaron a comprender mejor algunos de los desafíos encontrados.

La experiencia también nos permitió reconocer que utilizar inteligencia artificial de manera responsable implica mantener una actitud crítica frente a sus respuestas.

No todas las recomendaciones son aplicables a cualquier proyecto, por lo que resulta necesario analizar su contexto, revisar su pertinencia y comprobar los resultados.

FoodExpress representó una oportunidad para poner en práctica conocimientos de arquitectura de software y fortalecer nuestras habilidades para investigar, analizar problemas y trabajar con diferentes tecnologías.

## 8. Conclusión

El desarrollo de FoodExpress fue una experiencia académica en la que aplicamos conceptos de diseño y arquitectura de software mediante la construcción de una aplicación basada en microservicios.

Como equipo participamos en la definición del proyecto, la selección de tecnologías, la organización de sus componentes y las actividades de integración y verificación del sistema.

Durante este proceso utilizamos Claude como una herramienta complementaria para resolver dudas, consultar ejemplos técnicos, comprender conceptos y evaluar posibles soluciones.

La principal enseñanza fue reconocer que la inteligencia artificial puede contribuir al aprendizaje y facilitar determinadas actividades del desarrollo de software, pero su utilización debe acompañarse de comprensión técnica, análisis crítico y verificación de resultados.

En nuestro caso, la herramienta formó parte de los recursos de apoyo utilizados durante el proyecto, mientras que la responsabilidad académica sobre las decisiones, la comprensión de la solución y la presentación de FoodExpress correspondió al equipo.

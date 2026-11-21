# Bitácora de uso de inteligencia artificial

**Proyecto:** FoodExpress  
**Asignatura:** Diseño y Arquitectura de Software  
**Integrantes:** Sebastián Quintero, Santiago Martínez y Juan David Borda

## 1. Introducción

Durante la construcción de FoodExpress utilizamos inteligencia artificial como una herramienta de apoyo para orientar el desarrollo del proyecto, resolver dudas técnicas y evaluar diferentes alternativas de implementación.

Nuestro propósito al utilizarla fue contar con una fuente adicional de orientación que nos permitiera comprender mejor algunos conceptos de arquitectura de software y encontrar soluciones a las dificultades que surgieron durante el proceso.

La herramienta también participó en la generación de componentes de código, pruebas y documentación. Como equipo definimos el proyecto, las tecnologías y el alcance, y realizamos verificaciones de funcionamiento.

## 2. Herramienta utilizada

La herramienta utilizada fue Claude, desarrollada por Anthropic.

La empleamos para consultar conceptos técnicos, comparar alternativas de diseño, estructurar componentes, generar implementaciones y apoyar la revisión de errores.

Su utilización estuvo relacionada principalmente con la arquitectura de microservicios, los patrones de diseño, la comunicación entre servicios, las pruebas y la documentación técnica.

## 3. Aplicación de la inteligencia artificial durante el proyecto

### Selección y planificación

En la etapa inicial utilizamos la IA para comparar las alternativas disponibles y comprender los principales retos técnicos de cada una.

A partir de este análisis, el equipo seleccionó FoodExpress y definió el alcance de la solución.

También utilizamos la herramienta para evaluar tecnologías y organizar la estructura general del sistema.

### Arquitectura del software

La IA nos permitió explorar diferentes alternativas para organizar los componentes y comprender cómo aplicar Clean Architecture en una solución basada en microservicios.

Se trabajó con tres servicios principales:

- Restaurantes y Menú.
- Pedidos.
- Notificaciones.

También se abordó la aplicación de los patrones Builder, State y Adapter, relacionándolos con las necesidades del sistema.

La herramienta contribuyó tanto a la explicación de estas soluciones como a la generación de parte de su implementación.

### Desarrollo del backend

Durante la construcción del backend utilizamos Claude para apoyar la implementación con Java y Spring Boot.

La herramienta ayudó a estructurar los componentes, desarrollar funcionalidades, proponer validaciones y generar pruebas automatizadas.

También permitió explorar mecanismos de comunicación mediante RabbitMQ y persistencia con PostgreSQL.

El equipo realizó una ejecución local de la versión inicial utilizando Docker Compose, lo que permitió comprobar el funcionamiento del sistema fuera del entorno de la IA.

### Desarrollo del frontend

Para la interfaz web trabajamos con React, TypeScript y Vite.

La IA apoyó la construcción de componentes y la identificación de problemas relacionados con la navegación, la presentación de información y las notificaciones.

Se desarrollaron funcionalidades como la consulta de restaurantes, visualización de menús, carrito de compras, seguimiento de pedidos y panel de cocina.

Las pruebas realizadas con un backend simulado permitieron detectar problemas de visualización y comportamiento que fueron corregidos durante el desarrollo.

### Integración de tecnologías

Uno de los retos fue comprender cómo se relacionaban los diferentes servicios y cómo podían ejecutarse mediante Docker.

Utilizamos la IA para orientar la configuración de contenedores, redes, variables de entorno y comunicación entre componentes.

También sirvió para analizar errores de configuración, especialmente en Nginx, y proponer soluciones que posteriormente pudieran verificarse.

### Documentación y diagramas

La herramienta apoyó la elaboración de diagramas UML y C4, así como la organización de requerimientos funcionales, reglas de negocio y decisiones arquitectónicas.

Estos recursos facilitaron representar la estructura del sistema y relacionar las decisiones técnicas con los conceptos estudiados en la asignatura.

## 4. Participación del equipo

La participación del equipo comenzó con la elección del proyecto y la definición de las tecnologías y funcionalidades que se desarrollarían.

También establecimos las prioridades de implementación y utilizamos los requerimientos académicos como referencia para orientar el trabajo.

Durante el proceso combinamos las decisiones del equipo con las propuestas y los componentes generados mediante inteligencia artificial.

La ejecución local del sistema fue una actividad importante, ya que permitió comprobar aspectos que la herramienta no podía verificar completamente en su propio entorno.

Para completar el registro de participación individual, se deben incorporar las actividades específicas realizadas por cada integrante durante las revisiones, pruebas, correcciones y preparación de la entrega.

## 5. Dificultades y soluciones

Durante el desarrollo encontramos dificultades relacionadas con la integración de tecnologías, la configuración del entorno y la comunicación entre componentes.

Una de las principales limitaciones fue que la IA no podía ejecutar completamente todos los servicios en su entorno. Por ello, fue necesario utilizar Docker Compose en un equipo local.

También se identificaron problemas en la configuración de Nginx, notificaciones duplicadas y dificultades de visualización en dispositivos móviles.

La herramienta permitió analizar estos problemas y generar correcciones, lo que facilitó continuar con el desarrollo.

Otra dificultad fue la incorporación tardía del control de versiones. A partir de esta experiencia se planteó una estrategia GitFlow para organizar las siguientes modificaciones del proyecto.

## 6. Aprendizajes

El desarrollo de FoodExpress nos permitió acercarnos a conceptos de arquitectura de software mediante un proyecto que integraba diferentes tecnologías.

Entre los temas trabajados estuvieron la separación de responsabilidades, los patrones de diseño, la comunicación mediante eventos y la organización de una aplicación en microservicios.

También aprendimos que las respuestas de una herramienta de inteligencia artificial deben revisarse y contrastarse con el comportamiento real del sistema.

La experiencia mostró la importancia de complementar las herramientas de desarrollo con pruebas, documentación y comprensión de las decisiones técnicas.

## 7. Reflexión final

Consideramos que la inteligencia artificial fue un recurso útil durante el desarrollo de FoodExpress, ya que nos permitió consultar dudas, explorar soluciones y avanzar en la construcción del sistema.

Su apoyo no se limitó a proporcionar información: también contribuyó a la generación de código, pruebas y documentación que formaron parte del proyecto.

Al mismo tiempo, la experiencia nos permitió reconocer la importancia de definir correctamente los requerimientos, evaluar las alternativas propuestas y verificar los resultados obtenidos.

Como equipo, esta experiencia nos ayudó a comprender mejor el proceso de construcción de una aplicación basada en microservicios y la necesidad de utilizar las herramientas de inteligencia artificial con criterio técnico y responsabilidad.

## 8. Conclusión

FoodExpress fue una experiencia de aprendizaje en la que combinamos decisiones del equipo, tecnologías de desarrollo y herramientas de inteligencia artificial.

Claude aportó orientación técnica y colaboró en la elaboración de diferentes componentes del sistema. El equipo definió el alcance, seleccionó las tecnologías y realizó verificaciones locales.

La principal enseñanza fue comprender que la inteligencia artificial puede facilitar distintas actividades del desarrollo de software, pero sus resultados requieren revisión, pruebas y decisiones fundamentadas para construir una solución funcional.

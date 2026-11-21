#!/usr/bin/env bash
# Crea el repositorio Git de FoodExpress con las ramas de GitFlow y lo sube a GitHub.
#
#   bash scripts/iniciar-repositorio.sh https://github.com/<usuario>/foodexpress.git
#
# Lo ejecuta UN integrante, con su propia identidad de Git configurada.
set -euo pipefail

REMOTO="${1:?Indique la URL del repositorio vacío de GitHub}"

if [ -d .git ]; then
  echo "Esta carpeta ya es un repositorio Git; no se hace nada." >&2
  exit 1
fi
if [ -z "$(git config user.name || true)" ] || [ -z "$(git config user.email || true)" ]; then
  echo "Configure primero su identidad: git config --global user.name / user.email" >&2
  exit 1
fi
if [ -f .env ]; then
  echo "Aviso: existe un archivo .env; está en .gitignore y NO se subirá."
fi

git init -b main
git add .
git commit -m "chore: importar la base del proyecto FoodExpress" \
           -m "Tres microservicios, frontend, Docker Compose, diagramas C4 y UML, ADRs y documentación."
git tag -a v0.1.0 -m "Base inicial del proyecto"
git branch develop
git remote add origin "$REMOTO"
git push -u origin main --tags
git push -u origin develop

echo
echo "Listo: main y develop están en GitHub."
echo "Siguiente paso: proteger main y develop (Settings > Branches) y crear las ramas de docs/GITFLOW.md."

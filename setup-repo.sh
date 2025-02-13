#!/bin/bash

# Verifica se já é um repositório git
if [ ! -d .git ]; then
    echo "Inicializando repositório Git..."
    git init
fi

# Garante que a estrutura de diretórios existe
mkdir -p .github/workflows

# Verifica a branch atual
current_branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "none")

# Se não estiver na branch main ou master, cria e muda para main
if [ "$current_branch" != "main" ] && [ "$current_branch" != "master" ]; then
    echo "Criando e mudando para branch main..."
    git checkout -b main
fi

# Adiciona arquivos ao git
echo "Adicionando arquivos ao Git..."
git add .github/workflows/docker-build.yml
git add Dockerfile
git add docker-compose.yml
git add build-multiarch.sh
git add README.md

echo "
Repositório configurado com sucesso!

Próximos passos:
1. Configure seu repositório remoto:
   git remote add origin https://github.com/seu-usuario/seu-repo.git

2. Faça commit das alterações:
   git commit -m 'Configuração inicial do Metabase multi-plataforma'

3. Envie para o GitHub:
   git push -u origin main

4. Configure os secrets no GitHub:
   - DOCKER_USERNAME
   - DOCKER_PASSWORD (token do DockerHub)

5. Verifique a aba Actions no GitHub para monitorar o workflow
"

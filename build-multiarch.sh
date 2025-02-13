#!/bin/bash

# Criar e usar um novo builder que suporta multi-plataforma
docker buildx create --name multiarch --driver docker-container --use

# Build e push para as plataformas amd64 e arm64
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --tag metabase:latest \
  --push \
  .

# Remover o builder
docker buildx rm multiarch

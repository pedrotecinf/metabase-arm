# Metabase Multi-Plataforma teste

Versão modificada do Metabase com suporte a múltiplas arquiteturas (amd64/arm64).

## Configuração do CI/CD

### 1. Configurar Secrets no GitHub

Para habilitar o build e push automático para o DockerHub:

1. Acesse seu repositório no GitHub
2. Vá para `Settings > Secrets and variables > Actions`
3. Clique em "New repository secret"
4. Adicione os seguintes secrets:
   - `DOCKER_USERNAME`: Seu nome de usuário do DockerHub
   - `DOCKER_PASSWORD`: Seu token de acesso do DockerHub

### 2. Gerar Token do DockerHub

1. Acesse [DockerHub](https://hub.docker.com)
2. Vá para `Account Settings > Security`
3. Clique em "New Access Token"
4. Nomeie o token (ex: "GitHub Actions")
5. Copie o token gerado e salve como `DOCKER_PASSWORD` nos secrets do GitHub

## Estrutura do Projeto

```
.
├── .github/workflows/    # Configurações do CI/CD
├── Dockerfile           # Build multi-plataforma
├── docker-compose.yml   # Configuração de execução
└── build-multiarch.sh   # Script de build local
```

## Uso Local

Build manual:
```bash
./build-multiarch.sh
```

Executar localmente:
```bash
docker compose up -d
```

## CI/CD Pipeline

O pipeline é executado automaticamente quando:
- Push para branch main
- Pull Request para main

Etapas do pipeline:
1. Setup do ambiente multi-plataforma
2. Login no DockerHub
3. Build da imagem (amd64/arm64)
4. Testes básicos do container
5. Push para DockerHub (apenas em main)

## Tags Geradas

- `latest`: Última versão estável
- `sha-{commit}`: Versão específica do commit
- `{branch-name}`: Versão da branch
- `{version}`: Tag de versão semântica

## Monitoramento

- Builds: Aba "Actions" do GitHub
- Imagens: Seu repositório no DockerHub

## Volumes e Persistência

- `metabase-data`: Dados do Metabase
- `plugins`: Plugins adicionais

## Configuração da VPS

Após o push da imagem, na sua VPS:

```bash
# Atualizar imagem
docker compose pull

# Reiniciar serviço
docker compose up -d
```

O Metabase estará disponível em `http://seu-ip-vps:3000`

# Sorteio

Software dedicado à Associação dos Servidores Públicos de Joinville para gerenciar sorteios 

## Pré-Requisitos

* MySQL
* Maven
* Java11 (caso não for utilizar docker)

### Caso utilziar Docker
* Docker
* Docker-compose

## Variáveis de ambiente
### Dados de acesso ao banco de dados
* DB_URL
* DB_USERNAME
* DB_PASSWORD

## Execução
```bash 
# Gerando artefato
$ mvn clean install 
```

### Executando direto com java
```bash
# Navegue até a pasta onde foi gerado o artefato
$ cd target/

# Execute o jar
$ java -jar sorteio-1.0.0.jar
```

### Executando com docker
```bash
# Na pasta raiz do projeto, onde está o docker-compose

# Para subir o MySQL + Sorteio em modo "attached"
$ docker-compose up

# Para subir somente o MySQL (dettached)
$ docker-compose up -d aspmj-db

# Para subir somente o Sorteio (dettached)
$ docker-compose up -d aspmj-app

# Para parar os serviços sem excluir o container (importante para não perder os dados do mysql)
$ docker-compose down

# Para parar os serviços excluindo container, imagens, networks e volumes (isto irá excluir os dados do mysql)
$ docker-compose stop
```

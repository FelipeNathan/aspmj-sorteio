# Sorteio

Software dedicado à Associação dos Servidores Públicos de Joinville para gerenciar sorteios 

## Pré-Requisitos

* MySQL
* Maven
* Java11
* RabbitMQ

### Caso utilziar Docker
* Docker
* Docker-compose

## Variáveis de ambiente
### Dados de acesso ao banco de dados
* DB_URL
* DB_USERNAME
* DB_PASSWORD

### Url de conexão com RabbitMQ
* RABBITMQ_URL

## Execução
```bash 
# Gerando artefato
$ mvn clean install 
```

### Executando direto com java
Obs: Necessário ter MySQL já iniciado
```bash
# Navegue até a pasta onde foi gerado o artefato
$ cd target

# Execute o jar
$ java -jar sorteio-1.0.0.jar
```

### Executando com docker
Na pasta raiz do projeto onde está o docker-compose

Para primeira execução, inicialize o container do MySQL para as primeiras configurações do banco, volume, etc

```bash
$ docker-compose up -d aspmj-db
```

Caso já tenha inicializado uma vez, pode subir os 2 projetos juntos
```bash
# Na pasta raiz do projeto, onde está o docker-compose

# Para subir o MySQL + Sorteio + RabbitMQ em modo "attached"
$ docker-compose up

# Para subir somente o MySQL (dettached)
$ docker-compose up -d aspmj-db

# Para subir somente o Sorteio (dettached)
$ docker-compose up -d aspmj-app

# Para subir somente o RabbitMQ (dettached)
$ docker-compose up -d aspmj-rabbitmq

# Para parar os serviços sem excluir os containers
$ docker-compose stop

# Para parar os serviços excluindo containers e networks
$ docker-compose down
```

# API de Serviços para o RH

### Recursos utilizados

* Spring Boot
* Spring Security
* Banco de dados: H2
* Servidor: Tomcat (embarcado)
* Imagem Docker disponível: saviofreitas/challenge-ibyte 

### Endpoints

* /api/auhtenticate
* /api/departaments
* /api/people

### Autenticação

* Serviços providos via API Stateless autenticados via token (JWT)

### Documentação
Arquivos swagger.yml e swagger.json na raíz do projeto

### Execução
docker run -p 8000:8080 saviofreitas/challenge-ibyte:1.0.1

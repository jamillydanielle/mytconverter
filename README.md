# mytconverter

Projeto integrador para conversão de arquivos com suporte a diferentes formatos de mídia.

---

## 📚 Diagrama de classes

```mermaid
classDiagram
    class User {
        -int id
        -string username
        -string password
        +get_username()
        +User()
    }

    class Convertion {
        -string url
        -double file_size
        -int user_id
        +get_url()
        +Convertion()
    }

    class MP3 {
        -int bit_rate
    }

    class MP4 {
        -int video_resolution
    }

    User "1" o-- "many" Convertion : has
    Convertion <|-- MP3
    Convertion <|-- MP4

## Visão Geral da Arquitetura

O sistema é composto pelos seguintes microsserviços:

*   **`usermanagement` (Java):** Gerencia as operações relacionadas a usuários, como criação, autenticação e autorização.
*   **`convertion` (Java):** Responsável por criar e gerenciar as conversoes do youtube.
*   **`emailsender` (Java):** Envia e-mails.
*   **`gateway` (Node.js):** Atua como um ponto de entrada para o sistema, roteando requisições para os microsserviços apropriados.
*   **`db` (PostgreSQL):** Banco de dados relacional para armazenar dados persistentes.

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

*   **Docker/Podman:** Para executar os containers.  [https://www.docker.com/](https://www.docker.com/), para usuários de Mac, utilizar o Podman [https://podman.io/](https://podman.io/)
*   **Docker/Podman Compose:** Para gerenciar os containers.  Geralmente instalado junto com o Docker Desktop.
*   **Node.js e npm:** Para rodar o gateway (Node.js).  Recomendável usar uma versão LTS (Long Term Support).  [https://nodejs.org/](https://nodejs.org/)
*   **Java JDK:**  Para compilar e executar os microsserviços Java.  Recomendável JDK 17 ou superior.
*   **Python e Poetry:** Para gerenciar dependências e executar o serviço Python. [https://python-poetry.org/](https://python-poetry.org/)

## Configuração e Execução

Siga estes passos para configurar e rodar o projeto:

1.  **Clonar o Repositório:**

    ```bash
    git clone <URL_DO_SEU_REPOSITORIO>
    cd <NOME_DO_DIRETORIO_DO_REPOSITORIO>
    ```

2.  **Executar com Docker Compose:**

    O Docker Compose orquestra a construção e execução de todos os serviços.

    ```bash
    docker-compose up --build
    ```


    ou, para usuários Mac:

    ```bash
    podman compose up --build
    ```

    Este comando irá:
    * Construir as imagens Docker para cada microsserviço (Java, Python e Node.js).
    * Criar e iniciar os containers.
    * Configurar as redes para comunicação entre os serviços.

    Para executar em background (modo "detached"), use:

    ```bash
    docker-compose up --build -d
    ```

    ou, para usuários Mac:

    ```bash
    podman compose up --build -d
    ```

4.  **Acessando os Serviços:**

    *   **Frontend:** `http://localhost:3000`
    *   **PostgreSQL:** `localhost:2345`

## Configuração Adicional e Customização

*   **Variáveis de Ambiente:**

    Cada microsserviço tem suas próprias variáveis de ambiente configuradas no `docker-compose.yml`.  Você pode modificar essas variáveis para ajustar o comportamento da aplicação.  Alguns exemplos:

    *   `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASS`:  Configurações do banco de dados.
    *   `JWT_SECRET_KEY`, `JWT_ISSUER`: Configurações de autenticação JWT.
    *   `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`:  Configurações do servidor de e-mail.

*   **Portas:**

    As portas expostas pelos serviços podem ser alteradas no `docker-compose.yml`.  Por exemplo, para o gateway:

    ```yaml
    ports:
      - "3001:3001"
    ```

    A primeira porta (3001) é a porta na sua máquina host, e a segunda (3001) é a porta dentro do container.  Se você quiser acessar o gateway em uma porta diferente, altere a primeira porta.

*   **Para o Gateway (Node.js):**

    Dentro do diretório `node_backend/api_gateway`:

    *   `npm install`: Instala as dependências do Node.js.
    *   `npm run start:dev`: Inicia o servidor em modo de desenvolvimento com hot reloading.  Este comando está configurado no `docker-compose.yml` para o serviço `gateway`.

*   **Para os Microsserviços Java:**

    O Spring Boot Devtools está habilitado para hot reloading.  Certifique-se de que o volume esteja configurado corretamente no `docker-compose.yml`.


*   **Para o Frontend:**

    Dentro do diretório `frontend`:

    *   `npm install`: Instala as dependências do Next.js.
    *   `npm run dev`: Inicia o servidor em modo de desenvolvimento com hot reloading.

## Observações

*   **JWT Secret Key:** A chave secreta JWT usada no `docker-compose.yml` é apenas para fins de desenvolvimento.  Em um ambiente de produção, gere uma chave forte e segura.
*   **Dependências:**  Certifique-se de que as dependências de cada microsserviço estejam corretamente gerenciadas (Maven para Java, npm para Node.js).
*   **Logs:** Para visualizar os logs de um container, use o comando `docker logs <container_id>`. Você pode obter o ID do container com `docker ps`.

## Fluxo de Inicialização

1.  **Iniciar os Containers:**

    O primeiro passo é iniciar todos os containers definidos no `docker-compose.yml`.  Isso inclui o banco de dados, os microsserviços Java, o serviço Python, o gateway Node.js e o Mailhog.  Execute o seguinte comando na raiz do projeto (onde o `docker-compose.yml` está localizado):

    ```bash
    docker compose up --build
    ```

    ou, para usuários Mac:

    ```bash
    podman compose up --build
    ```

    Este comando irá construir as imagens Docker (se necessário) e iniciar os containers em modo "detached" (em segundo plano).  Se você quiser ver os logs em tempo real, remova a flag `-d`.

2.  **Iniciar o Frontend:**

    Após os containers estarem em execução, você precisa iniciar o servidor de desenvolvimento do frontend.  Navegue até o diretório `frontend` e execute:

    ```bash
    cd frontend
    npm install # Caso não tenha instalado as dependencias
    npm run dev
    ```

    Isso iniciará o servidor Next.js em modo de desenvolvimento com hot reloading. O servidor ficará disponível em `http://localhost:3000`.

3.  **Criar um Administrador:**

    Para criar um usuário administrador no sistema, você precisa executar um comando dentro do container `usermanagement`.  Primeiro, identifique o nome ou ID do container `usermanagement` usando `docker ps`.  Em seguida, execute o seguinte comando, substituindo `[nome do container ou id]` pelo valor correto:

    ```bash
    podman exec -it repositorio-mytconvert-usermanagement-1 bash
    java -jar usermanagement.jar create-admin "Administrador mytconvert" admin@example.br --server.port=0
    ```

4.  **Acessar e Configurar o Sistema:**

    1.  **Acesse o Frontend:** Abra seu navegador e acesse `http://localhost:3000`.

    2.  **Crie a Senha do Administrador:** Ao acessar o sistema pela primeira vez com a conta de administrador recém-criada (admin@example.com), você será solicitado a criar uma senha. Siga as instruções na tela.

    3.  **Faça Login como Administrador:** Use as credenciais do administrador (admin@example.com e a senha que você acabou de criar) para fazer login no sistema.

    4.  **Crie Usuários via Interface:** Depois de logado como administrador, use a interface do frontend para criar novos usuários com diferentes roles.
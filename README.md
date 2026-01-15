📋 TeamTasks – Task & Time Management API

TeamTasks é uma API REST para gestão de tarefas em equipes, com controle de acesso por organização, convites por token, 
autenticação JWT e controle de tempo por tarefa (timer).

Projeto desenvolvido com foco em boas práticas de backend, segurança e regras de negócio reais.

⸻

🚀 Funcionalidades

🔐 Autenticação & Segurança
	•	Registro e login de usuários
	•	Autenticação com JWT (access + refresh token)
	•	Refresh token com rotação e revogação
	•	Spring Security (stateless)

🏢 Organizações (Times)
	•	Criação de organizações
	•	Membership com roles:
	•	OWNER
	•	ADMIN
	•	MEMBER
	•	Controle de acesso por organização (multi-tenant)

✉️ Convites
	•	Convites por token seguro
	•	Definição de role no convite
	•	Alteração de role antes da aceitação
	•	Revogação de convite
	•	Aceitação de convite por usuário autenticado

📋 Tarefas
	•	CRUD de tarefas por organização
	•	Status: TODO , IN_PROGRESS , DONE
	•	Atribuição de tarefas a membros
	•	Reatribuição restrita a OWNER/ADMIN
	•	Detecção automática de tarefas atrasadas
	•	Filtros por status e período

⏱️ Timer por Tarefa
	•	Start / Pause / Stop
	•	Múltiplas pausas por tarefa
	•	Registro de sessões de tempo
	•	Soma automática do tempo total gasto
	•	Apenas o responsável pela tarefa pode controlar o timer

⸻

🧱 Arquitetura & Tecnologias
	•	Java 17
	•	Spring Boot
	•	Spring Security
	•	JWT (Access + Refresh Token)
	•	PostgreSQL
	•	Flyway (versionamento de banco)
	•	JPA / Hibernate
	•	Docker & Docker Compose
	•	Swagger / OpenAPI
	•	Maven

⸻

📦 Modelagem (Resumo)
	•	User
	•	Organization
	•	Membership
	•	OrgInvite
	•	Task
	•	TaskTimeEntry
	•	RefreshToken

Relacionamentos pensados para:
	•	multi-tenant
	•	segurança por organização
	•	regras de negócio realistas

⸻

🧪 Testes
	•	Testes manuais realizados via Insomnia
	•	Conferência de dados no Beekeeper Studio
	•	Fluxos testados:
	- Auth
	- Convites
	- Tasks
  - Timer
	- Permissões por role
  
⸻

  🐳 Rodando o projeto localmente

Pré-requisitos
	•	Java 17+
	•	Docker e Docker Compose

Subir o banco:

bash
docker compose up -d

Rodar a aplicação:

bash
./mvnw spring-boot:run


⸻

📄 Documentação da API

Swagger disponível em:

código:

http://localhost:8080/swagger-ui.html


⸻

🧠 Próximos Passos (Roadmap)
	•	Relatório de tempo por usuário
	•	Dashboard de produtividade
	•	Integração com frontend (Next.js)
	•	Testes automatizados
	•	CI/CD com GitHub Actions

⸻

👩‍💻 Autora

Yasmin Rodrigues
Desenvolvedora Java | Backend
📍 Brasil

Projeto criado com foco em aprendizado prático, boas práticas e evolução profissional.

⸻



	

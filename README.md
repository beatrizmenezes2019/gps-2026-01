# GPS 2026-01 — Gerador de Project Model Canvas via WhatsApp

Trabalho final da disciplina de **Gerência de Projetos de Software** — UFG, Engenharia de Software.

O sistema permite que um usuário converse com um assistente de IA pelo WhatsApp e, ao final da conversa, receba um PDF do **Project Model Canvas (PMC)** gerado automaticamente com as informações coletadas.

---

## Arquitetura

```
Usuário (WhatsApp)
       │
       ▼
    WAHA (API WhatsApp)
       │  webhook POST
       ▼
     n8n (orquestração)
       │
       ├─── AI Agent (Gemini) ◄─── Redis (memória de sessão)
       │         │ coleta 15 campos do PMC
       │         │ quando completo: CANVAS_PRONTO + JSON
       │
       ├─── Spring Boot API
       │         │ recebe JSON do canvas
       │         │ gera PDF com openhtmltopdf
       │         │ salva em volume Docker
       │         └─ retorna { downloadUrl, fileName }
       │
       └─── WAHA envia link do PDF ao usuário
```

---

## Componentes

### WAHA
Gerencia a conexão com o WhatsApp. Recebe mensagens e as encaminha via webhook para o n8n. Também é responsável por enviar as respostas ao usuário.

- Engine: `NOWEB`
- Porta: `3000`
- Configurado para disparar eventos do tipo `message`

### n8n
Orquestra todo o fluxo. O workflow (`workflow.json`) contém os seguintes nós principais:

| Nó | Tipo | Função |
|----|------|--------|
| Webhook | Webhook | Recebe mensagens do WAHA |
| Set Dados | Set | Extrai session, chatId, mensagem, fromMe |
| Ignorar mensagens próprias | If | Filtra mensagens enviadas pelo próprio bot |
| AI Agent Canvas | LangChain Agent | Conduz a conversa e coleta os 15 campos do PMC |
| Google Gemini Chat Model | LLM | Modelo de linguagem (gemini-2.0-flash) |
| Redis Chat Memory | Memory | Mantém histórico da conversa por sessão |
| Send Seen | WAHA | Marca a mensagem como lida |
| Canvas Pronto? | If | Verifica se o agente retornou `CANVAS_PRONTO` |
| Extrair JSON Canvas | Code | Faz parse do JSON emitido pelo agente |
| Gerar PDF Canvas | HTTP Request | Chama a API Spring Boot para gerar o PDF |
| Extrair Download URL | Set | Extrai o `downloadUrl` da resposta da API |
| Enviar Link PDF | WAHA | Envia o link de download ao usuário |
| Enviar Resposta Texto | WAHA | Envia respostas intermediárias do agente |

### Spring Boot (`canvas-pdf-generator`)
API REST em Java que recebe os dados do canvas em JSON, renderiza um template HTML com Thymeleaf e gera o PDF usando `openhtmltopdf`.

- Porta: `8080`
- Endpoint de geração: `POST /api/canvas/generate`
- Endpoint de download: `GET /api/canvas/download/{fileName}`
- PDFs armazenados em volume Docker: `/app/pdf-storage`

### Redis
Armazena o histórico de conversa de cada usuário. A chave de sessão é o `chatId` do WhatsApp, com TTL de 3600 segundos.

---

## Campos coletados pelo assistente (PMC)

O agente coleta os 15 campos do Project Model Canvas na seguinte ordem:

1. Código do projeto (sigla)
2. Nome completo do projeto
3. Justificativas
4. Objetivos
5. Benefícios
6. Produto
7. Requisitos (título + descrição)
8. Stakeholders
9. Equipe
10. Grupo de Entregas
11. Restrições
12. Premissas
13. Riscos
14. Linha do Tempo
15. Métricas de Sucesso

Quando todos os campos são confirmados, o agente emite `CANVAS_PRONTO` seguido do JSON completo, que dispara a geração do PDF.

---

## Estrutura do projeto

```
gps-2026-01/
├── docker-compose.yml          # Sobe todos os serviços
├── workflow.json               # Workflow do n8n (importar manualmente)
└── canvas-pdf-generator/       # API Spring Boot
    ├── Dockerfile
    ├── pom.xml
    └── src/main/
        ├── java/com/canvas/generator/
        │   ├── controller/CanvasController.java
        │   ├── model/CanvasData.java
        │   └── service/PdfGeneratorService.java
        └── resources/
            ├── application.properties
            └── templates/canvas.html
```

---

## Como executar

### Pré-requisitos
- Docker e Docker Compose instalados
- Conta no [Google AI Studio](https://aistudio.google.com/) para a chave da API Gemini
- n8n com o plugin da comunidade `n8n-nodes-waha` instalado

### 1. Configurar o IP público

Edite `canvas-pdf-generator/src/main/resources/application.properties` e defina o IP local da sua máquina (para que o link do PDF seja acessível pelo celular):

```properties
canvas.public.url=http://SEU_IP_LOCAL:8080
```

### 2. Subir os serviços

```bash
docker compose up -d --build
```

Isso sobe os serviços:
- `redis-gps` na porta `6379`
- `waha-gps` na porta `3000`
- `n8n-gps` na porta `5678`
- `canvas-pdf-gps` na porta `8080`

### 3. Configurar o WAHA

Acesse `http://localhost:3000` e conecte o WhatsApp escaneando o QR Code.

### 4. Importar o workflow no n8n

1. Acesse `http://localhost:5678`
2. Vá em **Workflows → Import from file**
3. Selecione o arquivo `workflow.json`
4. Configure as credenciais nos nós:
   - **WAHA**: URL `http://waha-gps:3000`, sem autenticação
   - **Google Gemini**: chave da API do Google AI Studio
   - **Redis**: host `redis-gps`, porta `6379`, senha `default`
5. Ative o workflow pelo toggle **Active**

### 5. Testar

Envie uma mensagem pelo WhatsApp para o número conectado. O assistente irá guiá-lo pela coleta dos dados e, ao final, enviará o link para download do PDF do Project Model Canvas.

---

## Observações técnicas

- O template HTML do canvas (`canvas.html`) usa layout em tabela HTML pura — o `openhtmltopdf` não suporta CSS Flexbox adequadamente.
- O PDF é gerado no formato A4 landscape.
- O WAHA na versão gratuita (Core) não suporta envio de arquivos — por isso o PDF é disponibilizado via link de download.
- O histórico de conversa é mantido por 1 hora (TTL Redis). Após esse período, uma nova conversa começa do zero.
- O n8n usa `host.docker.internal` para acessar o webhook a partir do container.

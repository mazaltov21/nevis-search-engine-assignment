# Nevis Search Engine Assignment

This service provides a simplified Search API for WealthTech platform to help advisors quickly search across **clients** and **documents**.

It supports:
- **Client search** by corporate email domain (e.g. `"Nevis Wealth"` → `user@neviswealth.com`)
- **Document semantic search** using embeddings + pgvector (e.g. `"address proof"` → documents containing `"utility bill"`)
- **Document summary**: AI-generated document summaries

---

## Setup Instructions

### Prerequisites
- Docker + Docker Compose installed
- An OpenAI API key

### 1) Create `.env` file (required for OpenAI features)
To enable embedding generation (semantic search) and document summaries, create a file named `.env` in the project root:

```
touch .env
```

### 2) Add your OpenAI key
You must provide your own OpenAI key. The service will not be able to call OpenAI without it.
```
OPENAI_API_KEY=your_personal_openai_api_key_here
```

### 3) Start the service
Run:
```
docker-compose up --build
```
This starts:

- database (Postgres + pgvector)

- application (Spring Boot)

### 4) Verify the service
Once the containers are up, the API is available at:
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## API Overview
### Create a client
```
POST /clients
```
Example:
```
curl -X POST "http://localhost:8080/clients" \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "Alice",
    "last_name": "Cooper",
    "email": "alice.cooper@neviswealth.com",
    "countryOfResidence": "UK"
  }'
```
### Create a document for a client
```
POST /clients/{id}/documents
```
Example:
```
curl -X POST "http://localhost:8080/clients/<CLIENT_UUID>/documents" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "monthly payment",
    "content": "utility bill for january 350 euro"
  }'
```
### Client search by company name / domain
```
GET /search?q=...
```
Example:
```
curl "http://localhost:8080/search?q=Nevis%20Wealth"
```
Example response:
```
[
  {
    "type": "client",
    "id": "bd55e1e1-a78a-4545-9e67-c8346708aa68",
    "firstName": "Alice",
    "lastName": "Cooper",
    "email": "alice.cooper@neviswealth.com",
    "countryOfResidence": "UK"
  }
]
```
### Semantic document search (LLM embeddings + pgvector)
```
GET /search?q=...
```
Example:
```
curl "http://localhost:8080/search?q=address%20proof"
```
Example response:
```
[
  {
    "type": "document",
    "id": "2bc9a89d-def4-4396-bb38-6e8cafc9b04f",
    "clientId": "bd55e1e1-a78a-4545-9e67-c8346708aa68",
    "title": "monthly payment",
    "content": "monthly payment\n\nutility bill for january 350 euro",
    "createdAt": "2025-12-08T22:37:23.477927219"
  }
]
```
Example semantic behavior:
- Searching "address proof" can return a document containing "utility bill"
- Searching "proof of residence" may match similar content even if keywords differ

### Document summary (LLM)
```
GET /documents/summary/{id}
```
Example:
```
curl "http://localhost:8080/documents/summary/2bc9a89d-def4-4396-bb38-6e8cafc9b04f"
```
Example response:
```
{
  "summary": "AI renerated response with summary of the document's content"
}
```

## Notes / Troubleshooting
If semantic search or summaries fail, confirm:

- .env exists in the project root

- OPENAI_API_KEY is set and valid

- containers were restarted after adding .env:
```
docker-compose down
docker-compose up --build
```
If you only want to test client domain search, you can run without OPENAI_API_KEY, but semantic search + summary endpoints may not work.
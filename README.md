# AgriBid Nexus — Backend

An auction agricultural procurement platform built around one core idea: **a farmer's crop-lot submission should be trusted only after it passes multiple independent evidence checks** — never a single opaque score. Everything else in the system (pricing, regional intelligence, reputation, dispute handling) builds on top of that trust layer.

Built for **GEOHACK 3.0**.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Configuration](#configuration)
- [Database & Migrations](#database--migrations)
- [Running the Application](#running-the-application)
- [API Overview](#api-overview)
- [Authentication](#authentication)
- [Admin Account Setup](#admin-account-setup)
- [Rate Limiting](#rate-limiting)
- [Known Limitations](#known-limitations)
- [License](#license)

---

## Overview

AgriBid Nexus lets farmers list verified crop lots for competitive reverse-auction bidding by distributors. Every submission passes through a **7-signal evidence engine** — field-location match, travel plausibility, duplicate detection, harvest-season logic, weather cross-check, a live randomly-generated liveness challenge, and GPS-track spatial coverage — before it becomes eligible for auction. Only evidence that clears this bar is allowed to influence anything else on the platform, including **AgriPulse**, the platform's regional price and pest-signal intelligence layer.

## Key Features

- **Reverse-auction bidding** with optimistic-locked concurrent bids
- **7-signal video evidence engine** with full, explainable reporting (never one hidden trust score)
- **Gemini-powered AI grading** of crop quality from submitted video
- **RAG-grounded reserve-price suggestions**, citing real ingested market documents
- **AgriPulse** — regional price benchmarks, pest/disease early-warning, supply outlook, all trust-filtered
- **FPO cooperative pooling** with race-free contributions and transparent payout splits
- **AI negotiation co-pilot** with real tool-calling (mandi tax, live MSP, warehouse capacity, routing)
- **Route optimization** (nearest-neighbor + 2-opt) across pooled crop lots
- **Simulated distributor wallet** with a real minimum-balance bid gate
- **WhatsApp & USSD accessibility channels** for farmers without reliable smartphone access
- **Push notification infrastructure** via the Expo Push API
- **Full audit trail, dispute handling, and reputation scoring**, all built from real persisted history

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1 |
| Language | Java |
| Security | Spring Security 7 + JWT (jjwt 0.13.0) + BCrypt (12 rounds) |
| Database | H2 (file-based, dev) / PostgreSQL (prod) via JPA/Hibernate |
| Migrations | Flyway |
| Vector Search / RAG | Qdrant + Spring AI 2.0 |
| AI Model | Google Gemini 3.6-flash (chat, vision, embeddings) |
| Rate Limiting | Bucket4j 8.19.0 |
| External Messaging | Twilio WhatsApp API |
| Push Notifications | Expo Push API |

## Project Structure


src/main/java/com/agribid/nexus/
├── ai/
│   ├── evidence/          # 7-signal evidence assessment engine
│   ├── vision/             # Gemini crop grading
│   ├── pricing/            # RAG-grounded reserve price advisor
│   ├── planning/           # Demand forecasting, crop recommendations
│   ├── negotiation/        # AI negotiation co-pilot (tool-calling)
│   ├── logistics/          # Route optimization
│   ├── regional/           # AgriPulse aggregation, reputation, analytics
│   ├── rag/                 # Document ingestion for RAG grounding
│   └── mcp/                 # MCP tool integration
├── config/                  # Security, CORS, Qdrant, JWT config
├── controller/               # REST controllers (~25 files)
├── domain/                   # JPA entities, grouped by bounded context
│   ├── user/ crop/ auction/ contract/ regional/ notification/ wallet/
├── dto/
│   ├── request/ response/ mapper/
├── exception/                # GlobalExceptionHandler + custom exceptions
├── integration/              # Honest external-dependency scaffolding
├── notification/             # ExpoPushNotificationService
├── repository/                # Spring Data JPA repositories
├── scheduler/                 # Scheduled jobs (auction auto-close)
├── security/                  # JwtAuthFilter, RateLimitingFilter, UserPrincipal
├── service/
│   └── impl/                  # Service implementations
├── util/                      # GeoUtils, GpsTrackCodec, FileStorageUtil
├── validation/
└── wallet/                    # WalletService (bid-gating logic)

src/main/resources/
├── db/migration/              # Flyway migrations V1–V20
├── prompts/                    # AI prompt templates
└── application*.properties


## Prerequisites

- Java 21+
- Maven
- Docker (for Qdrant)
- An internet connection (for Gemini API calls)

## Setup & Installation


# 1. Clone the repository
git clone <your-repo-url>
cd nexus

# 2. Start Qdrant (vector store for RAG)
docker run -d -p 6333:6333 -p 6334:6334 --name agribid-qdrant qdrant/qdrant

# 3. Build
mvn clean install


Key properties in application.properties:

properties
# JWT — the fallback value is for local development only;
# override JWT_SECRET as a real environment variable in production
agribid.jwt.secret=${JWT_SECRET:this-is-a-development-only-secret-replace-me-before-deploying-anywhere-real-0123456789}

# Database (H2, file-based)
spring.datasource.url=jdbc:h2:file:./data/agribid_nexus;AUTO_SERVER=TRUE

# Qdrant — use 127.0.0.1, not localhost (avoids an IPv6 resolution issue)
spring.ai.vectorstore.qdrant.host=127.0.0.1
spring.ai.vectorstore.qdrant.port=6334

# Gemini
spring.ai.google.genai.chat.options.model=gemini-3.6-flash

# CORS — add every origin your frontend is actually served from
agribid.cors.allowed-origins=http://localhost:3000,http://localhost:5500,http://127.0.0.1:5500

# Optional external integrations (unset = honest 501 responses, not fake success)
agribid.integrations.payment-gateway-api-key=
agribid.integrations.enam-api-key=
agribid.integrations.twilio-account-sid=


## Database & Migrations

Schema is managed entirely by **Flyway** — migrations run automatically on startup. Current migrations span **V1 through V20**, covering (in order): base schema → video/GPS evidence → FPO pooling → liveness challenge & human review → spatial coverage → AgriPulse regional signals → disputes → sustainability self-report → logistics tracking → WhatsApp linking → offline capture support → push notifications → distributor wallet.

Never edit an already-applied migration — always add a new `V{n}__description.sql` file.

## Running the Application

```bash
mvn spring-boot:run
```

On a clean startup you should see `Started AgriBid_NexusApplication` with no errors, followed by Tomcat starting on port `8080`.

H2 console (dev only): `http://localhost:8080/h2-console`

## API Overview

~90 REST endpoints across ~25 controllers, spanning: authentication, fields, crop lots & evidence, listings & bidding, contracts & fulfillment, FPO pooling, AgriPulse regional intelligence, planning, reputation, disputes, analytics & reporting, provenance, negotiation, route optimization, voice transcription, WhatsApp, USSD, push notifications, and the distributor wallet.

All endpoints are under `/api/v1/`. A small set (listing search, provenance lookup, platform metrics, the WhatsApp/USSD webhooks) are intentionally public — everything else requires a valid JWT.

## Authentication


POST /api/v1/auth/register   → { token, userId, email, role, kycVerified }
POST /api/v1/auth/login      → { token, userId, email, role, kycVerified }
GET  /api/v1/auth/me         → validates an existing token, used for session restore


Send the token on every subsequent request:

Authorization: Bearer <token>


Only `FARMER` and `DISTRIBUTOR` can self-register. `AGRONOMIST` and `ADMIN` accounts are provisioned directly in the database.

## Admin Account Setup

sql
DELETE FROM admin_profiles WHERE id IN (SELECT id FROM users WHERE email = 'your-admin-email');
DELETE FROM users WHERE email = 'your-admin-email';

INSERT INTO users (user_type, email, password_hash, role, kyc_verified, enabled, created_at)
VALUES ('ADMIN', 'your-admin-email', '<real-bcrypt-hash>', 'ADMIN', true, true, CURRENT_TIMESTAMP);

INSERT INTO admin_profiles (id) SELECT id FROM users WHERE email = 'your-admin-email';


Generate a real BCrypt hash (12 rounds) rather than inserting plaintext — e.g. via a short Python snippet using the `bcrypt` package.

## Rate Limiting

Token-bucket limiting (Bucket4j) is applied to: login attempts, video uploads, and the public WhatsApp/USSD webhooks — protecting against brute-force and API-cost abuse without requiring authentication on the public routes.

## Known Limitations

Stated directly, not hidden:

- **UPI escrow, e-NAM/Agmarknet, advance financing** — all need a real external account or business partnership this project doesn't have. Real, correctly-shaped integration points exist and return an honest `501 Not Implemented` rather than a fake success.
- **PM-KISAN/KCC eligibility** — no public API exists for this at all; the endpoint gives informational guidance only.
- **Real USSD short-code** — the full menu logic is real and tested against the standard telecom gateway contract; no actual carrier short-code is provisioned.
- **Push notification delivery** — the backend integration with Expo's Push API is real and complete; delivery to a specific device depends on that device running a real compiled build rather than the Expo Go development client.
- **Perceptual video hashing / forensic tamper detection** — named, real, unclosed gaps in the fraud-detection layer. Current duplicate detection is exact-match (SHA-256) only.

## License

Built for GEOHACK 3.0.

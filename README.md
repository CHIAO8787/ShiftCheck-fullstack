# ShiftCheck — 申し送り事項の抜け漏れ防止ツール

交班事項の対応状況、期限超過、引き継ぎ確認を管理する個人制作のフルスタック・デモです。

> 公式機能・公式連携ではありません。実際の利用者名、健康情報、施設の機密情報は入力しないでください。

## 技術
- Frontend: HTML / CSS / Vanilla JavaScript
- Backend: Java 17 / Spring Boot 3.4 / Spring Web
- API: REST / JSON
- Database: PostgreSQL 16 / Spring Data JPA
- Validation: Jakarta Bean Validation
- Test: JUnit 5 / Spring Boot Test / MockMvc
- Local DB: Docker Compose

## 起動
必要: JDK 17+, Maven 3.9+, Docker Desktop/Engine

```bash
docker compose up -d
mvn spring-boot:run
```
ブラウザで http://localhost:8080 を開きます。フロントエンドは Spring Boot から配信されます。

## テスト
```bash
mvn test
```
MockMvc の Web 層テストは PostgreSQL を起動せずに実行できます。

## 構成
```text
ShiftCheck-fullstack/
├── pom.xml
├── docker-compose.yml
├── database/schema.sql
└── src/
    ├── main/java/jp/shiftcheck/
    │   ├── ShiftCheckApplication.java
    │   ├── entity/HandoverItem.java
    │   ├── entity/HandoverStatus.java
    │   ├── entity/Priority.java
    │   ├── dto/HandoverItemRequest.java
    │   ├── dto/StatusUpdateRequest.java
    │   ├── dto/HandoverItemResponse.java
    │   ├── repository/HandoverItemRepository.java
    │   ├── service/HandoverItemService.java
    │   └── controller/HandoverItemController.java
    │       controller/ApiExceptionHandler.java
    ├── main/resources/application.yml
    ├── main/resources/static/{index.html,style.css,app.js}
    └── test/java/jp/shiftcheck/controller/HandoverItemControllerTest.java
```

## DB schema
`database/schema.sql` は手動確認用の DDL です。ローカル起動時は Hibernate の `ddl-auto: update` がテーブルを作成します。

| Column | Type | Description |
|---|---|---|
| id | BIGSERIAL | PK |
| title | VARCHAR(180) | 必須 |
| assignee | VARCHAR(80) | 担当者 |
| category | VARCHAR(40) | カテゴリ |
| due_at | TIMESTAMP | 期限 |
| priority | VARCHAR(10) | HIGH / MEDIUM / LOW |
| status | VARCHAR(10) | TODO / DOING / DONE |
| confirmed | BOOLEAN | 引き継ぎ確認 |
| created_at / updated_at | TIMESTAMP | 作成・更新日時 |

## REST API
Base: `http://localhost:8080/api/handover-items`

| Method | Path | Description |
|---|---|---|
| GET | `/api/handover-items` | 一覧。`status`, `priority`, `q` で絞り込み |
| GET | `/api/handover-items/{id}` | 詳細 |
| POST | `/api/handover-items` | 新規作成 |
| PATCH | `/api/handover-items/{id}` | status / confirmed 更新 |
| DELETE | `/api/handover-items/{id}` | 削除 |

例:
```bash
curl -X POST http://localhost:8080/api/handover-items -H "Content-Type: application/json" -d '{"title":"備品を確認する","assignee":"夜勤チーム","category":"物品確認","dueAt":"2030-01-01T09:00:00","priority":"HIGH"}'
curl -X PATCH http://localhost:8080/api/handover-items/1 -H "Content-Type: application/json" -d '{"status":"DONE"}'
curl "http://localhost:8080/api/handover-items?status=TODO&priority=HIGH&q=備品"
```

## API test cases
1. `GET /api/handover-items` → 200 + JSON array
2. POST valid title → 201 + ID
3. POST blank title → 400 validation error
4. GET existing ID → 200
5. PATCH `{"status":"DOING"}` → 200
6. PATCH `{"confirmed":true}` → 200
7. GET with `status=TODO` → TODO only
8. DELETE existing ID → 204
9. GET deleted ID → 404
10. invalid enum → 400

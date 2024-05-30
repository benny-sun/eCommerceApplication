### **專案簡介**

這個專案是通過完成一個電子商務應用程式，來展現在安全性和 DevOps 方面的技能，包括自動化測試及部署。  
從一個已完成陽春功能的範本開始，並為範本新增適當的 JWT 認證和授權控制，以確保 User 只能存取自己的數據，並且這些數據只能以安全的方式存取。

### **專案範本設置**

https://github.com/udacity/nd035-c4-Security-and-DevOps/blob/master/README.md

範本使用 Java 8、Spring Boot、Maven、Hibernate ORM 和 H2 Database 所編寫，  
將範本匯入 Intellij IDEA，自行刪除和新增必要的 Maven Dependencies 進行開發。

範本基本功能:   
<img width="300" src="https://github.com/benny-sun/eCommerceApplication/assets/22260295/d03eeea6-5146-46d0-9a77-e7aed41bb579"/>

### **專案結構**

1. Application 主程式: **`EcommerceApplication.java`**
    1. Controller Layer: 負責處理 HTTP Request and Response 以及部分商業邏輯
    2. Domain layer (Model): 包含 Domain Model 與 Persistence Layer
        1. **`model.persistence`** : Hibernate 用於持久化到 H2 資料庫的數據模型 **`Cart`**、**`Item`**、**`User`**、**`UserOrder`**。
        2. **`model.persistence.repositories`** : 操作每個 Entity 的 **`JpaRepository`** interface，負責資料持久化。
        3. **`model.requests`** : 從 JSON Request 轉換成的 Request DTO。
    3. Test (JUnit)
        1. 獨立的 in-memory database
        2. 避開 EcommerceApplication 所有使用到 log4j 的實際寫入
        3. Controller feature tests
        4. JWT feature tests
    4. Log (log4j)
        1. 記錄 User 註冊成功及失敗
        2. 記錄 User 下單成功及失敗
        3. 使用 log4j 是為了方便匯入 Splunk 解析數據
2. Infrastructure: **`docker/`**
    1. Jenkins pipeline 記錄在檔案 **`Jenkinsfile`**
    2. 運行 Jenkins 的 Dockerfile  **`JenkinsDockerfile`**
    3. 運行電子商務應用程式的 Dockerfile **`EcDockerfile`**

### **安全性實作**

1. 新增 Spring Boot Starter Security 依賴。
2. 新增 Java JWT 依賴。
3. 禁用 Spring Boot 自動配置的安全模組。
4. 實作以下：
    - **`JWTAuthenticationFilter`** : 處理 User 登入，配發 JWT
    - **`JWTAuthenticationVerficationFilter`** : 處理每個請求的 JWT 解碼與驗證
    - **`UserDetailsServiceImpl`** : 取得 User 資訊，如果 User 不存在，則拋出 UsernameNotFoundException
    - **`SecurityConstants`** : 一些安全相關常數，像是 JWT 到期時間、secret key
    - **`WebSecurityConfiguration`** : Spring Security 的基本設定，包括 JWT Filters、無須身分驗證的 endpoints

### **登入及授權**

可以使用 Spring 的預設 **`POST /login`** endpoint 進行登入，成功後 Response Header 會有 JWT **`Authorization: Bearer {JWT}`**。
其他請求需攜帶這個 JWT，否則返回 401 Unauthorized。

---

### Test Code Coverage
![test-code-coverage](https://github.com/benny-sun/eCommerceApplication/assets/22260295/6f0ff74a-3aff-458c-94c5-bdb83103f956)

### Splunk Data Analytics
![127 0 0 1_8000_en-US_app_search_ec_dashboard](https://github.com/benny-sun/eCommerceApplication/assets/22260295/81644c8f-43fb-4ffc-b840-f551781ed759)

### Jenkins Pipeline
![localhost_9000_job_ec-pipeline_10_flowGraphTable_](https://github.com/benny-sun/eCommerceApplication/assets/22260295/b748bcb1-cb32-4490-b813-416d10b6ee6a)

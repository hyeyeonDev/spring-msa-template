# spring-msa-template

Spring Cloud를 사용한 마이크로서비스 아키텍처 템플릿입니다.  
Docker, Jenkins, 멀티테넌시 지원 포함.

## 구성

- Eureka Server: 서비스 디스커버리
- Config Server: 설정 중앙화
- API Gateway: 요청 라우팅
- Multi Tenant Core
- Security Common
- Demo Service: 샘플 마이크로서비스 (멀티테넌시 지원)
- Docker: 컨테이너화
- Jenkins: CI/CD 파이프라인

## 멀티테넌시

- 요청 헤더 `X-Tenant-Id`로 테넌트 지정 (예: `tenant_1`, `tenant_2`).
- 테스트: `curl -H "X-Tenant-Id: tenant_1" http://localhost:8080/demo`

## 구동 순서
1️⃣ config-server → 2️⃣ discovery-server → 3️⃣ gateway-service → 4️⃣ user-service, raffle-service

## Docker

### 캐시정리

```bash
docker-compose down
docker-compose rm -f
docker system prune -f
./gradlew clean
```

### 빌드 및 실행

```bash
./gradlew clean :config-server:build :discovery-server:build gateway-service:build :user-service:build
docker-compose up --build

docker-compose build
docker-compose up -d
docker-compose logs -f
```

### 테스트 실행

```bash
rm -rf config-server/build/ discovery-server/build/ gateway-service/build/ user-service/build/ multi-tenant-core/build/ security-common/build
./gradlew :user-service:bootRun --args='--spring.profiles.active=local'  
```

## 문제

### docker-compose build 에러

docker-compose.yml 파일에서 network 설정 잘못됨.  
config-server 의 depends_on 주의!

### user-service 실행 에러

#### Parameter 0 of constructor in com.hy.multi_tenant_core.config.MultiTenancyConfig required a bean of type 'com.hy.multi_tenant_core.MultiTenancyProperties' that could not be found.

- 원인:  
  MultiTenancyProperties 빈 생성 실패.  
  MultiTenancyConfig의 생성자가 MultiTenancyProperties 빈을 주입받으려 했으나, Spring 컨텍스트에 해당 빈이 없음
- 해결방법:
  MultiTenancyProperties가 @ConfigurationProperties로 올바르게 바인딩되도록 @EnableConfigurationProperties를 추가합니다.
  MultitenancyConfig.java 파일에 @EnableConfigurationProperties(MultiTenancyProperties.class) 추가

#### Parameter 0 of constructor in com.hy.security_common.config.SecurityCommonConfig required a bean of type 'com.hy.security_common.jwt.JwtAuthenticationFilter' that could not be found.

- 원인:
  JwtAuthenticationFilter 빈 생성 실패.  
  SecurityCommonConfig 클래스의 생성자가 JwtAuthenticationFilter 빈을 @Autowired로 주입받으려 했으나, Spring 컨텍스트에 해당 빈이 없음.
- 해결방법:  
  JwtTokenProvider 의 Value("${jwt.validity}")가 application.yml에서 jwt.validity 속성을 찾지 못함.
  security의 yml 에 존재하면 될줄 알았지만 사용하는 서비스 (user-service)에 존재해야함.

### gateway-service 실행 에러
#### Caused by: org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/gateway-service/default": Read timed out
``` bash
> Task :gateway-service:compileJava UP-TO-DATE
> Task :gateway-service:processResources UP-TO-DATE
> Task :gateway-service:classes UP-TO-DATE
> Task :gateway-service:com.hy.gateway_service.GatewayServiceApplication.main()
... 
```
- 원인: 시간 초과 오류
  ```bash
  # 실행시 응답 없음
  curl http://localhost:8888/gateway-service/test
  lsof -i :8888
  kill -9 [PID]
  ```
  8888포트 확인 시 다중 연결로 일부 연결이 제대로 닫히지 않음.
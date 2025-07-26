# c99-nets-sso-spring

Spring security 설정 제공

## 의존성 요구사항

### 필수 외부 라이브러리

이 프로젝트는 **nets-nsso-agent-core-jakarta.jar** 라이브러리에 의존합니다. 이 라이브러리는 외부 JAR 파일로 제공되며, Maven Central Repository에서 사용할 수 없습니다.

#### 1. nets-nsso-agent-core-jakarta.jar

**Maven 의존성 설정**:
```xml
<dependency>
    <groupId>nets.nsso</groupId>
    <artifactId>agent-core-jakarta</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

**설치 방법**:

- **로컬 Maven Repository에 설치**:
   ```bash
   mvn install:install-file \
     -Dfile=libs/nets-nsso-agent-core-jakarta.jar \
     -DgroupId=nets.nsso \
     -DartifactId=agent-core-jakarta \
     -Dversion=1.0.0 \
     -Dpackaging=jar
   ```

2. **프로젝트 빌드 시 자동 설치**:
   ```bash
   mvn clean install
   ```
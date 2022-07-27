# OpenId Connect examples

## Spring Boot 2.5.x MVC Client

### Release Notes

#### 0.0.2-SNAPSHOT

Uses spring method
- spring-boot-starter-oauth2-client

### H2 database changes

Modify resources/db/data.sql to add your name and umd email

i.e.

INSERT INTO MEMBER ("Email", "Inactive", "Admin", "Name", "UMDUniqueId", "RoleId") VALUES ('yourEmail@utah.gov', 0, 1, 'Your Name', '', 4);

:thumbsup:
